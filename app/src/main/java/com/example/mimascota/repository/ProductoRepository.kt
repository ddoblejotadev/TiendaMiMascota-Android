package com.example.mimascota.repository

import com.example.mimascota.Model.Producto
import com.example.mimascota.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ProductoRepository: Capa de abstracción entre el ViewModel y la fuente de datos
 *
 * Implementa el patrón Repository para:
 * - Abstraer las llamadas a la API
 * - Manejar errores de red
 * - Ejecutar operaciones en el dispatcher IO
 * - Proporcionar una interfaz limpia al ViewModel
 */
class ProductoRepository {

    private val productoService = RetrofitClient.productoService

    /**
     * Resultado de operaciones con productos
     */
    sealed class ProductoResult<out T> {
        data class Success<T>(val data: T) : ProductoResult<T>()
        data class Error(val message: String, val code: Int? = null) : ProductoResult<Nothing>()
        object Loading : ProductoResult<Nothing>()
    }

    /**
     * Obtiene todos los productos
     */
    suspend fun getAllProductos(): ProductoResult<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.getAllProductos()
                if (response.isSuccessful && response.body() != null) {
                    ProductoResult.Success(response.body()!!)
                } else {
                    ProductoResult.Error(
                        message = "Error al obtener productos: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Obtiene un producto por ID
     */
    suspend fun getProductoById(id: Int): ProductoResult<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.getProductoById(id)
                if (response.isSuccessful && response.body() != null) {
                    ProductoResult.Success(response.body()!!)
                } else {
                    ProductoResult.Error(
                        message = "Producto no encontrado",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Obtiene productos por categoría
     */
    suspend fun getProductosByCategoria(categoria: String): ProductoResult<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.getProductosByCategoria(categoria)
                if (response.isSuccessful && response.body() != null) {
                    ProductoResult.Success(response.body()!!)
                } else {
                    ProductoResult.Error(
                        message = "Error al obtener productos de la categoría $categoria",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Crea un nuevo producto
     */
    suspend fun createProducto(producto: Producto): ProductoResult<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.createProducto(producto)
                if (response.isSuccessful && response.body() != null) {
                    ProductoResult.Success(response.body()!!)
                } else {
                    ProductoResult.Error(
                        message = "Error al crear producto: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Actualiza un producto existente
     */
    suspend fun updateProducto(id: Int, producto: Producto): ProductoResult<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.updateProducto(id, producto)
                if (response.isSuccessful && response.body() != null) {
                    ProductoResult.Success(response.body()!!)
                } else {
                    ProductoResult.Error(
                        message = "Error al actualizar producto: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Elimina un producto
     */
    suspend fun deleteProducto(id: Int): ProductoResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = productoService.deleteProducto(id)
                if (response.isSuccessful) {
                    ProductoResult.Success(true)
                } else {
                    ProductoResult.Error(
                        message = "Error al eliminar producto: ${response.message()}",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                ProductoResult.Error(
                    message = "Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Busca productos por nombre (implementación local - filtrado)
     * Nota: Si el backend implementa búsqueda, se debería crear un endpoint específico
     */
    suspend fun searchProductos(query: String): ProductoResult<List<Producto>> {
        return when (val result = getAllProductos()) {
            is ProductoResult.Success -> {
                val filtered = result.data.filter { producto ->
                    producto.name.contains(query, ignoreCase = true) ||
                    producto.description?.contains(query, ignoreCase = true) == true
                }
                ProductoResult.Success(filtered)
            }
            is ProductoResult.Error -> result
            else -> ProductoResult.Error("Error en la búsqueda")
        }
    }
}

