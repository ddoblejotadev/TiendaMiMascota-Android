package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mimascota.Model.Producto
import com.example.mimascota.R
import com.example.mimascota.config.ApiConfig
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat
import java.util.Locale

/**
 * ProductoAdapter: Adaptador para RecyclerView que muestra la lista de productos
 *
 * Características:
 * - Usa DiffUtil para actualizaciones eficientes
 * - Carga imágenes con Coil (URLs absolutas desde backend)
 * - Formato de precio chileno
 * - Click listeners para ver detalles y agregar al carrito
 */
class ProductoAdapter(
    private val onItemClick: (Producto) -> Unit,
    private val onAddToCartClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = getItem(position)
        holder.bind(producto)

        // Mostrar header de categoría si es la primera posición o la categoría cambió respecto al anterior
        val showHeader = if (position == 0) {
            true
        } else {
            val prev = getItem(position - 1)
            val prevCat = prev.category?.trim()?.lowercase() ?: ""
            val curCat = producto.category?.trim()?.lowercase() ?: ""
            prevCat != curCat
        }
        holder.showCategoryHeader(if (showHeader) producto.category?.trim() else null)
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardProducto)
        private val imageView: ImageView = itemView.findViewById(R.id.ivProductoImagen)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvProductoNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvProductoPrecio)
        private val tvStock: TextView = itemView.findViewById(R.id.tvProductoStock)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvProductoCategoria)
        private val btnAddCart: MaterialButton = itemView.findViewById(R.id.btnAddToCart)
        private val tvCategoryHeader: TextView? = itemView.findViewById(R.id.tvCategoryHeader)

        fun bind(producto: Producto) {
            tvNombre.text = producto.name

            // Formato de precio chileno
            val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
            tvPrecio.text = formato.format(producto.price)

            tvStock.text = itemView.context.getString(R.string.product_stock, producto.stock ?: 0)
            tvCategoria.text = producto.category

            // Construir URL absoluta y cargar con Coil
            val imageUrl = ApiConfig.toAbsoluteImageUrl(producto.imageUrl)
            imageView.load(imageUrl) {
                placeholder(R.drawable.placeholder_product)
                error(R.drawable.ic_error_image)
                crossfade(true)
            }

            // Click en la card para ver detalles
            cardView.setOnClickListener {
                onItemClick(producto)
            }

            // Click en botón para agregar al carrito
            btnAddCart.setOnClickListener {
                onAddToCartClick(producto)
            }

            // Deshabilitar botón si no hay stock
            btnAddCart.isEnabled = (producto.stock ?: 0) > 0
        }

        fun showCategoryHeader(category: String?) {
            if (tvCategoryHeader == null) return
            if (category.isNullOrBlank()) {
                tvCategoryHeader.visibility = View.GONE
            } else {
                tvCategoryHeader.visibility = View.VISIBLE
                tvCategoryHeader.text = category
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.producto_id == newItem.producto_id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            // Opcional: comparar campos relevantes
            return oldItem.producto_id == newItem.producto_id &&
                    oldItem.price == newItem.price &&
                    oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.producto_nombre == newItem.producto_nombre
        }
    }
}
