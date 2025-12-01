package com.example.mimascota.service

import com.example.mimascota.model.Animal
import com.example.mimascota.model.AnimalesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Modelo para la respuesta anidada del detalle del animal
data class AnimalDetailResponse(val data: Animal)

interface HuachitosApiService {
    @GET("animales/comuna/{comunaId}")
    suspend fun getAnimales(
        @Path("comunaId") comunaId: Int
    ): Response<AnimalesResponse>

    @GET("animal/{animalId}")
    suspend fun getAnimal(
        @Path("animalId") animalId: Int
    ): Response<AnimalDetailResponse>
}
