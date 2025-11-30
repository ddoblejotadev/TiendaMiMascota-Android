package com.example.mimascota.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.databinding.ActivityCheckoutBinding
import com.example.mimascota.model.CartItem
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
import com.example.mimascota.viewModel.SharedViewModel
import com.example.mimascota.viewModel.SharedViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: SharedViewModel
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

        setupRecyclerView()
        // Si vinieron items por extras, mostrarlos directamente
        handleIntentExtras()
        setupObservers()

        // Configurar botón realizar pedido (placeholder, no cambia comportamiento existente)
        binding.btnRealizarPedido.setOnClickListener {
            // Por ahora sólo mostrar un progress y delegar acción real al ViewModel/Repo existente
            binding.progressBar.visibility = android.view.View.VISIBLE
            // Aquí podrías iniciar la creación de la orden real
            // Simular breve delay o delegar al CheckoutViewModel si existe
            binding.progressBar.postDelayed({ binding.progressBar.visibility = android.view.View.GONE }, 800)
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
            // Algunos locales pueden mostrar "CLP$", si prefieres sólo "$" puedes reemplazar
            fmt.format(amount)
        } catch (e: Exception) {
            // Fallback simple
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
