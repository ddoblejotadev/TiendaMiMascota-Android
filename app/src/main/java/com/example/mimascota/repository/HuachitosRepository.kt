package com.example.mimascota.repository

import com.example.mimascota.client.HuachitosRetrofitClient
import com.example.mimascota.model.Animal

class HuachitosRepository {
    private val apiService = HuachitosRetrofitClient.apiService

    suspend fun getAnimales(
        comunaId: Int,
        tipo: String?,
        estado: String?
    ): Result<List<Animal>> {
        return try {
            // Lógica 'when' restaurada para llamar al endpoint correcto según los filtros.
            val response = when {
                !tipo.isNullOrBlank() && !estado.isNullOrBlank() -> apiService.getAnimalesPorComunaTipoYEstado(comunaId, tipo, estado)
                !tipo.isNullOrBlank() -> apiService.getAnimalesPorComunaYTipo(comunaId, tipo)
                !estado.isNullOrBlank() -> apiService.getAnimalesPorComunaYEstado(comunaId, estado)
                else -> apiService.getAnimalesPorComuna(comunaId)
            }

            if (response.isSuccessful && response.body() != null) {
                // La respuesta de la lista de animales está en el campo "data"
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimalById(animalId: Int): Result<Animal> {
        return try {
            val response = apiService.getAnimal(animalId)
            if (response.isSuccessful && response.body() != null) {
                // La respuesta del detalle del animal también está en un campo "data"
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
