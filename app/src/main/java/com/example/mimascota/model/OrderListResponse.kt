package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * El backend devuelve un objeto de paginación. La lista de órdenes está en el campo "content".
 */
data class OrderListResponse(
    @SerializedName("content")
    val content: List<OrdenHistorial>
)
