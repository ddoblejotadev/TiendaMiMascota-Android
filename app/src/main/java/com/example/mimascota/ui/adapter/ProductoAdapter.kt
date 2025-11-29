package com.example.mimascota.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mimascota.R
import com.example.mimascota.databinding.ItemProductoBinding
import com.example.mimascota.model.Producto
import com.example.mimascota.util.AppConfig // Importar AppConfig
import java.util.Locale

class ProductoAdapter(
    private val onItemClick: (Producto) -> Unit,
    private val onAddToCartClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    companion object {
        private const val TAG = "ProductoAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: Producto) {
            binding.tvProductoNombre.text = producto.producto_nombre
            binding.tvProductoPrecio.text = String.format(Locale.getDefault(), "$%.2f", producto.price)
            binding.tvProductoCategoria.text = producto.category

            val precioAnterior = producto.precioAnterior
            if (precioAnterior != null && precioAnterior > producto.price) {
                binding.tvProductoStock.visibility = View.VISIBLE
                binding.tvProductoStock.text = String.format(Locale.getDefault(), "$%.2f", precioAnterior)
                binding.tvProductoStock.paintFlags = binding.tvProductoStock.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvProductoStock.visibility = View.GONE
            }

            // Use AppConfig.toAbsoluteImageUrl to build correct URL
            val imageUrl = producto.imageUrl
            val fullUrl = AppConfig.toAbsoluteImageUrl(imageUrl)
            if (!fullUrl.isNullOrEmpty()) {
                Log.d(TAG, "Cargando imagen: $fullUrl para productoId=${producto.producto_id}")
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(binding.ivProductoImagen)
            } else {
                Log.w(TAG, "URL de imagen vacía para productoId=${producto.producto_id}")
                binding.ivProductoImagen.setImageResource(R.drawable.placeholder_product)
            }

            binding.btnAddToCart.setOnClickListener {
                // Animación simple: escalar la imagen y después ejecutar callback
                binding.ivProductoImagen.animate()
                    .scaleX(0.85f)
                    .scaleY(0.85f)
                    .setDuration(120)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.ivProductoImagen.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(120)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        onAddToCartClick(producto)
                                    }
                                }).start()
                        }
                    }).start()
            }

            itemView.setOnClickListener {
                onItemClick(producto)
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.producto_id == newItem.producto_id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.producto_id == newItem.producto_id && oldItem.producto_nombre == newItem.producto_nombre
        }
    }
}
