package com.example.mimascota.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.databinding.ActivityCheckoutBinding
import com.example.mimascota.model.CartItem
import com.example.mimascota.model.DatosEnvio
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.util.TokenManager
import com.example.mimascota.viewModel.CheckoutViewModel
import com.example.mimascota.viewModel.CheckoutViewModelFactory
import com.example.mimascota.viewModel.SharedViewModel
import com.example.mimascota.viewModel.SharedViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutAdapter: CheckoutProductoAdapter

    private val FREE_SHIPPING_THRESHOLD = 20000.0
    private val SHIPPING_FEE = 2000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewModelFactory = SharedViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        val checkoutFactory = CheckoutViewModelFactory(TokenManager)
        checkoutViewModel = ViewModelProvider(this, checkoutFactory).get(CheckoutViewModel::class.java)

        setupRecyclerView()
        setupDropdowns()
        handleIntentExtras()
        setupObservers()
        setupValidation()

        binding.btnRealizarPedido.setOnClickListener {
            val nombre = binding.etNombreCompleto.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val telefono = binding.etTelefono.text?.toString()?.trim() ?: ""
            val direccion = binding.etDireccion.text?.toString()?.trim() ?: ""
            val region = binding.actvRegion.text.toString().trim()
            val comuna = binding.actvComuna.text.toString().trim()
            val codigoPostal = binding.etCodigoPostal.text?.toString()?.trim() ?: ""

            val datosEnvio = DatosEnvio(
                nombreCompleto = nombre,
                email = email,
                telefono = telefono,
                direccion = direccion,
                ciudad = comuna,
                region = region,
                codigoPostal = codigoPostal
            )

            val items = checkoutAdapter.currentList
            if (items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalConIva = items.sumOf { it.producto.price * it.cantidad }
            val shipping = calculateShipping(totalConIva)
            val finalTotal = totalConIva + shipping

            binding.progressBar.visibility = View.VISIBLE

            checkoutViewModel.crearOrden(items, datosEnvio, totalConIva, finalTotal)
        }

        val savedUser = TokenManager.getUsuario()
        savedUser?.let {
            binding.etNombreCompleto.setText(it.nombre)
            binding.etEmail.setText(it.email)
            binding.etTelefono.setText(it.telefono ?: "")
            binding.etDireccion.setText(it.direccion ?: "")
        }
    }

    private fun setupValidation() {
        binding.etTelefono.addTextChangedListener {
            checkoutViewModel.validarFormulario(
                telefono = it.toString(),
                direccion = binding.etDireccion.text.toString(),
                region = binding.actvRegion.text.toString(),
                comuna = binding.actvComuna.text.toString()
            )
        }
        binding.etDireccion.addTextChangedListener {
            checkoutViewModel.validarFormulario(
                telefono = binding.etTelefono.text.toString(),
                direccion = it.toString(),
                region = binding.actvRegion.text.toString(),
                comuna = binding.actvComuna.text.toString()
            )
        }
        binding.actvRegion.addTextChangedListener {
            checkoutViewModel.validarFormulario(
                telefono = binding.etTelefono.text.toString(),
                direccion = binding.etDireccion.text.toString(),
                region = it.toString(),
                comuna = binding.actvComuna.text.toString()
            )
        }
        binding.actvComuna.addTextChangedListener {
            checkoutViewModel.validarFormulario(
                telefono = binding.etTelefono.text.toString(),
                direccion = binding.etDireccion.text.toString(),
                region = binding.actvRegion.text.toString(),
                comuna = it.toString()
            )
        }
    }


    private fun setupDropdowns() {
        val regionComunaMap = mapOf(
            "Región Metropolitana" to listOf("Santiago", "Puente Alto", "Maipú", "La Florida", "Las Condes", "Providencia", "Ñuñoa"),
            "Región de Valparaíso" to listOf("Valparaíso", "Viña del Mar", "Quilpué", "Concón", "Villa Alemana", "San Antonio", "Los Andes"),
            "Región del Maule" to listOf("Talca", "Curicó", "Linares", "Constitución", "Molina", "San Javier"),
            "Región del Biobío" to listOf("Concepción", "Talcahuano", "Chillán", "Los Ángeles", "Coronel", "Hualpén"),
            "Región de La Araucanía" to listOf("Temuco", "Padre Las Casas", "Villarrica", "Angol", "Pucón")
        )

        val regiones = regionComunaMap.keys.toList()
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regiones)
        binding.actvRegion.setAdapter(regionAdapter)

        binding.actvRegion.setOnItemClickListener { parent, _, position, _ ->
            val selectedRegion = parent.getItemAtPosition(position) as String
            val comunas = regionComunaMap[selectedRegion] ?: emptyList()
            val comunaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunas)
            binding.actvComuna.setAdapter(comunaAdapter)
            binding.actvComuna.setText("", false)
        }
    }

    private fun handleIntentExtras() {
        try {
            val gson = Gson()
            val itemsJson = intent.getStringExtra("cart_items_json")
            if (!itemsJson.isNullOrBlank()) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val items: List<CartItem> = gson.fromJson(itemsJson, type)
                checkoutAdapter.submitList(items)
                calculateAndShowTotals(items)
                return
            }

            if (intent.hasExtra("cart_total")) {
                val totalConIva = intent.getDoubleExtra("cart_total", 0.0)
                calculateAndShowTotals(totalConIva)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutProductoAdapter()
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = checkoutAdapter
        }
    }

    private fun setupObservers() {
        viewModel.carrito.observe(this) { cartItems ->
            if (checkoutAdapter.currentList.isEmpty() && cartItems != null) {
                checkoutAdapter.submitList(cartItems)
                calculateAndShowTotals(cartItems)
            }
        }

        checkoutViewModel.ordenCreada.observe(this) { ordenResponse ->
            binding.progressBar.visibility = View.GONE
            ordenResponse?.let {
                val resultIntent = Intent()
                resultIntent.putExtra("compraExitosa", true)
                resultIntent.putExtra("NUMERO_ORDEN", it.numeroOrden)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        checkoutViewModel.navegarARechazo.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, CompraRechazadaActivity::class.java)
                intent.putExtra("tipoError", it)
                startActivity(intent)
            }
        }

        checkoutViewModel.error.observe(this) { err ->
            err?.let {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                checkoutViewModel.limpiarError()
            }
        }

        checkoutViewModel.telefonoError.observe(this) {
            binding.tilTelefono.error = it
        }

        checkoutViewModel.direccionError.observe(this) {
            binding.tilDireccion.error = it
        }

        checkoutViewModel.regionError.observe(this) {
            binding.tilRegion.error = it
        }

        checkoutViewModel.comunaError.observe(this) {
            binding.tilComuna.error = it
        }

        checkoutViewModel.isFormValid.observe(this) {
            binding.btnRealizarPedido.isEnabled = it
        }
    }

    private fun calculateAndShowTotals(cartItems: List<CartItem>) {
        val totalConIva = cartItems.sumOf { it.producto.price * it.cantidad }
        calculateAndShowTotals(totalConIva)
    }

    private fun calculateAndShowTotals(totalConIva: Double) {
        val subtotal = CurrencyUtils.getBasePrice(totalConIva)
        val iva = totalConIva - subtotal
        val shipping = calculateShipping(totalConIva)
        val finalTotal = totalConIva + shipping

        binding.tvSubtotal.text = formatClp(subtotal)
        binding.tvIva.text = formatClp(iva)
        binding.tvShipping.text = formatClp(shipping)
        binding.tvTotal.text = formatClp(finalTotal)
    }

    private fun calculateShipping(totalConIva: Double): Double {
        return if (totalConIva < FREE_SHIPPING_THRESHOLD) SHIPPING_FEE else 0.0
    }

    private fun formatClp(amount: Double): String {
        return try {
            val fmt = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            fmt.maximumFractionDigits = 0
            fmt.format(amount)
        } catch (e: Exception) {
            String.format(Locale.getDefault(), "$%.0f", amount)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
