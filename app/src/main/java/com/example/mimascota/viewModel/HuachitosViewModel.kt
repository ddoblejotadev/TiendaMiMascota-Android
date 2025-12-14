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

    fun cargarAnimales(comunaId: Int, tipo: String? = null, estado: String? = null, genero: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getAnimales(comunaId, tipo, estado, genero)
                .onSuccess { listaDeAnimales ->
                    var filteredList = listaDeAnimales
                    if (tipo != null) {
                        filteredList = filteredList.filter { it.tipo?.lowercase() == tipo.lowercase() }
                    }
                    if (estado != null) {
                        filteredList = filteredList.filter { it. estado?.lowercase() == estado.lowercase() }
                    }
                    if (genero != null) {
                        filteredList = filteredList.filter { it.genero?.lowercase() == genero.lowercase() }
                    }
                    _animales.value = filteredList
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
