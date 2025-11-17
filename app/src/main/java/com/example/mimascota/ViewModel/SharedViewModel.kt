package com.example.mimascota.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.repository.ProductoRepository
import com.example.mimascota.repository.CartSyncRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

/**
 * SharedViewModel: ViewModel compartido entre fragments
 *
 * Funcionalidades:
 * - Gestión de productos con LiveData
 * - Gestión del carrito compartido
 * - Filtros y búsqueda
 * - Sincronización con backend
 * - Sincronización REAL con React Context mediante API
 * - Integración con autenticación JWT
 */
class SharedViewModel(context: Context) : ViewModel() {

    private val repository = ProductoRepository()
    private val cartSyncRepository = CartSyncRepository()
    private val tokenManager = RetrofitClient.getTokenManager()

    // ID del usuario logueado (obtenido desde TokenManager)
    private val userId: Int
        get() = tokenManager.getUserId()

    // Habilita/deshabilita sincronización automática con React
    var sincronizacionAutomatica: Boolean = true

    // ========== PRODUCTOS ==========
    private val _productos = MutableLiveData<List<Producto>>(emptyList())
    val productos: LiveData<List<Producto>> = _productos

    private val _productosFiltrados = MutableLiveData<List<Producto>>(emptyList())
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados

    private val _productoSeleccionado = MutableLiveData<Producto?>()
    val productoSeleccionado: LiveData<Producto?> = _productoSeleccionado

    // ========== CARRITO ==========
    private val _carrito = MutableLiveData<List<CartItem>>(emptyList())
    val carrito: LiveData<List<CartItem>> = _carrito

    private val _totalCarrito = MutableLiveData(0)
    val totalCarrito: LiveData<Int> = _totalCarrito

    private val _cantidadItems = MutableLiveData(0)
    val cantidadItems: LiveData<Int> = _cantidadItems

    // ========== ESTADOS ==========
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

    // ========== FILTROS ==========
    private val _categoriaSeleccionada = MutableLiveData("Todas")
    val categoriaSeleccionada: LiveData<String> = _categoriaSeleccionada

    private val _busqueda = MutableLiveData("")
    val busqueda: LiveData<String> = _busqueda

    // ========== FUNCIONES DE PRODUCTOS ==========

