package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    companion object {
        private const val TAG = "CatalogoViewModel"
    }

    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            val result = repository.getAllProductos()
            when (result) {
                is ProductoRepository.ProductoResult.Success -> {
                    Log.d(TAG, "Productos recibidos: ${result.data.size}")
                    _productos.value = result.data
                }
                is ProductoRepository.ProductoResult.Error -> {
                    Log.e(TAG, "Error al cargar productos: ${result.message}")
                }
                else -> {
                    Log.w(TAG, "Resultado desconocido al cargar productos")
                }
            }
        }
    }

    fun getProductoById(id: Int): StateFlow<Producto?> {
        val productoFlow = MutableStateFlow<Producto?>(null)
        viewModelScope.launch {
            val result = repository.getProductoById(id)
            if (result is ProductoRepository.ProductoResult.Success) {
                productoFlow.value = result.data
            }
        }
        return productoFlow.asStateFlow()
    }

    fun createProducto(producto: Producto) {
        viewModelScope.launch {
            when (val result = repository.createProducto(producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    Log.d(TAG, "Producto creado con éxito, recargando lista.")
                    cargarProductos() // Recargar la lista
                }
                is ProductoRepository.ProductoResult.Error -> {
                    Log.e(TAG, "Error al crear producto: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun updateProducto(id: Int, producto: Producto) {
        viewModelScope.launch {
            when (val result = repository.updateProducto(id, producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    Log.d(TAG, "Producto actualizado con éxito, recargando lista.")
                    cargarProductos() // Recargar la lista
                }
                is ProductoRepository.ProductoResult.Error -> {
                    Log.e(TAG, "Error al actualizar producto: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun deleteProducto(id: Int) {
        viewModelScope.launch {
            when (val result = repository.deleteProducto(id)) {
                is ProductoRepository.ProductoResult.Success -> {
                    Log.d(TAG, "Producto eliminado con éxito, recargando lista.")
                    cargarProductos() // Recargar la lista
                }
                is ProductoRepository.ProductoResult.Error -> {
                    Log.e(TAG, "Error al eliminar producto: ${result.message}")
                }
                else -> {}
            }
        }
    }
}
