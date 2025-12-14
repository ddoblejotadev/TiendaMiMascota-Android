
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

            val imageUrl = producto.imagen

            // Intenta decodificar como Base64 primero
            val bitmap = ImageUtils.decodeBase64ToBitmap(imageUrl)

            if (bitmap != null) {
                // Si la decodificación es exitosa, es una imagen Base64
                binding.ivProductoImagen.setImageBitmap(bitmap)
            } else if (!imageUrl.isNullOrBlank()) {
                // Si no es un Base64 válido, se asume que es una URL y se carga con Coil
                binding.ivProductoImagen.load(imageUrl) {
                    placeholder(R.drawable.logo1)
                    error(R.drawable.logo1)
                }
            } else {
                // Si la URL de la imagen está vacía o nula, se carga la imagen de respaldo
                binding.ivProductoImagen.load(R.drawable.logo1)
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
