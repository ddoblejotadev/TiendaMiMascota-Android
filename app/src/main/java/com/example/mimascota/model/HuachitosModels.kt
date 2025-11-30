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
    val nombre: String,
    val tipo: String, // "Perro", "Gato", etc.
    val edad: String,
    val estado: String, // "adopcion", "encontrado", etc.
    val genero: String, // "macho", "hembra"
    
    @SerializedName("desc_fisica")
    val descFisica: String,
    
    @SerializedName("desc_personalidad")
    val descPersonalidad: String,
    
    @SerializedName("desc_adicional")
    val descAdicional: String,
    
    val esterilizado: Int, // 1 para sí, 0 para no
    val vacunas: Int, // 1 para sí, 0 para no
    val imagen: String, // URL de la imagen
    val equipo: String,
    val region: String,
    val comuna: String,
    val url: String
)
