package com.example.mimascota.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.repository.OrdenRepository
import kotlinx.coroutines.launch

/**
 * MisPedidosViewModel: ViewModel para la pantalla de mis pedidos
 */
class MisPedidosViewModel(private val tokenManager: com.example.mimascota.util.TokenManager) : ViewModel() {
    private val repository = OrdenRepository()

    // LiveData para órdenes
    private val _misOrdenes = MutableLiveData<List<OrdenHistorial>>()
    val misOrdenes: LiveData<List<OrdenHistorial>> = _misOrdenes

    // LiveData para loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData para órden seleccionada
    private val _ordenSeleccionada = MutableLiveData<OrdenHistorial?>()
    val ordenSeleccionada: LiveData<OrdenHistorial?> = _ordenSeleccionada

    /**
     * Cargar órdenes del usuario
     */
    fun cargarOrdenes(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerMisOrdenes(usuarioId).fold(
                onSuccess = { ordenes ->
                    // Ordenar por fecha descendente
                    _misOrdenes.value = ordenes.sortedByDescending { it.fecha }
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error desconocido"
                    _misOrdenes.value = emptyList()
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Cargar detalle de una orden
     */
    fun cargarDetalleOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerDetalleOrden(ordenId).fold(
                onSuccess = { orden ->
                    _ordenSeleccionada.value = orden
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error desconocido"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Cancelar una orden
     */
    fun cancelarOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.cancelarOrden(ordenId).fold(
                onSuccess = {
                    _error.value = "Orden cancelada exitosamente"
                    _isLoading.value = false
                    // Recargar la lista
                    val usuarioId = (_misOrdenes.value?.firstOrNull()?.usuarioId) ?: return@launch
                    cargarOrdenes(usuarioId)
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cancelar la orden"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Limpiar error
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Cargar órdenes del usuario usando el ID del token
     */
    fun cargarMisOrdenes() {
        val usuarioId = tokenManager.getUserId()
        if (usuarioId > 0) {
            cargarOrdenes(usuarioId)
        } else {
            _error.value = "Usuario no autenticado"
            _misOrdenes.value = emptyList()
        }
    }
}
