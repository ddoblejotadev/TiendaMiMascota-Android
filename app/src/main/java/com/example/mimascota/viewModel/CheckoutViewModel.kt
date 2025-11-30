package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.*
import com.example.mimascota.repository.CheckoutRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val checkoutRepository = CheckoutRepository()

    // ========== LiveData para la UI ==========

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _verificacionStock = MutableLiveData<VerificarStockResponse?>()
    val verificacionStock: LiveData<VerificarStockResponse?> = _verificacionStock

    private val _ordenCreada = MutableLiveData<OrdenResponse?>()
    val ordenCreada: LiveData<OrdenResponse?> = _ordenCreada

    private val _navegarAConfirmacion = MutableLiveData<Event<String>>()
    val navegarAConfirmacion: LiveData<Event<String>> = _navegarAConfirmacion

    private val _navegarARechazo = MutableLiveData<Event<String>>()
    val navegarARechazo: LiveData<Event<String>> = _navegarARechazo

    private val _ordenesHistorial = MutableLiveData<List<OrdenHistorial>>(emptyList())
    val ordenesHistorial: LiveData<List<OrdenHistorial>> = _ordenesHistorial

    // ========== Lógica del ViewModel ==========

    fun verificarStock(items: List<StockItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = checkoutRepository.verificarStock(items)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    val verificacion = result.data
                    _verificacionStock.value = verificacion
                    if (!verificacion.disponible) {
                        Log.w("CheckoutViewModel", "Stock no disponible para algunos productos")
                        _navegarARechazo.value = Event("STOCK_INSUFICIENTE")
                    } else {
                        Log.d("CheckoutViewModel", "Stock disponible para todos los productos")
                    }
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    Log.e("CheckoutViewModel", "Error al verificar stock: ${result.message}")
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun crearOrden(cartItems: List<CartItem>, datosEnvio: DatosEnvio, subtotal: Double, total: Double) {
        viewModelScope.launch {
            _isLoading.value = true

            val itemsOrden = cartItems.map {
                ItemOrden(
                    productoId = it.producto.producto_id,
                    cantidad = it.cantidad,
                    precioUnitario = it.producto.price
                )
            }

            val userId = tokenManager.getUserId()
            val esInvitado = userId <= 0

            when (val result = checkoutRepository.crearOrden(userId.toLong(), esInvitado, datosEnvio, itemsOrden, subtotal, total)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    val orden = result.data
                    _ordenCreada.value = orden
                    _navegarAConfirmacion.value = Event(orden.numeroOrden)
                    Log.d("CheckoutViewModel", "Orden creada con éxito: ${orden.numeroOrden}")
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _navegarARechazo.value = Event("ERROR_CREACION")
                    Log.e("CheckoutViewModel", "Error al crear orden: ${result.message}")
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun cargarHistorialOrdenes() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.getUserId()
            if (userId > 0) {
                when (val result = checkoutRepository.obtenerOrdenesUsuario(userId.toLong())) {
                    is CheckoutRepository.CheckoutResult.Success -> {
                        _ordenesHistorial.value = result.data.sortedByDescending { it.fecha }
                    }
                    is CheckoutRepository.CheckoutResult.Error -> {
                        _error.value = result.message
                    }
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Obtiene una orden del historial y la establece como la orden "creada" para reutilizar la pantalla de confirmación
     */
    fun verDetalleDeOrden(ordenId: Long) {
        val orden = _ordenesHistorial.value?.firstOrNull { it.id == ordenId }
        if (orden != null) {
            _ordenCreada.value = OrdenResponse(
                id = orden.id,
                numeroOrden = orden.numeroOrden,
                fecha = orden.fecha,
                estado = orden.estado,
                total = orden.total,
                mensaje = "Orden cargada desde el historial",
                items = orden.productos.map { 
                    ProductoOrden(it.productoId, it.nombre, it.cantidad, it.precioUnitario, it.imagen)
                }
            )
            _navegarAConfirmacion.value = Event(orden.numeroOrden)
        } else {
            _error.value = "No se encontró la orden en el historial."
        }
    }


    fun limpiarError() {
        _error.value = null
    }
}

/**
 * Wrapper para eventos de navegación, para evitar que se disparen múltiples veces
 */
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
