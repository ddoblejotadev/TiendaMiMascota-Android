package com.example.mimascota.repository

import android.util.Log
import com.example.mimascota.client.RetrofitClient
import com.example.mimascota.model.Producto
import com.example.mimascota.model.ProductoRequest
import java.io.IOException

class ProductoRepository {

    private val apiService = RetrofitClient.apiService
    private val TAG = "ProductoRepository"

    // Resultado sellado para manejar éxito, error y carga
    sealed class ProductoResult<out T> {
        data class Success<T>(val data: T) : ProductoResult<T>()
        data class Error(val message: String) : ProductoResult<Nothing>()
        object Loading : ProductoResult<Nothing>()
    }

    /**
     * Obtiene todos los productos desde el ApiService unificado
     */
    suspend fun getAllProductos(): ProductoResult<List<Producto>> {
        return try {
            val response = apiService.getAllProductos(limite = 50)
            if (response.isSuccessful && response.body() != null) {
                val productos = response.body()!!
                Log.d(TAG, "Productos recibidos: ${productos.size}")
                productos.take(5).forEachIndexed { index, producto ->
                    Log.d(TAG, "#${index + 1} id=${producto.producto_id} name=${producto.producto_nombre} imageUrl=${producto.imageUrl}")
                }
                ProductoResult.Success(productos)
            } else {
                Log.e(TAG, "Error en respuesta: ${response.code()} - ${response.message()}")
                ProductoResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "IOException al obtener productos: ${e.message}", e)
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener productos: ${e.message}", e)
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Obtiene un producto por su ID
     */
    suspend fun getProductoById(id: Int): ProductoResult<Producto> {
        return try {
            val response = apiService.getProductoById(id)
            if (response.isSuccessful && response.body() != null) {
                ProductoResult.Success(response.body()!!)
            } else {
                ProductoResult.Error("Error ${response.code()}: Producto no encontrado")
            }
        } catch (e: IOException) {
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Crea un nuevo producto
     */
    suspend fun createProducto(producto: Producto): ProductoResult<Producto> {
        return try {
             val productoRequest = ProductoRequest(
                nombre = producto.producto_nombre,
                description = producto.description ?: "",
                price = producto.price ?: 0.0,
                stock = producto.stock ?: 0,
                category = producto.category ?: "General",
                imageUrl = producto.imageUrl ?: "",
                destacado = producto.destacado ?: false,
                valoracion = producto.valoracion ?: 0.0,
                precioAnterior = producto.precioAnterior?.toInt() ?: 0
            )
            Log.d(TAG, "Creando producto con datos: $productoRequest")
            val response = apiService.createProducto(productoRequest)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Producto creado con éxito: ${response.body()}")
                ProductoResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Error al crear producto: ${response.code()} - ${response.message()} - $errorBody")
                ProductoResult.Error("Error ${response.code()}: ${response.message()} $errorBody")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error de red al crear producto: ${e.message}")
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al crear producto: ${e.message}")
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Actualiza un producto existente
     */
    suspend fun updateProducto(id: Int, producto: Producto): ProductoResult<Producto> {
        return try {
            val productoRequest = ProductoRequest(
                nombre = producto.producto_nombre,
                description = producto.description ?: "",
                price = producto.price ?: 0.0,
                stock = producto.stock ?: 0,
                category = producto.category ?: "General",
                imageUrl = producto.imageUrl ?: "",
                destacado = producto.destacado ?: false,
                valoracion = producto.valoracion ?: 0.0,
                precioAnterior = producto.precioAnterior?.toInt() ?: 0
            )
            Log.d(TAG, "Actualizando producto ID: $id con datos: $productoRequest")
            val response = apiService.updateProducto(id, productoRequest)
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Producto actualizado con éxito: ${response.body()}")
                ProductoResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Error al actualizar producto: ${response.code()} - ${response.message()} - $errorBody")
                ProductoResult.Error("Error ${response.code()}: ${response.message()} $errorBody")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error de red al actualizar producto: ${e.message}")
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al actualizar producto: ${e.message}")
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Elimina un producto por su ID
     */
    suspend fun deleteProducto(id: Int): ProductoResult<Unit> {
        return try {
            val response = apiService.deleteProducto(id)
            if (response.isSuccessful) {
                ProductoResult.Success(Unit)
            } else {
                ProductoResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Obtiene productos por categoría
     */
    suspend fun getProductosByCategoria(categoria: String): ProductoResult<List<Producto>> {
        return try {
            val response = apiService.getProductosByCategoria(categoria)
            if (response.isSuccessful && response.body() != null) {
                ProductoResult.Success(response.body()!!)
            } else {
                ProductoResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }

    /**
     * Busca productos por un término de búsqueda
     */
    suspend fun searchProductos(query: String): ProductoResult<List<Producto>> {
        return try {
            val response = apiService.searchProductos(query)
            if (response.isSuccessful && response.body() != null) {
                ProductoResult.Success(response.body()!!)
            } else {
                ProductoResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            ProductoResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ProductoResult.Error("Excepción: ${e.message}")
        }
    }
}
