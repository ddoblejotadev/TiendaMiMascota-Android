package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mimascota.Model.CartItem
import com.example.mimascota.databinding.ItemCheckoutProductoBinding
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para mostrar productos en el checkout (read-only)
 */
class CheckoutProductoAdapter : ListAdapter<CartItem, CheckoutProductoAdapter.ViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCheckoutProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemCheckoutProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

        fun bind(item: CartItem) {
            with(binding) {
                tvCantidad.text = "${item.cantidad}x"
                tvNombreProducto.text = item.producto.producto_nombre
                tvSubtotal.text = formato.format(item.subtotal)
            }
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

