package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * CartViewModel: Maneja la lógica del carrito de compras
 *
 * Funcionalidades:
 * - Agregar/eliminar productos del carrito
 * - Calcular totales
 * - Procesar compras con validación de stock
 */
sealed class AgregarResultado {
    object Exito : AgregarResultado()
    data class ExcedeStock(val stockDisponible: Int, val cantidadEnCarrito: Int) : AgregarResultado()
}

class CartViewModel : ViewModel() {

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    private val _ultimoPedido = MutableStateFlow<List<CartItem>>(emptyList())
    val ultimoPedido: StateFlow<List<CartItem>> = _ultimoPedido.asStateFlow()

    private val _totalUltimoPedido = MutableStateFlow(0)
    val totalUltimoPedido: StateFlow<Int> = _totalUltimoPedido.asStateFlow()

    private val _numeroUltimoPedido = MutableStateFlow("")
    val numeroUltimoPedido: StateFlow<String> = _numeroUltimoPedido.asStateFlow()

    fun agregarAlCarrito(producto: Producto): AgregarResultado {
        val cantidadActual = _carrito.value.find { it.producto.id == producto.id }?.cantidad ?: 0
        val itemExistente = _carrito.value.find { it.producto.id == producto.id }

        _carrito.value = if (itemExistente != null) {
            _carrito.value.map {
                if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + 1) else it
            }
        } else {
            _carrito.value + CartItem(producto, 1)
        }

        val nuevaCantidad = cantidadActual + 1
        return if (nuevaCantidad > producto.stock) {
            AgregarResultado.ExcedeStock(producto.stock, nuevaCantidad)
        } else {
            AgregarResultado.Exito
        }
    }

    fun disminuirCantidad(producto: Producto) {
        _carrito.value = _carrito.value.mapNotNull { item ->
            when {
                item.producto.id != producto.id -> item
                item.cantidad > 1 -> item.copy(cantidad = item.cantidad - 1)
                else -> null
            }
        }
    }

    fun eliminarDelCarrito(producto: Producto) {
        _carrito.value = _carrito.value.filter { it.producto.id != producto.id }
    }

    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }

    fun getTotal(): Int = _carrito.value.sumOf { it.subtotal }

    fun getTotalItems(): Int = _carrito.value.sumOf { it.cantidad }

    private fun generarNumeroPedido(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(6)
        val random = Random.nextInt(1000, 9999)
        return "MM-$timestamp-$random"
    }

    private fun procesarCompra() {
        _ultimoPedido.value = _carrito.value.toList()
        _totalUltimoPedido.value = getTotal()
        _numeroUltimoPedido.value = generarNumeroPedido()
        vaciarCarrito()
    }

    fun intentarProcesarCompra(): String? {
        // Validar stock
        if (_carrito.value.any { it.cantidad > it.producto.stock }) {
            return "STOCK"
        }

        // Simular errores aleatorios (80% éxito, 20% error)
        return when (Random.nextInt(1, 11)) {
            in 1..8 -> {
                procesarCompra()
                null
            }
            9 -> "PAGO"
            else -> "CONEXION"
        }
    }
}
