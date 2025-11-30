package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.CartItem
import com.example.mimascota.model.Producto
import com.example.mimascota.repository.CartSyncRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    companion object {
        private const val TAG = "CartViewModel"
    }

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    // Repo para sincronizar carrito con backend/React si se requiere
    private val cartSyncRepository = CartSyncRepository()

    fun agregarAlCarrito(producto: Producto) {
        Log.d(TAG, "agregarAlCarrito llamado para producto id=${producto.producto_id} name=${producto.producto_nombre}")
        _items.update { currentItems ->
            val existingItem = currentItems.find { it.producto.producto_id == producto.producto_id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.producto.producto_id == producto.producto_id) it.copy(cantidad = it.cantidad + 1) else it
                }
            } else {
                currentItems + CartItem(producto, 1)
            }
        }
        recalcularTotal()

        // Sincronizar en background si el usuario está logueado
        if (TokenManager.isLoggedIn()) {
            viewModelScope.launch {
                try {
                    val userId = TokenManager.getUserId()
                    val result = cartSyncRepository.sincronizarCarritoConReact(userId, _items.value)
                    when (result) {
                        is CartSyncRepository.SyncResult.Success -> Log.d(TAG, "Carrito sincronizado con backend")
                        is CartSyncRepository.SyncResult.Error -> Log.w(TAG, "Fallo sincronizar carrito: ${result.message}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Excepción sincronizando carrito: ${e.message}")
                }
            }
        }

        Log.d(TAG, "Items ahora: ${_items.value.size}, total=${_total.value}")
    }

    fun actualizarCantidad(producto: Producto, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            _items.update { currentItems ->
                currentItems.filterNot { it.producto.producto_id == producto.producto_id }
            }
        } else {
            _items.update { currentItems ->
                currentItems.map {
                    if (it.producto.producto_id == producto.producto_id) it.copy(cantidad = nuevaCantidad) else it
                }
            }
        }
        recalcularTotal()

        // Sincronizar si está logueado
        if (TokenManager.isLoggedIn()) {
            viewModelScope.launch {
                try {
                    val userId = TokenManager.getUserId()
                    val result = cartSyncRepository.sincronizarCarritoConReact(userId, _items.value)
                    when (result) {
                        is CartSyncRepository.SyncResult.Success -> Log.d(TAG, "Carrito sincronizado tras actualizar cantidad")
                        is CartSyncRepository.SyncResult.Error -> Log.w(TAG, "Fallo sincronizar carrito: ${result.message}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Excepción sincronizando carrito: ${e.message}")
                }
            }
        }
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
        recalcularTotal()

        if (TokenManager.isLoggedIn()) {
            viewModelScope.launch {
                try {
                    val userId = TokenManager.getUserId()
                    val result = cartSyncRepository.sincronizarCarritoConReact(userId, _items.value)
                    when (result) {
                        is CartSyncRepository.SyncResult.Success -> Log.d(TAG, "Carrito vacío sincronizado")
                        is CartSyncRepository.SyncResult.Error -> Log.w(TAG, "Fallo sincronizar carrito vacío: ${result.message}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Excepción sincronizando carrito vacío: ${e.message}")
                }
            }
        }
    }

    private fun recalcularTotal() {
        _total.value = _items.value.sumOf { item ->
            val price = item.producto.price
            (price ?: 0.0) * item.cantidad
        }
    }

    /**
     * Helper: obtener cantidad en carrito para un producto concreto
     */
    fun cantidadParaProducto(productoId: Int): Int {
        return _items.value.find { it.producto.producto_id == productoId }?.cantidad ?: 0
    }
}
