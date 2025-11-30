package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mimascota.databinding.ItemCheckoutProductoBinding
import com.example.mimascota.model.CartItem

class CheckoutProductoAdapter : ListAdapter<CartItem, CheckoutProductoAdapter.CheckoutViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CheckoutViewHolder(private val binding: ItemCheckoutProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.tvNombreProducto.text = cartItem.producto.name
            binding.tvCantidad.text = "Cantidad: ${cartItem.cantidad}"
            val subtotal = cartItem.producto.price * cartItem.cantidad
            binding.tvSubtotal.text = String.format("$%.2f", subtotal)
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.producto.producto_id == newItem.producto.producto_id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
