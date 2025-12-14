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

    // LiveData para los errores de validación
    private val _telefonoError = MutableLiveData<String?>()
    val telefonoError: LiveData<String?> = _telefonoError

    private val _direccionError = MutableLiveData<String?>()
    val direccionError: LiveData<String?> = _direccionError

    private val _regionError = MutableLiveData<String?>()
    val regionError: LiveData<String?> = _regionError

    private val _comunaError = MutableLiveData<String?>()
    val comunaError: LiveData<String?> = _comunaError

    // LiveData para la validez del formulario
    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid: LiveData<Boolean> = _isFormValid

    fun validarFormulario(telefono: String, direccion: String, region: String, comuna: String) {
        val isTelefonoValid = telefono.length == 9 && telefono.all { it.isDigit() }
        val isDireccionValid = direccion.isNotBlank()
        val isRegionValid = region.isNotBlank()
        val isComunaValid = comuna.isNotBlank()

        _telefonoError.value = if (isTelefonoValid) null else "El teléfono debe tener 9 dígitos."
        _direccionError.value = if (isDireccionValid) null else "La dirección es obligatoria."
        _regionError.value = if (isRegionValid) null else "La región es obligatoria."
        _comunaError.value = if (isComunaValid) null else "La comuna es obligatoria."

        _isFormValid.value = isTelefonoValid && isDireccionValid && isRegionValid && isComunaValid
    }

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

                    val orderItems = cartItems.map { cartItem ->
                        ItemOrden(
                            productoId = cartItem.producto.producto_id,
                            cantidad = cartItem.cantidad,
                            precioUnitario = cartItem.producto.price,
                            productoNombre = cartItem.producto.producto_nombre,
                            productoImagen = cartItem.producto.imageUrl
                        )
                    }

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
