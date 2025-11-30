package com.example.mimascota.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()

    fun getAllProductos() {
        viewModelScope.launch {
            val result = repository.getAllProductos()
            if (result is ProductoRepository.ProductoResult.Success) {
                _productos.value = result.data
            }
        }
    }

    fun getProductoById(id: Int) {
        viewModelScope.launch {
            val result = repository.getProductoById(id)
            if (result is ProductoRepository.ProductoResult.Success) {
                _producto.value = result.data
            }
        }
    }
}
