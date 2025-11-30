package com.example.mimascota.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CartItemEntity: Representa la tabla "carrito_items" en la base de datos
 * Guarda los productos agregados al carrito de forma persistente
 */
@Entity(tableName = "carrito_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val nombre: String,
    // Cambiado a Double para soportar decimales
    val precio: Double,
    val cantidad: Int,
    val imagen: String = "",
    val fechaAñadido: Long = System.currentTimeMillis()
)
