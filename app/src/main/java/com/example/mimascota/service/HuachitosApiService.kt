package com.example.mimascota.service

import com.example.mimascota.model.AnimalDetailResponse
import com.example.mimascota.model.HuachitosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HuachitosApiService {

    @GET("animales")
    suspend fun getAnimales(
        @Query("comuna_id") comunaId: Int,
        @Query("tipo") tipo: String? = null,
        @Query("estado") estado: String? = null,
        @Query("genero") genero: String? = null
    ): Response<HuachitosResponse>

    @GET("animal/{id}")
    suspend fun getAnimalById(
        @Path("id") animalId: Int
    ): Response<AnimalDetailResponse>
}
