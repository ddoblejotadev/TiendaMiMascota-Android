package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ProductoViewModel: ViewModel para gestionar el estado de productos desde el backend
 *
 * Funcionalidades:
 * - Obtener productos por categoría
 * - Buscar productos
 * - CRUD de productos (crear, actualizar, eliminar)
 * - Manejo de estados de carga y errores
 * - Integración con el carrito de compras
 */
class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    // Estado de la lista de productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    // Estado de producto individual (para detalles)
    private val _productoActual = MutableStateFlow<Producto?>(null)
    val productoActual: StateFlow<Producto?> = _productoActual.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Estado de éxito (para operaciones CRUD)
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    /**
     * Obtiene todos los productos
     */
    fun loadAllProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                    _error.value = null
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _productos.value = emptyList()
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Obtiene productos por categoría
     */
    fun loadProductosByCategoria(categoria: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getProductosByCategoria(categoria)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                    _error.value = null
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _productos.value = emptyList()
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Obtiene un producto por ID
     */
    fun loadProductoById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getProductoById(id)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productoActual.value = result.data
                    _error.value = null
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _productoActual.value = null
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Busca productos por nombre o descripción
     */
    fun searchProductos(query: String) {
        if (query.isBlank()) {
            loadAllProductos()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.searchProductos(query)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                    _error.value = null
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo producto
     */
    fun createProducto(producto: Producto, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.createProducto(producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _successMessage.value = "Producto creado exitosamente"
                    _error.value = null
                    onSuccess()
                    // Recargar la lista
                    loadAllProductos()
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Actualiza un producto existente
     */
    fun updateProducto(id: Int, producto: Producto, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.updateProducto(id, producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _successMessage.value = "Producto actualizado exitosamente"
                    _error.value = null
                    onSuccess()
                    // Recargar la lista
                    loadAllProductos()
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Elimina un producto
     */
    fun deleteProducto(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.deleteProducto(id)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _successMessage.value = "Producto eliminado exitosamente"
                    _error.value = null
                    onSuccess()
                    // Recargar la lista
                    loadAllProductos()
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * Limpia el producto actual
     */
    fun clearProductoActual() {
        _productoActual.value = null
    }

    /**
     * Filtra productos destacados
     */
    fun getProductosDestacados(): List<Producto> {
        return _productos.value.filter { it.destacado == true }
    }

    /**
     * Filtra productos por rango de precio
     */
    fun filterByPriceRange(minPrice: Int, maxPrice: Int) {
        val filtered = _productos.value.filter { it.price in minPrice..maxPrice }
        _productos.value = filtered
    }

    /**
     * Ordena productos por precio (ascendente o descendente)
     */
    fun sortByPrice(ascending: Boolean = true) {
        _productos.value = if (ascending) {
            _productos.value.sortedBy { it.price }
        } else {
            _productos.value.sortedByDescending { it.price }
        }
    }

    /**
     * Ordena productos por valoración
     */
    fun sortByRating() {
        _productos.value = _productos.value.sortedByDescending { it.valoracion ?: 0.0 }
    }
}

