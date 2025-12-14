package com.example.mimascota.repository

import com.example.mimascota.client.HuachitosRetrofitClient
import com.example.mimascota.model.Animal

class HuachitosRepository {
    private val apiService = HuachitosRetrofitClient.apiService

    suspend fun getAnimales(
        comunaId: Int,
        tipo: String? = null,
        estado: String? = null,
        genero: String? = null
    ): Result<List<Animal>> {
        return try {
            val response = apiService.getAnimales(comunaId, tipo, estado, genero)

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
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
