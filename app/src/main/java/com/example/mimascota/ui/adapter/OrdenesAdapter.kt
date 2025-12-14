
package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mimascota.databinding.ItemOrdenBinding
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.model.ProductoHistorial
import com.example.mimascota.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrdenesAdapter(
) : ListAdapter<OrdenHistorial, OrdenesAdapter.OrdenViewHolder>(DiffCallback()) {

    private val expandedState = mutableMapOf<Long, Boolean>()
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val binding = ItemOrdenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val orden = getItem(position)
        val isExpanded = expandedState[orden.id] ?: false
        holder.bind(orden, isExpanded)
    }

    inner class OrdenViewHolder(private val binding: ItemOrdenBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.headerOrden.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val orden = getItem(position)
                    val isExpanded = !(expandedState[orden.id] ?: false)
                    expandedState[orden.id] = isExpanded
                    notifyItemChanged(position)
                }
            }
        }

        fun bind(orden: OrdenHistorial, isExpanded: Boolean) {
            binding.tvNumeroOrden.text = "Orden #${orden.numeroOrden ?: "N/A"}"
            binding.tvEstadoOrden.text = orden.estado ?: "Desconocido"

            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = orden.fecha?.let { parser.parse(it) }
                val formatter = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
                binding.tvFechaOrden.text = if (date != null) formatter.format(date) else "Fecha no disponible"
            } catch (e: Exception) {
                binding.tvFechaOrden.text = (orden.fecha?.split("T")?.firstOrNull() ?: orden.fecha) ?: "Fecha no disponible"
            }

            binding.tvTotalOrden.text = CurrencyUtils.formatAsCLP(orden.total)

            // Visibilidad del detalle
            binding.detalleOrdenContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

            if (isExpanded) {
                // Setup del RecyclerView de productos
                val detalleAdapter = DetalleProductoAdapter()
                binding.rvProductosDetalle.apply {
                    layoutManager = LinearLayoutManager(binding.root.context)
                    adapter = detalleAdapter
                    setRecycledViewPool(viewPool)
                }
                val productosHistorial = orden.productos?.map { 
                    ProductoHistorial(
                        productoId = it.productoId.toString(),
                        nombre = it.nombre ?: "",
                        cantidad = it.cantidad ?: 0,
                        precioUnitario = it.precioUnitario ?: 0.0,
                        imagen = it.imagen ?: ""
                    )
                }
                detalleAdapter.submitList(productosHistorial)

                // Desglose de precios
                val totalConIva = orden.total ?: 0.0
                val subtotal = CurrencyUtils.getBasePrice(totalConIva)
                val iva = totalConIva - subtotal

                binding.tvDetalleSubtotal.text = CurrencyUtils.formatAsCLP(subtotal)
                binding.tvDetalleIva.text = CurrencyUtils.formatAsCLP(iva)
                binding.tvDetalleEnvio.text = ""
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
