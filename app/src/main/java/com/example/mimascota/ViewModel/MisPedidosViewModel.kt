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
class MisPedidosViewModel(
    private val repository: OrdenRepository = OrdenRepository()
) : ViewModel() {

    // Lista de órdenes
    private val _ordenes = MutableLiveData<List<Orden>>()
    val ordenes: LiveData<List<Orden>> = _ordenes

    // Loading y error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Orden seleccionada
    private val _ordenSeleccionada = MutableLiveData<Orden?>()
    val ordenSeleccionada: LiveData<Orden?> = _ordenSeleccionada

    // Carga órdenes del usuario
    fun cargarOrdenes(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerMisOrdenes(usuarioId).fold(
                onSuccess = {
                    _ordenes.value = it.sortedByDescending { o -> o.fecha }
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message ?: "Error desconocido"
                    _ordenes.value = emptyList()
                    _isLoading.value = false
                }
            )
        }
    }

    // Carga detalle de orden
    fun cargarDetalleOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerDetalleOrden(ordenId).fold(
                onSuccess = {
                    _ordenSeleccionada.value = it
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message ?: "Error desconocido"
                    _isLoading.value = false
                }
            )
        }
    }

    // Cancela orden
    fun cancelarOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.cancelarOrden(ordenId).fold(
                onSuccess = {
                    _error.value = "Orden cancelada exitosamente"
                    _isLoading.value = false
                    val usuarioId = _ordenes.value?.firstOrNull()?.usuarioId ?: return@launch
                    cargarOrdenes(usuarioId)
                },
                onFailure = {
                    _error.value = it.message ?: "Error al cancelar la orden"
                    _isLoading.value = false
                }
            )
        }
    }

    // Limpia error
    fun clearError() {
        _error.value = null
    }
}

