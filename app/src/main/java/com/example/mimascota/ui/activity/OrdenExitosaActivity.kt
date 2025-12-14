package com.example.mimascota.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.MainActivity
import com.example.mimascota.databinding.ActivityOrdenExitosaBinding
import com.example.mimascota.model.CartItem
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
import com.example.mimascota.util.CurrencyUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrdenExitosaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdenExitosaBinding
    private lateinit var checkoutAdapter: CheckoutProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdenExitosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        handleIntentExtras()
        setupListeners()
    }

    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutProductoAdapter()
        binding.rvProductosComprados.apply {
            layoutManager = LinearLayoutManager(this@OrdenExitosaActivity)
            adapter = checkoutAdapter
        }
    }

    private fun handleIntentExtras() {
        val numeroOrden = intent.getStringExtra("NUMERO_ORDEN") ?: "N/A"
        val subtotal = intent.getDoubleExtra("SUBTOTAL", 0.0)
        val iva = intent.getDoubleExtra("IVA", 0.0)
        val shipping = intent.getDoubleExtra("SHIPPING", 0.0)
        val total = intent.getDoubleExtra("TOTAL", 0.0)
        val itemsJson = intent.getStringExtra("CART_ITEMS_JSON")

        binding.tvNumeroOrden.text = "Número de orden: #$numeroOrden"
        binding.tvSubtotal.text = CurrencyUtils.formatAsCLP(subtotal)
        binding.tvIva.text = CurrencyUtils.formatAsCLP(iva)
        binding.tvShipping.text = CurrencyUtils.formatAsCLP(shipping)
        binding.tvTotalPagado.text = CurrencyUtils.formatAsCLP(total)

        if (!itemsJson.isNullOrBlank()) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            val items: List<CartItem> = Gson().fromJson(itemsJson, type)
            checkoutAdapter.submitList(items)
        }
    }

    private fun setupListeners() {
        binding.btnVerMisPedidos.setOnClickListener {
            val intent = Intent(this, MisPedidosActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("NAVIGATE_TO", "Catalogo")
                putExtra("CLEAR_CART", true)
            }
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("NAVIGATE_TO", "Catalogo")
            putExtra("CLEAR_CART", true)
        }
        startActivity(intent)
        super.onBackPressed()
        finish()
    }
}