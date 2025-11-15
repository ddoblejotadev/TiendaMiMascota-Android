package com.example.mimascota.ViewModel

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
 */
class CheckoutViewModel : ViewModel() {

    private val repository = CheckoutRepository()

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
    private val _ordenes = MutableStateFlow<List<Orden>>(emptyList())
    val ordenes: StateFlow<List<Orden>> = _ordenes.asStateFlow()

    /**
     * Verifica el stock de los productos antes del checkout
     */
    fun verificarStock(items: List<StockItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.verificarStock(items)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _stockVerificado.value = result.data
                    _error.value = null
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _stockVerificado.value = null
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Crea una nueva orden de compra
     */
    fun crearOrden(
        usuarioId: Int,
        datosEnvio: DatosEnvio,
        items: List<OrdenItem>,
        total: Int,
        esInvitado: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.crearOrden(usuarioId, datosEnvio, items, total, esInvitado)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _ordenCreada.value = result.data
                    _error.value = null
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _ordenCreada.value = null
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Obtiene el historial de órdenes del usuario
     */
    fun cargarOrdenesUsuario(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = repository.obtenerOrdenesUsuario(usuarioId)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    _ordenes.value = result.data
                    _error.value = null
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.value = result.message
                    _ordenes.value = emptyList()
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el estado después de completar el checkout
     */
    fun limpiarEstado() {
        _stockVerificado.value = null
        _ordenCreada.value = null
        _error.value = null
    }

    /**
     * Limpia solo el error
     */
    fun clearError() {
        _error.value = null
    }
}

