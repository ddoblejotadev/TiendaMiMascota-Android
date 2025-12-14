package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

// Modelo para la respuesta de la lista de animales
data class HuachitosResponse(
    val data: List<Animal>
)

// Modelo para la respuesta del detalle de un animal
data class HuachitosDetailResponse(
    val data: Animal
)

// Modelo para un solo animal, usando @SerializedName para mapear los nombres del JSON
data class Animal(
    val id: Int,
    val nombre: String?,
    val tipo: String?,
    val edad: String?,
    val genero: String?,
    val comuna: String?,
    val region: String?,
    @SerializedName("desc_fisica") val descFisica: String?,
    @SerializedName("desc_personalidad") val descPersonalidad: String?,
    @SerializedName("desc_adicional") val descAdicional: String?,
    val url: String?,

    // Campo para la respuesta de la lista (una sola imagen)
    @SerializedName("imagen") private val imagenUrl: String?,

    // Campo para la respuesta de detalles (lista de imágenes)
    val imagenes: List<Imagen>?,
) {
    /**
     * Propiedad computada para obtener la URL de la imagen principal,
     * independientemente de si viene como una cadena o una lista.
     */
    val imagen: String
        get() = imagenes?.firstOrNull()?.imagen ?: imagenUrl ?: ""
}
