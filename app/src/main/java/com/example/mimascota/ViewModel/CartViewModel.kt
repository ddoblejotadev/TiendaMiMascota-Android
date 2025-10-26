package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel: ViewModel()  {

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    // Obtener la cantidad de un producto específico
    fun getCantidadProducto(productoId: Int): Int {
        return _carrito.value.find { it.producto.id == productoId }?.cantidad ?: 0
    }

    // Agregar un producto (incrementa la cantidad si ya existe)
    fun agregarAlCarrito(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val itemExistente = carritoActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            carritoActual.add(CartItem(producto, 1))
        }

        _carrito.value = carritoActual
    }

    // Disminuir cantidad de un producto
    fun disminuirCantidad(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val itemExistente = carritoActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            if (itemExistente.cantidad > 1) {
                itemExistente.cantidad--
            } else {
                carritoActual.remove(itemExistente)
            }
        }

        _carrito.value = carritoActual
    }

    // Eliminar un producto completamente del carrito
    fun eliminarDelCarrito(producto: Producto) {
        _carrito.value = _carrito.value.filter { it.producto.id != producto.id }
    }

    // Vaciar el carrito
    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }

    // Obtener el total del carrito
    fun getTotal(): Int {
        return _carrito.value.sumOf { it.subtotal }
    }

    // Obtener cantidad total de items
    fun getTotalItems(): Int {
        return _carrito.value.sumOf { it.cantidad }
    }
}