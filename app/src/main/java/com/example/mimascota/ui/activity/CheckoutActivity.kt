package com.example.mimascota.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mimascota.databinding.ActivityCheckoutBinding
import com.example.mimascota.model.CartItem
import com.example.mimascota.ui.adapter.CheckoutProductoAdapter
import com.example.mimascota.viewModel.SharedViewModel
import com.example.mimascota.viewModel.SharedViewModelFactory

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var checkoutAdapter: CheckoutProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = SharedViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        setupRecyclerView()
        setupObservers()
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
            checkoutAdapter.submitList(cartItems)
            if (cartItems != null) {
                updateTotal(cartItems)
            }
        }
    }

    private fun updateTotal(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { it.producto.price * it.cantidad }
        binding.tvTotal.text = String.format("Total: $%.2f", total)
    }
}
