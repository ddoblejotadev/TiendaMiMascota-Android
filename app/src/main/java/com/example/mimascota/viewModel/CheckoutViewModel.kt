package com.example.mimascota.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.*
import com.example.mimascota.repository.CheckoutRepository
import com.example.mimascota.util.Event
import com.example.mimascota.util.TokenManager
import kotlinx.coroutines.launch

class CheckoutViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val checkoutRepository = CheckoutRepository()

    private val _ordenCreada = MutableLiveData<OrdenResponse>()
    val ordenCreada: LiveData<OrdenResponse> = _ordenCreada

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _navegarARechazo = MutableLiveData<Event<String>>()
    val navegarARechazo: LiveData<Event<String>> = _navegarARechazo

    fun crearOrden(
        cartItems: List<CartItem>,
        datosEnvio: DatosEnvio,
        subtotal: Double,
        total: Double
    ) {
        viewModelScope.launch {
            val stockItems = cartItems.map { StockItem(productoId = it.producto.producto_id, cantidad = it.cantidad) }

            when (val stockResult = checkoutRepository.verificarStock(stockItems)) {
                is CheckoutRepository.CheckoutResult.Success -> {
                    val userId = tokenManager.getUserId()
                    if (userId == null) {
                        _error.postValue("Error: Usuario no autenticado.")
                        return@launch
                    }

                    val orderItems = cartItems.map { ItemOrden(productoId = it.producto.producto_id, cantidad = it.cantidad, precioUnitario = it.producto.price) }

                    when (val ordenResult = checkoutRepository.crearOrden(
                        usuarioId = userId.toLong(),
                        esInvitado = false,
                        datosEnvio = datosEnvio,
                        items = orderItems,
                        subtotal = subtotal,
                        total = total
                    )) {
                        is CheckoutRepository.CheckoutResult.Success -> {
                            _ordenCreada.postValue(ordenResult.data!!)
                        }
                        is CheckoutRepository.CheckoutResult.Error -> {
                            _error.postValue(ordenResult.message)
                            if (ordenResult.message.contains("pago", ignoreCase = true)) {
                                _navegarARechazo.postValue(Event("pago"))
                            } else {
                                _navegarARechazo.postValue(Event("desconocido"))
                            }
                        }
                        is CheckoutRepository.CheckoutResult.Loading -> { /* No action needed */ }
                    }
                }
                is CheckoutRepository.CheckoutResult.Error -> {
                    _error.postValue(stockResult.message)
                    _navegarARechazo.postValue(Event("stock"))
                }
                is CheckoutRepository.CheckoutResult.Loading -> { /* No action needed */ }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