    /**
     * Carga todos los productos desde el backend
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                    aplicarFiltros()
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
     * Carga productos por categoría
     */
    fun cargarProductosPorCategoria(categoria: String) {
        if (categoria == "Todas") {
            cargarProductos()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _categoriaSeleccionada.value = categoria

            when (val result = repository.getProductosByCategoria(categoria)) {
                is ProductoRepository.ProductoResult.Success -> {
                    _productos.value = result.data
                    aplicarFiltros()
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
     * Busca productos por texto
     */
    fun buscarProductos(query: String) {
        _busqueda.value = query
        aplicarFiltros()
    }

    /**
     * Aplica filtros de búsqueda y categoría
     */
    private fun aplicarFiltros() {
        var lista = _productos.value ?: emptyList()

        // Filtrar por búsqueda
        val query = _busqueda.value ?: ""
        if (query.isNotBlank()) {
            lista = lista.filter { producto ->
                producto.name.contains(query, ignoreCase = true) ||
                producto.description?.contains(query, ignoreCase = true) == true ||
                producto.category.contains(query, ignoreCase = true)
            }
        }

        // Ordenar y agrupar por categoría -> primero categorías no vacías, luego por nombre (normalizando)
        lista = lista.sortedWith(compareBy(
            { it.category.isBlank() },
            { it.category.trim().lowercase() },
            { it.producto_nombre.trim().lowercase() }
        ))

        // Log para depuración: imprimir primeras categorías en orden
        try {
            val cats = lista.map { it.category.trim() }.filter { it.isNotBlank() }
            Log.d("SharedViewModel", "Productos ordenados, primeras categorias: ${cats.take(10)}")
        } catch (e: Exception) {
            Log.e("SharedViewModel", "Error al loggear categorias: ${e.message}")
        }

        _productosFiltrados.value = lista
    }

    /**
     * Selecciona un producto para ver detalles
     */
    fun seleccionarProducto(producto: Producto) {
        _productoSeleccionado.value = producto
    }

    /**
     * Limpia la selección de producto
     */
    fun limpiarSeleccion() {
        _productoSeleccionado.value = null
    }

    // ========== FUNCIONES DE CARRITO ==========

    /**
     * Agrega un producto al carrito
     */
    fun agregarAlCarrito(producto: Producto, cantidad: Int = 1): Boolean {
        val carritoActual = _carrito.value?.toMutableList() ?: mutableListOf()
        val itemExistente = carritoActual.find { it.producto.producto_id == producto.producto_id }

        val stockDisponible = producto.stock ?: 0
        if (stockDisponible <= 0) {
            _mensaje.value = "Producto sin stock"
            return false
        }

        if (itemExistente != null) {
            val nuevaCantidad = itemExistente.cantidad + cantidad
            if (nuevaCantidad > stockDisponible) {
                _mensaje.value = "Stock insuficiente. Disponible: $stockDisponible"
                return false
            }
            val index = carritoActual.indexOf(itemExistente)
            carritoActual[index] = itemExistente.copy(cantidad = nuevaCantidad)
        } else {
            if (cantidad > stockDisponible) {
                _mensaje.value = "Stock insuficiente. Disponible: $stockDisponible"
                return false
            }
            carritoActual.add(CartItem(producto, cantidad))
        }

        _carrito.value = carritoActual
        calcularTotales()
        _mensaje.value = "${producto.producto_nombre} agregado al carrito"
        sincronizarCarritoConReact()
        return true
    }

    /**
     * Elimina un producto del carrito
     */
    fun eliminarDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value?.toMutableList() ?: mutableListOf()
        carritoActual.removeAll { it.producto.producto_id == producto.producto_id }
        _carrito.value = carritoActual
        calcularTotales()
        _mensaje.value = "${producto.producto_nombre} eliminado del carrito"

        // Sincronizar con React
        sincronizarCarritoConReact()
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    fun actualizarCantidad(producto: Producto, cantidad: Int) {
        if (cantidad <= 0) {
            eliminarDelCarrito(producto)
            return
        }
        val stockDisponible = producto.stock ?: 0
        if (cantidad > stockDisponible) {
            _mensaje.value = "Stock insuficiente. Disponible: $stockDisponible"
            return
        }
        val carritoActual = _carrito.value?.toMutableList() ?: mutableListOf()
        val itemExistente = carritoActual.find { it.producto.producto_id == producto.producto_id }
        if (itemExistente != null) {
            val index = carritoActual.indexOf(itemExistente)
            carritoActual[index] = itemExistente.copy(cantidad = cantidad)
            _carrito.value = carritoActual
            calcularTotales()
            sincronizarCarritoConReact()
        }
    }

    /**
     * Vacía el carrito
     */
    fun vaciarCarrito() {
        _carrito.value = emptyList()
        calcularTotales()
        _mensaje.value = "Carrito vaciado"

        // Sincronizar con React
        sincronizarCarritoConReact()
    }

    /**
     * Calcula los totales del carrito
     */
    private fun calcularTotales() {
        val items = _carrito.value ?: emptyList()
        _totalCarrito.value = items.sumOf { it.subtotal }
        _cantidadItems.value = items.sumOf { it.cantidad }
    }

    // ========== SINCRONIZACIÓN CON REACT ==========

    /**
     * Sincroniza el carrito con el Context de React mediante el backend
     * Esta función se llama automáticamente después de cada operación del carrito
     */
    private fun sincronizarCarritoConReact() {
        if (!sincronizacionAutomatica) return

        viewModelScope.launch {
            try {
                val items = _carrito.value ?: emptyList()

                when (val result = cartSyncRepository.sincronizarCarritoConReact(userId, items)) {
                    is CartSyncRepository.SyncResult.Success -> {
                        android.util.Log.d("SharedViewModel", "Carrito sincronizado con React")
                    }
                    is CartSyncRepository.SyncResult.Error -> {
                        android.util.Log.e("SharedViewModel", "Error sincronizando: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SharedViewModel", "Error en sincronización: ${e.message}")
            }
        }
    }

    /**
     * Obtiene el carrito desde React (sincronización inversa)
     * Útil al iniciar la app o después de login
     */
    fun sincronizarDesdeReact() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = cartSyncRepository.obtenerCarritoDesdeReact(userId)) {
                is CartSyncRepository.SyncResult.Success -> {
                    _carrito.value = result.data
                    calcularTotales()
                    _mensaje.value = "Carrito sincronizado desde React"
                }
                is CartSyncRepository.SyncResult.Error -> {
                    _error.value = "Error al obtener carrito: ${result.message}"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Fuerza una sincronización manual del carrito
     */
    fun forzarSincronizacion() { sincronizarCarritoConReact() }

    // ========== UTILIDADES ==========

    /**
     * Limpia el mensaje
     */
    fun limpiarMensaje() {
        _mensaje.value = null
    }

    /**
     * Limpia el error
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Obtiene las categorías disponibles
     */
    fun obtenerCategorias(): List<String> {
        val categorias = _productos.value
            ?.map { it.category?.trim() ?: "" }
            ?.filter { it.isNotBlank() }
            ?.map { it }
            ?.distinct() ?: emptyList()
        return listOf("Todas") + categorias.sortedWith(compareBy { it.lowercase() })
    }
}
