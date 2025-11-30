package com.example.mimascota.service

import com.example.mimascota.model.Animal
import com.example.mimascota.model.HuachitosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para la API externa de Huachitos.cl
 */
interface HuachitosApiService {

    /**
     * Obtiene una lista de animales para una comuna específica.
     */
    @GET("animales/comuna/{id}")
    suspend fun getAnimalesPorComuna(
        @Path("id") comunaId: Int
    ): Response<HuachitosResponse>

    /**
     * Obtiene animales filtrando por comuna y tipo.
     */
    @GET("animales/comuna/{comuna_id}/tipo/{tipo}")
    suspend fun getAnimalesPorComunaYTipo(
        @Path("comuna_id") comunaId: Int,
        @Path("tipo") tipo: String
    ): Response<HuachitosResponse>

    /**
     * Obtiene animales filtrando por comuna y estado.
     */
    @GET("animales/comuna/{comuna_id}/estado/{estado}")
    suspend fun getAnimalesPorComunaYEstado(
        @Path("comuna_id") comunaId: Int,
        @Path("estado") estado: String
    ): Response<HuachitosResponse>

    /**
     * Obtiene animales filtrando por comuna, tipo y estado.
     */
    @GET("animales/comuna/{comuna_id}/tipo/{tipo}/estado/{estado}")
    suspend fun getAnimalesPorComunaTipoYEstado(
        @Path("comuna_id") comunaId: Int,
        @Path("tipo") tipo: String,
        @Path("estado") estado: String
    ): Response<HuachitosResponse>

    /**
     * Obtiene el detalle de un animal específico por su ID.
     */
    @GET("animal/{id}")
    suspend fun getAnimalById(
        @Path("id") animalId: Int
    ): Response<Animal> // Se espera un objeto Animal directamente
}
