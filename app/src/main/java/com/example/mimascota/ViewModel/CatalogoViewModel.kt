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

    // Lista maestra (última sincronizada desde backend)
    private val _allProducts = MutableStateFlow<List<Producto>>(emptyList())

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Filtro actual aplicado ("Todas" para no filtrar)
    private var currentCategoryFilter: String = "Todas"

    // Id del producto seleccionado para edición (null si ninguno)
    private val _selectedProductId = MutableStateFlow<Int?>(null)
    val selectedProductId: StateFlow<Int?> = _selectedProductId

    /**
     * Carga todos los productos desde el backend y actualiza la lista maestra.
     * Mantiene cualquier filtro local aplicado (se re-aplica después de actualizar).
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = repo.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> {
                    _allProducts.value = result.data
                    // Reaplicar filtro actual
                    applyCategoryFilterInternal(currentCategoryFilter)
                }
                is ProductoRepository.ProductoResult.Error -> {
                    _error.value = result.message
                    _allProducts.value = emptyList()
                    _productos.value = emptyList()
                }
                else -> {}
            }

            _loading.value = false
        }
    }

    /**
     * Método público para aplicar un filtro local por categoría.
     * Rápido: filtra la lista maestra en memoria y actualiza el StateFlow.
     * También lanza una petición en background al servidor para mantener la lista maestra sincronizada.
     */
    fun applyCategoryFilter(categoria: String) {
        currentCategoryFilter = categoria
        // Filtro rápido local
        applyCategoryFilterInternal(categoria)

        // Refrescar en background desde el servidor para mantener consistencia
        viewModelScope.launch {
            if (categoria == "Todas") {
                when (val r = repo.getAllProductos()) {
                    is ProductoRepository.ProductoResult.Success -> {
                        _allProducts.value = r.data
                        applyCategoryFilterInternal(currentCategoryFilter)
                    }
                    is ProductoRepository.ProductoResult.Error -> {
                        // no sobrescribimos el estado filtrado local en caso de error
                        _error.value = r.message
                    }
                    else -> {}
                }
            } else {
                // Si backend soporta endpoint por categoría, usarlo; si falla, no romper la UI
                when (val r = repo.getProductosByCategoria(categoria)) {
                    is ProductoRepository.ProductoResult.Success -> {
                        // Integrar: reemplazamos los productos de esa categoría dentro de _allProducts
                        val updated = _allProducts.value.toMutableList()
                        // eliminar antiguos de la misma categoría
                        updated.removeAll { it.category.equals(categoria, ignoreCase = true) }
                        // agregar los nuevos recibidos
                        updated.addAll(r.data)
                        _allProducts.value = updated
                        applyCategoryFilterInternal(currentCategoryFilter)
                    }
                    is ProductoRepository.ProductoResult.Error -> {
                        _error.value = r.message
                    }
                    else -> {}
                }
            }
        }
    }

    // Aplica filtro local sin lanzar peticiones adicionales
    private fun applyCategoryFilterInternal(categoria: String) {
        val list = if (categoria.isBlank() || categoria.equals("Todas", ignoreCase = true)) {
            _allProducts.value
        } else {
            _allProducts.value.filter { it.category.equals(categoria, ignoreCase = true) }
        }
        _productos.value = list.sortedWith(compareBy({ it.category.trim().lowercase() }, { it.name.trim().lowercase() }))
    }

    /**
     * Mantengo el método original que buscaba por categoría en el backend pero lo delego a applyCategoryFilter
     * para compatibilidad (si algún código externo llama a cargarProductosPorCategoria seguirá funcionando).
     */
    fun cargarProductosPorCategoria(categoria: String) {
        applyCategoryFilter(categoria)
    }

    /**
     * Busca un producto por ID en la lista cargada
     */
    fun buscarProductoPorId(id: Int): Producto? {
        return _allProducts.value.find { it.id == id } ?: _productos.value.find { it.id == id }
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

    /**
     * Crea un nuevo producto usando el repositorio y actualiza las listas locales.
     */
    fun createProducto(producto: Producto, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            when (val r = repo.createProducto(producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    // agregar al listado maestro y reaplicar filtro
                    _allProducts.value = _allProducts.value + r.data
                    applyCategoryFilterInternal(currentCategoryFilter)
                    onResult(true, null)
                }
                is ProductoRepository.ProductoResult.Error -> {
                    onResult(false, r.message)
                }
                else -> onResult(false, "Error desconocido")
            }
            _loading.value = false
        }
    }

    /**
     * Actualiza producto existente
     */
    fun updateProducto(id: Int, producto: Producto, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            when (val r = repo.updateProducto(id, producto)) {
                is ProductoRepository.ProductoResult.Success -> {
                    // reemplazar en lista maestra
                    val updated = _allProducts.value.map { if (it.id == id) r.data else it }
                    _allProducts.value = updated
                    applyCategoryFilterInternal(currentCategoryFilter)
                    onResult(true, null)
                }
                is ProductoRepository.ProductoResult.Error -> onResult(false, r.message)
                else -> onResult(false, "Error desconocido")
            }
            _loading.value = false
        }
    }

    /**
     * Elimina producto
     */
    fun deleteProducto(id: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            when (val r = repo.deleteProducto(id)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _allProducts.value = _allProducts.value.filterNot { it.id == id }
                    applyCategoryFilterInternal(currentCategoryFilter)
                    onResult(true, null)
                }
                is ProductoRepository.ProductoResult.Error -> onResult(false, r.message)
                else -> onResult(false, "Error desconocido")
            }
            _loading.value = false
        }
    }

    fun selectForEdit(id: Int?) { _selectedProductId.value = id }

    // Obtener producto por id de la lista maestra (sin llamadas a backend)
    fun getProductoFromCache(id: Int): Producto? {
        return _allProducts.value.find { it.id == id }
    }
}