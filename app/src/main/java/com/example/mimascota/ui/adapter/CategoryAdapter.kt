package com.example.mimascota.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mimascota.databinding.ItemCategoriaAgrupadaBinding
import com.example.mimascota.model.CategoriaAgrupada
import com.example.mimascota.model.Producto

class CategoryAdapter(
    private val onProductoClick: (Producto) -> Unit,
    private val onAddToCartClick: (Producto) -> Unit
) : ListAdapter<CategoriaAgrupada, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoriaAgrupadaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, onProductoClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoriaAgrupadaBinding,
        private val onProductoClick: (Producto) -> Unit,
        private val onAddToCartClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoria: CategoriaAgrupada) {
            binding.tvCategoriaTitulo.text = categoria.nombre

            val productoAdapter = ProductoAdapter(onProductoClick, onAddToCartClick)
            binding.rvProductosCategoria.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = productoAdapter
            }
            productoAdapter.submitList(categoria.productos)
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoriaAgrupada>() {
        override fun areItemsTheSame(oldItem: CategoriaAgrupada, newItem: CategoriaAgrupada): Boolean {
            return oldItem.nombre == newItem.nombre
        }

        override fun areContentsTheSame(oldItem: CategoriaAgrupada, newItem: CategoriaAgrupada): Boolean {
            return oldItem == newItem
        }
    }
}
