package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mimascota.model.Orden
import com.example.mimascota.databinding.ItemOrdenBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * OrdenesAdapter: Adapter para mostrar lista de órdenes
 */
class OrdenesAdapter(
    private val onClick: (Orden) -> Unit
) : ListAdapter<Orden, OrdenesAdapter.ViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemOrdenBinding,
        private val onClick: (Orden) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(orden: Orden) {
            with(binding) {
                // Número de orden
                tvNumeroOrden.text = "Orden #${orden.numeroOrden}"

                // Fecha
                tvFecha.text = "Fecha: ${dateFormatter.format(orden.fecha)}"

                // Total
                tvTotal.text = currencyFormatter.format(orden.total)

                // Estado
                tvEstado.text = "Estado: ${orden.estado}"
                tvEstado.setTextColor(
                    when (orden.estado) {
                        "PENDIENTE" -> 0xFFFF9800.toInt()
                        "PROCESANDO" -> 0xFF2196F3.toInt()
                        "ENVIADO" -> 0xFF4CAF50.toInt()
                        "ENTREGADO" -> 0xFF4CAF50.toInt()
                        "CANCELADO" -> 0xFFF44336.toInt()
                        else -> 0xFF757575.toInt()
                    }
                )

                // Cantidad de productos
                tvCantidadProductos.text = "${orden.productos.size} producto(s)"

                // Click listener
                root.setOnClickListener {
                    onClick(orden)
                }
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Orden>() {
        override fun areItemsTheSame(oldItem: Orden, newItem: Orden): Boolean {
            return oldItem.ordenId == newItem.ordenId
        }

        override fun areContentsTheSame(oldItem: Orden, newItem: Orden): Boolean {
            return oldItem == newItem
        }
    }
}

