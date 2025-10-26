package com.example.mimascota.ViewModel

import androidx.lifecycle.ViewModel
import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

// Clase sellada para resultado de agregar al carrito
sealed class AgregarResultado {
    object Exito : AgregarResultado()
    data class ExcedeStock(val stockDisponible: Int, val cantidadEnCarrito: Int) : AgregarResultado()
}

class CartViewModel: ViewModel()  {

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    // Almacenar temporalmente el último pedido realizado
    private val _ultimoPedido = MutableStateFlow<List<CartItem>>(emptyList())
    val ultimoPedido: StateFlow<List<CartItem>> = _ultimoPedido.asStateFlow()

    private val _totalUltimoPedido = MutableStateFlow(0)
    val totalUltimoPedido: StateFlow<Int> = _totalUltimoPedido.asStateFlow()

    private val _numeroUltimoPedido = MutableStateFlow("")
    val numeroUltimoPedido: StateFlow<String> = _numeroUltimoPedido.asStateFlow()

    // Agregar un producto (siempre agrega, pero devuelve info sobre stock)
    // Retorna AgregarResultado con información para mostrar advertencias
    fun agregarAlCarrito(producto: Producto): AgregarResultado {
        // Obtener cantidad actual antes de agregar
        val cantidadActual = _carrito.value.find { it.producto.id == producto.id }?.cantidad ?: 0

        // Agregar al carrito (siempre)
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

        // Calcular nueva cantidad después de agregar
        val nuevaCantidad = cantidadActual + 1

        // Verificar si excede stock DESPUÉS de agregar
        return if (nuevaCantidad > producto.stock) {
            AgregarResultado.ExcedeStock(producto.stock, nuevaCantidad)
        } else {
            AgregarResultado.Exito
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

    // Generar número de pedido único
    fun generarNumeroPedido(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random.nextInt(1000, 9999)
        return "MM-${timestamp.toString().takeLast(6)}-$random"
    }

    // Obtener copia del carrito actual
    fun getCarritoCopia(): List<CartItem> {
        return _carrito.value.toList()
    }

    // Procesar la compra y guardar los datos del pedido
    fun procesarCompra() {
        _ultimoPedido.value = _carrito.value.toList()
        _totalUltimoPedido.value = getTotal()
        _numeroUltimoPedido.value = generarNumeroPedido()
        vaciarCarrito()
    }

    // Intentar procesar compra con validación de stock y simulación de errores
    // Retorna: null si es exitosa, o el tipo de error si falla
    fun intentarProcesarCompra(): String? {
        // PASO 1: Validar stock PRIMERO (esto es real, no simulado)
        val sinStock = _carrito.value.any { item ->
            item.cantidad > item.producto.stock
        }

        if (sinStock) {
            // Si hay productos sin stock suficiente, retornar error de stock
            return "STOCK"
        }

        // PASO 2: Si el stock es suficiente, simular otros errores aleatorios
        // Generar número aleatorio entre 1 y 10
        val probabilidad = Random.nextInt(1, 11)

        // 80% de probabilidad de éxito (1-8)
        // 20% de probabilidad de error (9-10)
        return when {
            probabilidad <= 8 -> {
                // Compra exitosa
                procesarCompra()
                null // Sin error
            }
            probabilidad == 9 -> {
                // Error de pago (10% probabilidad)
                "PAGO"
            }
            else -> {
                // Error de conexión (10% probabilidad)
                "CONEXION"
            }
        }
    }

    // Verificar si un producto tiene stock suficiente
    fun tieneStockSuficiente(producto: Producto): Boolean {
        val cantidadEnCarrito = _carrito.value.find { it.producto.id == producto.id }?.cantidad ?: 0
        return cantidadEnCarrito < producto.stock
    }

    // Obtener stock disponible de un producto (considerando lo que ya está en el carrito)
    fun getStockDisponible(producto: Producto): Int {
        val cantidadEnCarrito = _carrito.value.find { it.producto.id == producto.id }?.cantidad ?: 0
        return producto.stock - cantidadEnCarrito
    }
}