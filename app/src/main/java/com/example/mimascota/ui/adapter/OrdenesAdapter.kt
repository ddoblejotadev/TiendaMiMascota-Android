package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mimascota.R
import com.example.mimascota.databinding.ItemOrdenBinding
import com.example.mimascota.model.OrdenHistorial
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrdenesAdapter(
    private val onItemClicked: (OrdenHistorial) -> Unit
) : ListAdapter<OrdenHistorial, OrdenesAdapter.OrdenViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val binding = ItemOrdenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val orden = getItem(position)
        holder.bind(orden)
    }

    inner class OrdenViewHolder(private val binding: ItemOrdenBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orden: OrdenHistorial) {
            binding.tvNumeroOrden.text = "Orden #${orden.numeroOrden}"
            binding.tvEstadoOrden.text = orden.estado

            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(orden.fecha)
                val formatter = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
                binding.tvFechaOrden.text = formatter.format(date)
            } catch (e: Exception) {
                binding.tvFechaOrden.text = orden.fecha.split("T").firstOrNull() ?: orden.fecha
            }

            binding.tvTotalOrden.text = String.format(Locale.getDefault(), "$%,.0f", orden.total)

            // Limpiar vistas anteriores
            binding.productosContainer.removeAllViews()

            // Añadir vistas de detalle de productos
            orden.productos.forEach { item ->
                val inflater = LayoutInflater.from(binding.root.context)
                val detalleView = inflater.inflate(R.layout.item_detalle_orden, binding.productosContainer, false)

                val ivProductoImagen = detalleView.findViewById<ImageView>(R.id.ivProductoImagen)
                val tvProductoNombre = detalleView.findViewById<TextView>(R.id.tvProductoNombre)
                val tvCantidad = detalleView.findViewById<TextView>(R.id.tvCantidad)
                val tvPrecioUnitario = detalleView.findViewById<TextView>(R.id.tvPrecioUnitario)

                tvProductoNombre.text = item.nombre
                tvCantidad.text = "Cantidad: ${item.cantidad}"
                tvPrecioUnitario.text = String.format(Locale.getDefault(), "$%,.0f", item.precioUnitario)
                ivProductoImagen.load(item.imagen) {
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_close_clear_cancel)
                }

                binding.productosContainer.addView(detalleView)
            }

            itemView.setOnClickListener {
                onItemClicked(orden)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrdenHistorial>() {
        override fun areItemsTheSame(oldItem: OrdenHistorial, newItem: OrdenHistorial): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrdenHistorial, newItem: OrdenHistorial): Boolean {
            return oldItem == newItem
        }
    }
}
