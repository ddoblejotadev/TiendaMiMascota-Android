package com.example.mimascota.repository

import com.example.mimascota.client.HuachitosRetrofitClient
import com.example.mimascota.model.Animal

class HuachitosRepository {
    private val apiService = HuachitosRetrofitClient.apiService

    suspend fun getAnimales(
        comunaId: Int,
        tipo: String? = null,
        estado: String? = null
    ): Result<List<Animal>> {
        return try {
            val response = when {
                tipo != null && estado != null -> apiService.getAnimalesPorComunaTipoYEstado(comunaId, tipo, estado)
                tipo != null -> apiService.getAnimalesPorComunaYTipo(comunaId, tipo)
                estado != null -> apiService.getAnimalesPorComunaYEstado(comunaId, estado)
                else -> apiService.getAnimalesPorComuna(comunaId)
            }

            if (response.isSuccessful && response.body() != null) {
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
            val response = apiService.getAnimalById(animalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
