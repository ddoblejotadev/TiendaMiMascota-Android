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

    // Agregar un producto (incrementa la cantidad si ya existe)
    fun agregarAlCarrito(producto: Producto) {
        _carrito.value = _carrito.value.map { item ->
            if (item.producto.id == producto.id) {
                // Crear nueva instancia con cantidad incrementada
                CartItem(item.producto, item.cantidad + 1)
            } else {
                item
            }
        }.let { lista ->
            // Si el producto no existía, agregarlo
            if (lista.none { it.producto.id == producto.id }) {
                lista + CartItem(producto, 1)
            } else {
                lista
            }
        }
    }

    // Disminuir cantidad de un producto
    fun disminuirCantidad(producto: Producto) {
        _carrito.value = _carrito.value.mapNotNull { item ->
            if (item.producto.id == producto.id) {
                // Si la cantidad es mayor a 1, crear nueva instancia con cantidad decrementada
                if (item.cantidad > 1) {
                    CartItem(item.producto, item.cantidad - 1)
                } else {
                    // Si la cantidad es 1, eliminarlo (retornar null)
                    null
                }
            } else {
                item
            }
        }
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