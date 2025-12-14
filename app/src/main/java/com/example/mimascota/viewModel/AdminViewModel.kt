
package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.model.UserWithOrders
import com.example.mimascota.repository.AdminRepository
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val adminRepository = AdminRepository()
    private val productoRepository = ProductoRepository()

    private val _usersWithOrders = MutableStateFlow<List<UserWithOrders>>(emptyList())
    val usersWithOrders: StateFlow<List<UserWithOrders>> = _usersWithOrders.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Producto?>(null)
    val selectedProduct: StateFlow<Producto?> = _selectedProduct.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAdminData()
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 1. Fetch all products to create a lookup map
                val allProducts = when (val result = productoRepository.getAllProductos()) {
                    is ProductoRepository.ProductoResult.Success -> result.data
                    is ProductoRepository.ProductoResult.Error -> {
                        _error.value = result.message
                        Log.e("AdminViewModel", "Error loading products: ${result.message}")
                        emptyList()
                    }
                    else -> emptyList()
                }
                _productos.value = allProducts
                val productsMap = allProducts.associateBy { it.producto_id }

                // 2. Fetch all orders
                val orders = when (val ordersResult = adminRepository.getAllOrders()) {
                    is AdminRepository.AdminResult.Success -> ordersResult.data ?: emptyList()
                    is AdminRepository.AdminResult.Error -> {
                        _error.value = ordersResult.message
                        Log.e("AdminViewModel", "Error loading orders: ${ordersResult.message}")
                        emptyList()
                    }
                }

                // 3. Enrich orders with product details (FIXED LOGIC)
                val enrichedOrders = orders.map { order ->
                    val enrichedProductos = order.productos?.map { productoDeOrden ->
                        val fullProduct = productsMap[productoDeOrden.productoId]
                        if (fullProduct == null) {
                            Log.w("AdminViewModel", "Product ID ${productoDeOrden.productoId} in order #${order.id} not found in products map. It will be displayed with partial data.")
                            productoDeOrden // Return original item if not found
                        } else {
                            // Return item with name and image from the full product details
                            productoDeOrden.copy(
                                nombre = fullProduct.producto_nombre,
                                imagen = fullProduct.imageUrl
                            )
                        }
                    }
                    order.copy(productos = enrichedProductos)
                }

                // 4. Fetch users
                val users = when (val usersResult = adminRepository.getAllUsers()) {
                    is AdminRepository.AdminResult.Success -> usersResult.data ?: emptyList()
                    is AdminRepository.AdminResult.Error -> {
                        _error.value = usersResult.message
                        Log.e("AdminViewModel", "Error loading users: ${usersResult.message}")
                        emptyList()
                    }
                }

                // 5. Group enriched orders by user
                val groupedData = users.map { user ->
                    val userOrders = enrichedOrders.filter { it.usuarioId == user.usuarioId.toLong() }
                    UserWithOrders(user, userOrders)
                }

                _usersWithOrders.value = groupedData
                Log.d("AdminViewModel", "Admin data loaded and enriched. Users: ${users.size}, Orders: ${enrichedOrders.size}")

            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
                Log.e("AdminViewModel", "Exception loading admin data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // ========== PRODUCTOS CRUD ==========

    private fun loadProductos() {
        viewModelScope.launch {
            when (val result = productoRepository.getAllProductos()) {
                is ProductoRepository.ProductoResult.Success -> _productos.value = result.data
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun getProductoById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = productoRepository.getProductoById(id.toInt())
                if (result is ProductoRepository.ProductoResult.Success) {
                    _selectedProduct.value = result.data
                } else if (result is ProductoRepository.ProductoResult.Error) {
                    _error.value = result.message
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
                Log.e("AdminViewModel", "Excepción al obtener el producto", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createProducto(producto: Producto) {
        viewModelScope.launch {
            when (val result = productoRepository.createProducto(producto)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos()
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun updateProducto(id: Int, producto: Producto) {
        viewModelScope.launch {
            when (val result = productoRepository.updateProducto(id, producto)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos()
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun deleteProducto(id: Int) {
        viewModelScope.launch {
            when (val result = productoRepository.deleteProducto(id)) {
                is ProductoRepository.ProductoResult.Success -> loadProductos()
                is ProductoRepository.ProductoResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    // ========== ÓRDENES ==========

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            when (val result = adminRepository.updateOrderStatus(orderId, status)) {
                is AdminRepository.AdminResult.Success -> {
                    Log.d("AdminViewModel", "Estado de la orden actualizado. Refrescando datos.")
                    loadAdminData()
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    // ========== USUARIOS ==========

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            when (val result = adminRepository.deleteUser(userId)) {
                is AdminRepository.AdminResult.Success -> {
                    Log.d("AdminViewModel", "Usuario eliminado. Refrescando datos.")
                    loadAdminData() // Recargar la lista de usuarios
                }
                is AdminRepository.AdminResult.Error -> {
                    _error.value = result.message
                    Log.e("AdminViewModel", "Error al eliminar usuario: ${result.message}")
                }
            }
        }
    }
}
