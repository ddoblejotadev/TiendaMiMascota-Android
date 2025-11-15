package com.example.mimascota.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.Model.*
import com.example.mimascota.repository.CheckoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * CheckoutViewModel: ViewModel para gestionar el flujo de checkout
 * Actualizado para funcionar con backend en Render y tipos correctos
 */
class CheckoutViewModel : ViewModel() {

    private val repository = CheckoutRepository()
    private val TAG = "CheckoutViewModel"

    // Estado de la verificación de stock
    private val _stockVerificado = MutableStateFlow<VerificarStockResponse?>(null)
    val stockVerificado: StateFlow<VerificarStockResponse?> = _stockVerificado.asStateFlow()

    // Estado de la orden creada
    private val _ordenCreada = MutableStateFlow<OrdenResponse?>(null)
    val ordenCreada: StateFlow<OrdenResponse?> = _ordenCreada.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Historial de órdenes
    private val _ordenes = MutableStateFlow<List<OrdenHistorial>>(emptyList())
    val ordenes: StateFlow<List<OrdenHistorial>> = _ordenes.asStateFlow()

    /**
     * Verifica el stock de los productos antes del checkout
     */
    fun verificarStock(items: List<StockItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d(TAG, "Verificando stock para ${items.size} productos")

            when (val result = repository.verificarStock(items)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _stockVerificado.value = result.data
                    _error.value = null
                    Log.d(TAG, "Stock verificado exitosamente")
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _stockVerificado.value = null
                    Log.e(TAG, "Error al verificar stock: ${result.message}")
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Crea una nueva orden de compra con TODOS los campos obligatorios
     * IMPORTANTE: Asegúrate de incluir subtotal, total, estado, es_invitado, pais
     */
    fun crearOrden(
        usuarioId: Long,
        esInvitado: Boolean,
        datosEnvio: DatosEnvio,
        items: List<ItemOrden>,
        subtotal: Int,
        total: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Creando orden para usuario $usuarioId")
            Log.d(TAG, "Items: $items")
            Log.d(TAG, "Subtotal: $subtotal, Total: $total")
            Log.d(TAG, "Datos envío: $datosEnvio")

            when (val result = repository.crearOrden(
                usuarioId,
                esInvitado,
                datosEnvio,
                items,
                subtotal,
                total
            )) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _ordenCreada.value = result.data
                    _error.value = null
                    Log.d(TAG, "Orden creada exitosamente: ${result.data.numeroOrden}")
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _ordenCreada.value = null
                    Log.e(TAG, "Error al crear orden: ${result.message}")
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Obtiene el historial de órdenes del usuario
     */
    fun cargarOrdenesUsuario(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d(TAG, "Cargando órdenes del usuario $usuarioId")

            when (val result = repository.obtenerOrdenesUsuario(usuarioId)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _ordenes.value = result.data.sortedByDescending { it.fecha }
                    _error.value = null
                    Log.d(TAG, "Órdenes cargadas: ${result.data.size}")
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _ordenes.value = emptyList()
                    Log.e(TAG, "Error al cargar órdenes: ${result.message}")
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Cancela una orden
     */
    fun cancelarOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.cancelarOrden(ordenId)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    // Recargar la lista de órdenes después de cancelar
                    val usuarioId = _ordenes.value.firstOrNull()?.usuarioId
                    if (usuarioId != null) {
                        cargarOrdenesUsuario(usuarioId)
                    }
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el estado de la orden creada
     */
    fun limpiarOrdenCreada() {
        _ordenCreada.value = null
    }

    /**
     * Limpia el estado de error
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Limpia el estado de stock verificado
     */
    fun limpiarStockVerificado() {
        _stockVerificado.value = null
    }
}

