package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * Contenedor para la respuesta de la API que incluye la lista de animales.
 */
data class AnimalesResponse(
    @SerializedName("data")
    val data: List<Animal>
)

/**
 * Contenedor para la respuesta de la API que incluye el detalle de un solo animal.
 */
data class AnimalDetailResponse(
    @SerializedName("data")
    val data: Animal
)

/**
 * Modelo para una sola imagen de la galería de un animal.
 */
data class ImagenAnimal(
    val imagen: String
)

/**
 * Modelo de datos para un Animal. Todos los campos son opcionales para evitar crashes.
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
    
    // Imagen principal que viene en la lista
    val imagen: String?,
    
    // Galería de imágenes que viene en el detalle
    val imagenes: List<ImagenAnimal>?,
    
    val equipo: String?,
    val region: String?,
    val comuna: String?,
    val url: String?
)
