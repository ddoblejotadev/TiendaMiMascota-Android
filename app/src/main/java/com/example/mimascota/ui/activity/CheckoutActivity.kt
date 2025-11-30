package com.example.mimascota.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.databinding.ActivityCheckoutBinding
import com.example.mimascota.model.CartItem
import com.example.mimascota.model.DatosEnvio
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
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

    // Política de envío
    private val FREE_SHIPPING_THRESHOLD = 20000.0
    private val SHIPPING_FEE = 2000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar toolbar para que el botón de atrás esté en posición accesible
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewModelFactory = SharedViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        // Nuevo: CheckoutViewModel con TokenManager
        val checkoutFactory = CheckoutViewModelFactory(com.example.mimascota.util.TokenManager)
        checkoutViewModel = ViewModelProvider(this, checkoutFactory).get(CheckoutViewModel::class.java)

        setupRecyclerView()
        // Si vinieron items por extras, mostrarlos directamente
        handleIntentExtras()
        setupObservers()

        // Configurar botón realizar pedido: validar campos y llamar al ViewModel para crear la orden
        binding.btnRealizarPedido.setOnClickListener {
            // Recolectar datos de envío del formulario
            val nombre = binding.etNombreCompleto.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val telefono = binding.etTelefono.text?.toString()?.trim() ?: ""
            val direccion = binding.etDireccion.text?.toString()?.trim() ?: ""
            val ciudad = binding.etCiudad.text?.toString()?.trim() ?: ""
            val region = binding.etRegion.text?.toString()?.trim() ?: ""
            val codigoPostal = binding.etCodigoPostal.text?.toString()?.trim() ?: ""

            // Validaciones mínimas
            if (nombre.isEmpty() || email.isEmpty() || direccion.isEmpty() || ciudad.isEmpty()) {
                Toast.makeText(this, "Por favor completa los datos de envío (nombre, email, dirección, ciudad)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val datosEnvio = DatosEnvio(
                nombreCompleto = nombre,
                email = email,
                telefono = telefono,
                direccion = direccion,
                ciudad = ciudad,
                region = region,
                codigoPostal = codigoPostal
            )

            // Obtener items desde adapter o ViewModel
            val items = checkoutAdapter.currentList
            if (items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val subtotal = items.sumOf { it.producto.price * it.cantidad }
            val shipping = calculateShipping(subtotal)
            val total = subtotal + shipping

            // Mostrar progress
            binding.progressBar.visibility = View.VISIBLE

            // Llamar al CheckoutViewModel para crear la orden
            checkoutViewModel.crearOrden(items, datosEnvio, subtotal, total)
        }

        // Add: prefill address fields from saved user
        val savedUser = com.example.mimascota.util.TokenManager.getUsuario()
        savedUser?.let {
            binding.etNombreCompleto.setText(it.nombre)
            binding.etEmail.setText(it.email)
            binding.etTelefono.setText(it.telefono ?: "")
            // If backend stores direccion, it will populate; otherwise leave blank
            binding.etDireccion.setText(it.direccion ?: "")
        }

        // Allow user to edit address via ProfileEditActivity
        binding.etDireccion.setOnClickListener {
            val intent = Intent(this, com.example.mimascota.ui.activity.ProfileEditActivity::class.java)
            startActivity(intent)
        }

        // Observadores para navegar en base al resultado
        checkoutViewModel.ordenCreada.observe(this) { ordenResponse ->
            if (ordenResponse != null) {
                // Orden creada; navegar a pantalla Exitosa
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, com.example.mimascota.ui.activity.OrdenExitosaActivity::class.java).apply {
                    putExtra("NUMERO_ORDEN", ordenResponse.numeroOrden)
                    putExtra("TOTAL", ordenResponse.total.toInt())
                    putExtra("ORDEN_ID", ordenResponse.id.toInt())
                }
                startActivity(intent)
                finish()
            }
        }

        checkoutViewModel.navegarARechazo.observe(this) { event ->
            val tipo = event.getContentIfNotHandled()
            if (tipo != null) {
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, com.example.mimascota.ui.activity.CompraRechazadaActivity::class.java)
                intent.putExtra("tipoError", tipo)
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

            // Si no vienen items, si viene el total como extra usarlo como subtotal
            if (intent.hasExtra("cart_total")) {
                val subtotal = intent.getDoubleExtra("cart_total", 0.0)
                val shipping = calculateShipping(subtotal)
                val finalTotal = subtotal + shipping
                binding.tvSubtotal.text = formatClp(subtotal)
                binding.tvShipping.text = formatClp(shipping)
                binding.tvTotal.text = formatClp(finalTotal)
            }
        } catch (e: Exception) {
            // Fallback: no romper si algo falla
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
            // Si no hay items ya cargados desde extras, poblar desde el ViewModel
            if (checkoutAdapter.currentList.isEmpty() && cartItems != null) {
                checkoutAdapter.submitList(cartItems)
                calculateAndShowTotals(cartItems)
            }
        }
    }

    private fun calculateAndShowTotals(cartItems: List<CartItem>) {
        val subtotal = cartItems.sumOf { it.producto.price * it.cantidad }
        val shipping = calculateShipping(subtotal)
        val finalTotal = subtotal + shipping
        binding.tvSubtotal.text = formatClp(subtotal)
        binding.tvShipping.text = formatClp(shipping)
        binding.tvTotal.text = formatClp(finalTotal)
    }

    private fun calculateShipping(subtotal: Double): Double {
        return if (subtotal < FREE_SHIPPING_THRESHOLD) SHIPPING_FEE else 0.0
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
