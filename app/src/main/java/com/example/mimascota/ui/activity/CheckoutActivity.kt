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

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var checkoutAdapter: CheckoutProductoAdapter

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
    }

    private fun handleIntentExtras() {
        try {
            val gson = Gson()
            val itemsJson = intent.getStringExtra("cart_items_json")
            if (!itemsJson.isNullOrBlank()) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val items: List<CartItem> = gson.fromJson(itemsJson, type)
                checkoutAdapter.submitList(items)
                updateTotal(items)
            } else {
                // Si no vienen items, el ViewModel se encargará de poblar desde su repo
            }

            // Si también se pasa el total, usarlo (evita recalcular en la UI)
            if (intent.hasExtra("cart_total")) {
                val total = intent.getDoubleExtra("cart_total", 0.0)
                binding.tvTotal.text = String.format("Total: $%.2f", total)
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
                updateTotal(cartItems)
            }
        }
    }

    private fun updateTotal(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { it.producto.price * it.cantidad }
        binding.tvTotal.text = String.format("Total: $%.2f", total)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
