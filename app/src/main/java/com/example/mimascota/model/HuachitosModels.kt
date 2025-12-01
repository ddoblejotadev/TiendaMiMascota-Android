package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo para la respuesta de la lista de animales, que viene paginada.
 */
data class AnimalesResponse(
    @SerializedName("data")
    val data: List<Animal>
)

/**
 * Modelo para una sola imagen de la galería de un animal.
 */
data class ImagenAnimal(
    val imagen: String
)

/**
 * Modelo para un solo animal, usando @SerializedName para mapear los nombres del JSON.
 * Todos los campos son opcionales para evitar crashes si la API no los envía.
 */
data class Animal(
    val id: Int,
    val nombre: String?,
    val tipo: String?,
    val edad: String?,
    val estado: String?,
    val genero: String?,
    
    @SerializedName("desc_fisica")
    val descFisica: String?,
    
    @SerializedName("desc_personalidad")
    val descPersonalidad: String?,
    
    @SerializedName("desc_adicional")
    val descAdicional: String?,
    
    val esterilizado: Int?, // 1 para sí, 0 para no
    val vacunas: Int?, // 1 para sí, 0 para no
    
    // Campo de imagen principal (de la lista)
    val imagen: String?,
    
    // Campo de imágenes (del detalle)
    val imagenes: List<ImagenAnimal>?,
    
    val equipo: String?,
    val region: String?,
    val comuna: String?,
    val url: String?
)
