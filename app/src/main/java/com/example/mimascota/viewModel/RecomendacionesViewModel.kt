package com.example.mimascota.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimascota.model.Producto
import com.example.mimascota.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RecomendacionesViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _allProductos = MutableStateFlow<List<Producto>>(emptyList())

    val tiposAnimal = _allProductos.combine(MutableStateFlow(Unit)) { productos, _ ->
        productos.mapNotNull { it.tipoMascota }.distinct().sorted()
    }

    val categorias = _allProductos.combine(MutableStateFlow(Unit)) { productos, _ ->
        productos.flatMap { it.category.split(",").map { it.trim() } }.filter { it.isNotBlank() }.distinct().sorted()
    }

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
        Log.d(TAG, "--- 🚀 Iniciando Búsqueda de Recomendaciones (v_FINAL_EXPERTO) ---")
        Log.d(TAG, "Inputs: Tipo=${_tipoAnimalSeleccionado.value}, Cat=${_categoriaSeleccionada.value}, Raza=${_raza.value}, Edad=${_edad.value}, Peso=${_peso.value}")

        // 1. FILTRO PRIMARIO INQUEBRANTABLE: Por tipo de animal.
        val productosPorTipo = _allProductos.value.filter { producto ->
            _tipoAnimalSeleccionado.value.isBlank() ||
            producto.tipoMascota?.equals(_tipoAnimalSeleccionado.value, ignoreCase = true) == true ||
            producto.tipoMascota.isNullOrBlank() || producto.tipoMascota.equals("Otro", ignoreCase = true)
        }
        Log.d(TAG, "Productos tras filtro primario (Tipo Animal): ${productosPorTipo.size}")

        if (productosPorTipo.isEmpty()) {
            _recomendaciones.value = emptyList()
            Log.w(TAG, "⚠️ No quedaron productos después del filtro primario.")
            return
        }

        // 2. PUNTUACIÓN DE RELEVANCIA
        val scoredProducts = productosPorTipo.map { producto ->
            producto to calculateScore(producto)
        }.filter { it.second > 0 } // <-- CERO TOLERANCIA: Solo productos con puntuación > 0

        Log.d(TAG, "Productos con score > 0: ${scoredProducts.size}")

        // 3. ORDENAR Y MOSTRAR
        _recomendaciones.value = scoredProducts
            .sortedByDescending { it.second } // Ordenar por la mejor puntuación
            .map { it.first } // Obtener solo el producto

        Log.d(TAG, "🏆 Total de recomendaciones FINALES y RELEVANTES: ${_recomendaciones.value.size}")
        Log.d(TAG, "--- ✅ Búsqueda Finalizada (v_FINAL_EXPERTO) ---")
    }

    private fun calculateScore(producto: Producto): Int {
        var score = 0
        val textoProducto = (producto.producto_nombre + " " + (producto.description ?: "")).lowercase()
        
        val isSpecificSearch = _categoriaSeleccionada.value.isNotBlank() || _raza.value.isNotBlank() || _edad.value.isNotBlank() || _peso.value.isNotBlank()
        
        // Si es una búsqueda general (solo tipo de animal), todo es relevante.
        if (!isSpecificSearch) {
            return 1
        }

        // Criterio 1: Categoría (el más importante)
        if (_categoriaSeleccionada.value.isNotBlank()) {
            val categoriasProducto = producto.category.split(',').map { it.trim().lowercase() }
            if (categoriasProducto.contains(_categoriaSeleccionada.value.lowercase())) {
                score += 10 // Puntuación muy alta por coincidencia de categoría
            }
        }

        // Criterio 2: Edad
        val edadNum = _edad.value.toIntOrNull()
        if (edadNum != null) {
            val edadTerm = when {
                edadNum < 2 -> "cachorro"
                edadNum <= 7 -> "adulto"
                else -> "senior"
            }
            if (textoProducto.contains(edadTerm)) score += 5
            else if (textoProducto.contains("todas las edades")) score += 2
        }

        // Criterio 3: Raza
        if (_raza.value.isNotBlank()) {
            if (textoProducto.contains(_raza.value.lowercase())) score += 5
            else if (textoProducto.contains("todas las razas")) score += 2
        }

        // Criterio 4: Peso
        val pesoNum = _peso.value.toDoubleOrNull()
        if (pesoNum != null) {
            val pesoTerm = when {
                pesoNum <= 10 -> "raza pequeña"
                pesoNum <= 25 -> "raza mediana"
                else -> "raza grande"
            }
            if (textoProducto.contains(pesoTerm)) score += 5
            else if (textoProducto.contains("todos los pesos")) score += 2
        }
        
        return score
    }
}
