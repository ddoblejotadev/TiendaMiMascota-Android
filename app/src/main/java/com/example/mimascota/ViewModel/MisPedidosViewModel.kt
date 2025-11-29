package com.example.mimascota.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Orden
import com.example.mimascota.repository.OrdenRepository
import kotlinx.coroutines.launch

/**
 * MisPedidosViewModel: ViewModel para la pantalla de mis pedidos
 */
class MisPedidosViewModel : ViewModel() {
    private val repository = OrdenRepository()

    // LiveData para órdenes
    private val _ordenes = MutableLiveData<List<Orden>>()
    val ordenes: LiveData<List<Orden>> = _ordenes

    // LiveData para loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData para órden seleccionada
    private val _ordenSeleccionada = MutableLiveData<Orden?>()
    val ordenSeleccionada: LiveData<Orden?> = _ordenSeleccionada

    /**
     * Cargar órdenes del usuario
     */
    fun cargarOrdenes(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerMisOrdenes(usuarioId).fold(
                onSuccess = { ordenes ->
                    // Ordenar por fecha descendente
                    _ordenes.value = ordenes.sortedByDescending { it.fecha }
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error desconocido"
                    _ordenes.value = emptyList()
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
                    val usuarioId = (_ordenes.value?.firstOrNull()?.usuarioId) ?: return@launch
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
    fun clearError() {
        _error.value = null
    }
}

