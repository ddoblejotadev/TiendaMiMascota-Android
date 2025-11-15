package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * CatalogoViewModel: ViewModel para gestionar el catálogo de productos
 * Ahora integrado con el backend Spring Boot
 */
class CatalogoViewModel(
    private val repo: ProductoRepository = ProductoRepository()
): ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga todos los productos desde el backend
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = repo.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _productos.value = emptyList()
                }
                else -> {}
            }

            _loading.value = false
        }
    }

    /**
     * Carga productos por categoría desde el backend
     */
    fun cargarProductosPorCategoria(categoria: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = repo.getProductosByCategoria(categoria)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _productos.value = emptyList()
                }
                else -> {}
            }

            _loading.value = false
        }
    }

    /**
     * Busca un producto por ID en la lista cargada
     */
    fun buscarProductoPorId(id: Int): Producto? {
        return _productos.value.find { it.id == id }
    }

    /**
     * Obtiene un producto por ID desde el backend
     */
    fun cargarProductoPorId(id: Int, onResult: (Producto?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = repo.getProductoById(id)) {
                is ProductoRepository.ProductoResult.Success -> {
                    onResult(result.data)
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    onResult(null)
                }
                else -> {
                    onResult(null)
                }
            }

            _loading.value = false
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }
}