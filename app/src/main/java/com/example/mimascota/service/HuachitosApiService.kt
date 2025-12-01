package com.example.mimascota.service

import com.example.mimascota.model.AnimalDetailResponse
import com.example.mimascota.model.AnimalesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HuachitosApiService {

    @GET("animales/comuna/{comunaId}")
    suspend fun getAnimalesPorComuna(
        @Path("comunaId") comunaId: Int
    ): Response<AnimalesResponse>

    @GET("animales/comuna/{comunaId}/estado/{estado}")
    suspend fun getAnimalesPorComunaYEstado(
        @Path("comunaId") comunaId: Int,
        @Path("estado") estado: String
    ): Response<AnimalesResponse>

    @GET("animales/comuna/{comunaId}/tipo/{tipo}")
    suspend fun getAnimalesPorComunaYTipo(
        @Path("comunaId") comunaId: Int,
        @Path("tipo") tipo: String
    ): Response<AnimalesResponse>

    @GET("animales/comuna/{comunaId}/tipo/{tipo}/estado/{estado}")
    suspend fun getAnimalesPorComunaTipoYEstado(
        @Path("comunaId") comunaId: Int,
        @Path("tipo") tipo: String,
        @Path("estado") estado: String
    ): Response<AnimalesResponse>

    @GET("animal/{animalId}")
    suspend fun getAnimal(
        @Path("animalId") animalId: Int
    ): Response<AnimalDetailResponse>
}
