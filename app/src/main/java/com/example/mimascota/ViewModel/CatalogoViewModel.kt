package com.example.mimascota.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val repo: ProductoRepository = ProductoRepository()
): ViewModel() {
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun cargarProductos(context: Context) {
        viewModelScope.launch {
            _loading.value = true
            val list = repo.obtenerProductosDesdeAssets(context)
            _productos.value = list
            _loading.value = false
        }
    }
    fun buscarProductoPorId(id: Int): Producto? {
        return _productos.value.find { it.id == id }
    }
}