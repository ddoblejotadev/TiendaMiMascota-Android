package com.example.mimascota.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.CartItem
import com.example.mimascota.model.CategoriaAgrupada
import com.example.mimascota.model.Producto
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.repository.ProductoRepository
import com.example.mimascota.repository.CartSyncRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class SharedViewModel(context: Context) : ViewModel() {

    private val repository = ProductoRepository()
    private val cartSyncRepository = CartSyncRepository()
    private val tokenManager = RetrofitClient.getTokenManager()

    private val userId: Int get() = tokenManager.getUserId()
    var sincronizacionAutomatica: Boolean = true

    // ========== PRODUCTOS ==========
    private val _productos = MutableLiveData<List<Producto>>(emptyList())
    val productos: LiveData<List<Producto>> = _productos

    private val _productosFiltrados = MutableLiveData<List<Producto>>(emptyList())
    val productosFiltrados: LiveData<List<Producto>> = _productosFiltrados

    private val _productosAgrupados = MutableLiveData<List<CategoriaAgrupada>>(emptyList())
    val productosAgrupados: LiveData<List<CategoriaAgrupada>> = _productosAgrupados

    private val _productoSeleccionado = MutableLiveData<Producto?>()
    val productoSeleccionado: LiveData<Producto?> = _productoSeleccionado

    // ========== CARRITO ==========
    private val _carrito = MutableLiveData<List<CartItem>>(emptyList())
    val carrito: LiveData<List<CartItem>> = _carrito

    private val _totalCarrito = MutableLiveData(0.0)
    val totalCarrito: LiveData<Double> = _totalCarrito

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
                is ProductoRepository.ProductoResult.Loading -> {
                    // Opcional: manejar el estado de carga si es necesario
                }
            }
            _isLoading.value = false
        }
    }

    fun cargarProductosPorCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        aplicarFiltros() // Re-aplicar filtros en la lista actual
    }

    fun buscarProductos(query: String) {
        _busqueda.value = query
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        var lista = _productos.value ?: emptyList()

        val categoria = _categoriaSeleccionada.value
        if (categoria != null && categoria != "Todas") {
            lista = lista.filter { it.category.equals(categoria, ignoreCase = true) }
        }

        val query = _busqueda.value
        if (!query.isNullOrBlank()) {
            lista = lista.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }

        _productosFiltrados.value = lista

        val agrupados = lista
            .filter { it.category.isNotBlank() }
            .groupBy { it.category.trim() }
            .map { (nombreCategoria, productosCategoria) ->
                val productosOrdenados = productosCategoria.sortedBy { it.producto_nombre }
                CategoriaAgrupada(nombre = nombreCategoria, productos = productosOrdenados)
            }
            .sortedBy { it.nombre }

        _productosAgrupados.value = agrupados
        Log.d("SharedViewModel", "Productos agrupados en ${agrupados.size} categorías.")
    }

    fun seleccionarProducto(producto: Producto) {
        _productoSeleccionado.value = producto
    }

    fun limpiarSeleccion() {
        _productoSeleccionado.value = null
    }

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

    fun eliminarDelCarrito(producto: Producto) {
        val carritoActual = _carrito.value?.toMutableList() ?: mutableListOf()
        carritoActual.removeAll { it.producto.producto_id == producto.producto_id }
        _carrito.value = carritoActual
        calcularTotales()
        _mensaje.value = "${producto.producto_nombre} eliminado del carrito"
        sincronizarCarritoConReact()
    }

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

    fun vaciarCarrito() {
        _carrito.value = emptyList()
        calcularTotales()
        _mensaje.value = "Carrito vaciado"
        sincronizarCarritoConReact()
    }

    private fun calcularTotales() {
        val items = _carrito.value ?: emptyList()
        // Corregido: Calcular el total multiplicando precio (Double) por cantidad
        _totalCarrito.value = items.sumOf { it.producto.price * it.cantidad }
        _cantidadItems.value = items.sumOf { it.cantidad }
    }

    private fun sincronizarCarritoConReact() {
        if (!sincronizacionAutomatica) return
        viewModelScope.launch {
            try {
                val items = _carrito.value ?: emptyList()
                when (val result = cartSyncRepository.sincronizarCarritoConReact(userId, items)) {
                    is CartSyncRepository.SyncResult.Success -> Log.d("SharedViewModel", "Carrito sincronizado con React")
                    is CartSyncRepository.SyncResult.Error -> Log.e("SharedViewModel", "Error sincronizando: ${result.message}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error en sincronización: ${e.message}")
            }
        }
    }

    fun sincronizarDesdeReact() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = cartSyncRepository.obtenerCarritoDesdeReact(userId)) {
                is CartSyncRepository.SyncResult.Success -> {
                    _carrito.value = result.data
                    calcularTotales()
                    _mensaje.value = "Carrito sincronizado desde React"
                }
                is CartSyncRepository.SyncResult.Error -> _error.value = "Error al obtener carrito: ${result.message}"
            }
            _isLoading.value = false
        }
    }

    fun forzarSincronizacion() { sincronizarCarritoConReact() }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun limpiarError() {
        _error.value = null
    }

    fun obtenerCategorias(): List<String> {
        val categorias = _productos.value
            ?.map { it.category.trim() }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?.sorted() ?: emptyList()
        return listOf("Todas") + categorias
    }
}
