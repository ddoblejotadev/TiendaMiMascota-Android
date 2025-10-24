package com.example.mimascota.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mimascota.Model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel: ViewModel()  {

    private val _carrito = MutableStateFlow<List<Producto>>(emptyList())
    val carrito: StateFlow<List<Producto>> = _carrito

    fun agregarAlCarrito(producto: Producto) {
        _carrito.value = _carrito.value + producto
    }

    fun eliminarDelCarrito(producto: Producto) {
        _carrito.value = _carrito.value - producto
    }

    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }
}