package com.example.mimascota.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.OrdenHistorial
import com.example.mimascota.repository.OrdenRepository
import com.example.mimascota.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * MisPedidosViewModel: ViewModel para la pantalla de mis pedidos
 */
@Suppress("unused")
class MisPedidosViewModel(private val tokenManager: com.example.mimascota.util.TokenManager) : ViewModel() {
    private val repository = OrdenRepository()
    private val authRepository = AuthRepository()

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
                onFailure = { err ->
                    _error.value = err.message ?: "Error desconocido"
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
                onFailure = { err ->
                    _error.value = err.message ?: "Error desconocido"
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
                    // Recargar la lista si conocemos el usuario
                    val usuarioId = (_misOrdenes.value?.firstOrNull()?.usuarioId) ?: return@launch
                    cargarOrdenes(usuarioId)
                },
                onFailure = { err ->
                    _error.value = err.message ?: "Error al cancelar la orden"
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
     * Cargar órdenes del usuario usando el ID del token. Si no hay userId en TokenManager,
     * intentamos recuperar el perfil desde el backend y usar el id que devuelva.
     */
    fun cargarMisOrdenes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            var usuarioId = tokenManager.getUserId()
            if (usuarioId <= 0) {
                // Primero intentar extraer el userId del token JWT (fallback sin llamar al backend)
                val idFromToken = tokenManager.getUserIdFromToken()
                if (idFromToken != null && idFromToken > 0) {
                    usuarioId = idFromToken
                } else {
                    // Intentar recuperar perfil desde el backend
                    try {
                        val perfilResult = authRepository.obtenerUsuario()
                        if (perfilResult.isSuccess) {
                            val usuario = perfilResult.getOrNull()!!
                            tokenManager.saveUsuario(usuario)
                            usuarioId = usuario.usuarioId.toLong()
                        } else {
                            _error.value = "Usuario no autenticado"
                            _misOrdenes.value = emptyList()
                            _isLoading.value = false
                            return@launch
                        }
                    } catch (e: Exception) {
                        _error.value = "No se pudo recuperar perfil: ${e.message}"
                        _misOrdenes.value = emptyList()
                        _isLoading.value = false
                        return@launch
                    }
                }
            }

            // Si llegamos aquí, tenemos un usuarioId válido
            cargarOrdenes(usuarioId)
        }
    }
}
