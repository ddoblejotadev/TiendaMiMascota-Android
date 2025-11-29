package com.example.mimascota.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.OrdenHistorial // Corregido: usar OrdenHistorial
import com.example.mimascota.repository.OrdenRepository
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class MisPedidosViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val repository = OrdenRepository()

    // Corregido: usar OrdenHistorial
    private val _misOrdenes = MutableLiveData<List<OrdenHistorial>>()
    val misOrdenes: LiveData<List<OrdenHistorial>> = _misOrdenes

    // Corregido: usar OrdenHistorial
    private val _ordenSeleccionada = MutableLiveData<OrdenHistorial?>()
    val ordenSeleccionada: LiveData<OrdenHistorial?> = _ordenSeleccionada

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarMisOrdenes() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.getUserId()

            if (userId > 0) {
                // Corregido: convertir userId a Long
                val result = repository.obtenerMisOrdenes(userId.toLong())

                result.onSuccess {
                    // Corregido: El tipo de dato ahora es List<OrdenHistorial>
                    _misOrdenes.value = it.sortedByDescending { orden -> orden.fecha }
                }.onFailure {
                    _error.value = it.message
                }
            } else {
                _error.value = "Usuario no autenticado."
            }

            _isLoading.value = false
        }
    }

    fun cargarDetalleOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.obtenerDetalleOrden(ordenId)

            result.onSuccess {
                // Corregido: El tipo de dato ahora es OrdenHistorial
                _ordenSeleccionada.value = it
            }.onFailure {
                _error.value = it.message
            }
            
            _isLoading.value = false
        }
    }

    fun cancelarOrden(ordenId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.cancelarOrden(ordenId)
            result.onSuccess {
                // Refrescar la lista de órdenes después de cancelar
                cargarMisOrdenes()
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
