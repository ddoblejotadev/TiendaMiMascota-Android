package com.example.mimascota.repository

import com.example.mimascota.client.HuachitosRetrofitClient
import com.example.mimascota.model.Animal

class HuachitosRepository {
    private val apiService = HuachitosRetrofitClient.apiService

    suspend fun getAnimales(
        comunaId: Int,
        // Los parámetros tipo y estado no se usan actualmente, pero se mantienen por si se añaden en el futuro
        tipo: String? = null,
        estado: String? = null
    ): Result<List<Animal>> {
        return try {
            // Asegúrate de que los endpoints comentados existan en tu ApiService si los necesitas.
            val response = apiService.getAnimales(comunaId)

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
