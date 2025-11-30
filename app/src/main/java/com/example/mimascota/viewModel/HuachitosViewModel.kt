package com.example.mimascota.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Animal
import com.example.mimascota.repository.HuachitosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HuachitosViewModel : ViewModel() {
    private val repository = HuachitosRepository()

    private val _animales = MutableStateFlow<List<Animal>>(emptyList())
    val animales = _animales.asStateFlow()

    private val _selectedAnimal = MutableStateFlow<Animal?>(null)
    val selectedAnimal = _selectedAnimal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarAnimales(comunaId: Int, tipo: String? = null, estado: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getAnimales(comunaId, tipo, estado)
                .onSuccess { listaDeAnimales ->
                    _animales.value = listaDeAnimales
                }
                .onFailure { throwable ->
                    _error.value = throwable.message ?: "Error desconocido"
                }
            _isLoading.value = false
        }
    }

    fun getAnimalById(animalId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getAnimalById(animalId)
                .onSuccess { animal ->
                    _selectedAnimal.value = animal
                }
                .onFailure { throwable ->
                    _error.value = throwable.message ?: "Error desconocido"
                }
            _isLoading.value = false
        }
    }
}
