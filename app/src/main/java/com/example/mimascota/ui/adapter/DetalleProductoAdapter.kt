
package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mimascota.R
import com.example.mimascota.databinding.ItemDetalleOrdenBinding
import com.example.mimascota.model.ProductoHistorial
import com.example.mimascota.util.CurrencyUtils
import com.example.mimascota.util.ImageUtils

class DetalleProductoAdapter : ListAdapter<ProductoHistorial, DetalleProductoAdapter.DetalleProductoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleProductoViewHolder {
        val binding = ItemDetalleOrdenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DetalleProductoViewHolder(private val binding: ItemDetalleOrdenBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(producto: ProductoHistorial) {
            binding.tvProductoNombre.text = producto.nombre
            binding.tvProductoCantidad.text = "Cantidad: ${producto.cantidad}"
            binding.tvProductoPrecio.text = CurrencyUtils.formatAsCLP(producto.precioUnitario * producto.cantidad)

            if (producto.imagen.startsWith("data:image")) {
                val bitmap = ImageUtils.decodeBase64ToBitmap(producto.imagen)
                if (bitmap != null) {
                    binding.ivProductoImagen.setImageBitmap(bitmap)
                } else {
                    binding.ivProductoImagen.load(R.drawable.placeholder_product)
                }
            } else {
                binding.ivProductoImagen.load(producto.imagen) {
                    placeholder(R.drawable.placeholder_product)
                    error(R.drawable.placeholder_product)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductoHistorial>() {
        override fun areItemsTheSame(oldItem: ProductoHistorial, newItem: ProductoHistorial): Boolean {
            return oldItem.productoId == newItem.productoId
        }

        override fun areContentsTheSame(oldItem: ProductoHistorial, newItem: ProductoHistorial): Boolean {
            return oldItem == newItem
        }
    }
}
