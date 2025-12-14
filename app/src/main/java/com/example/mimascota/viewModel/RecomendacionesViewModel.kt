
package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecomendacionesViewModel : ViewModel() {

    private val repository = ProductoRepository()

    // Holds all products from the repository
    private val _allProductos = MutableStateFlow<List<Producto>>(emptyList())

    // Dynamically generates a distinct, sorted list of animal types from all products
    val tiposAnimal: StateFlow<List<String>> = _allProductos.map { productos ->
        productos.mapNotNull { it.tipoMascota }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Dynamically generates a distinct, sorted list of categories
    val categorias: StateFlow<List<String>> = _allProductos.map { productos ->
        productos.mapNotNull { it.category }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private val _tipoAnimalSeleccionado = MutableStateFlow("")
    val tipoAnimalSeleccionado: StateFlow<String> = _tipoAnimalSeleccionado.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    private val _raza = MutableStateFlow("")
    val raza: StateFlow<String> = _raza.asStateFlow()

    private val _edad = MutableStateFlow("")
    val edad: StateFlow<String> = _edad.asStateFlow()

    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _recomendaciones = MutableStateFlow<List<Producto>>(emptyList())
    val recomendaciones: StateFlow<List<Producto>> = _recomendaciones.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    companion object {
        private const val TAG = "RecomendacionesVM"
    }

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            val result = repository.getAllProductos()
            if (result is ProductoRepository.ProductoResult.Success) {
                _allProductos.value = result.data
                Log.d(TAG, "✅ Productos cargados: ${_allProductos.value.size} en total.")
            } else {
                Log.e(TAG, "❌ Error al cargar productos.")
            }
        }
    }

    fun onTipoAnimalChange(tipo: String) {
        _tipoAnimalSeleccionado.value = tipo
    }

    fun onCategoriaChange(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun onRazaChange(raza: String) {
        _raza.value = raza
    }

    fun onEdadChange(edad: String) {
        _edad.value = edad
    }

    fun onPesoChange(peso: String) {
        _peso.value = peso
    }

    fun buscarRecomendaciones() {
        viewModelScope.launch {
            _isSearching.value = true
            Log.d(TAG, "--- 🚀 Iniciando Búsqueda de Recomendaciones (v_ESTANDAR) ---")
            Log.d(TAG, "Inputs: Tipo=${_tipoAnimalSeleccionado.value}, Cat=${_categoriaSeleccionada.value}, Raza/Tamaño=${_raza.value}, Edad=${_edad.value}")

            val recommendations = withContext(Dispatchers.Default) { // Heavy work on background thread
                val productosPorTipo = _allProductos.value.filter { producto ->
                    _tipoAnimalSeleccionado.value.isBlank() ||
                    producto.tipoMascota?.equals(_tipoAnimalSeleccionado.value, ignoreCase = true) == true ||
                    producto.tipoMascota.isNullOrBlank() || producto.tipoMascota.equals("Otro", ignoreCase = true) || producto.tipoMascota.equals("Ambos", ignoreCase = true)
                }
                Log.d(TAG, "Productos tras filtro primario (Tipo Animal): ${productosPorTipo.size}")

                if (productosPorTipo.isEmpty()) {
                    Log.w(TAG, "⚠️ No quedaron productos después del filtro primario.")
                    return@withContext emptyList<Producto>()
                }

                val scoredProducts = productosPorTipo.map { producto ->
                    producto to calculateScore(producto)
                }.filter { it.second > 0 }

                Log.d(TAG, "Productos con score > 0: ${scoredProducts.size}")

                scoredProducts
                    .sortedByDescending { it.second } 
                    .map { it.first }
            }
            
            _recomendaciones.value = recommendations
            Log.d(TAG, "🏆 Total de recomendaciones FINALES: ${_recomendaciones.value.size}")
            Log.d(TAG, "--- ✅ Búsqueda Finalizada (v_ESTANDAR) ---")
            _isSearching.value = false
        }
    }

    private fun calculateScore(producto: Producto): Int {
        var score = 0
        val textoProducto = "${producto.producto_nombre} ${producto.description} ${producto.edad} ${producto.raza}".lowercase()
        
        val isSpecificSearch = _categoriaSeleccionada.value.isNotBlank() || _raza.value.isNotBlank() || _edad.value.isNotBlank()
        
        if (!isSpecificSearch) return 1

        // Criterio 1: Categoría (muy importante)
        if (_categoriaSeleccionada.value.isNotBlank() && producto.category.contains(_categoriaSeleccionada.value, ignoreCase = true)) {
            score += 10
        }

        // Criterio 2: Edad (importante)
        if (_edad.value.isNotBlank() && textoProducto.contains(_edad.value.lowercase().split("/")[0].trim())) {
            score += 5
        } else if (textoProducto.contains("todas las edades")) {
            score += 1
        }

        // Criterio 3: Raza/Tamaño (importante)
        if (_raza.value.isNotBlank() && textoProducto.contains(_raza.value.lowercase().replace("raza", "").trim())) {
            score += 5
        } else if (textoProducto.contains("todas las razas")) {
            score += 1
        }

        return score
    }
}
