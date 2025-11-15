package com.example.mimascota.service

import com.example.mimascota.Model.CartItem
import com.example.mimascota.Model.Producto
import retrofit2.Response
import retrofit2.http.*

/**
 * CartSyncService: Servicio para sincronizar el carrito con React Context
 *
 * Endpoints para mantener el carrito sincronizado entre Android y React
 */
interface CartSyncService {

    /**
     * Obtiene el carrito del usuario desde el backend (React Context)
     * GET /carrito/{userId}
     */
    @GET("carrito/{userId}")
    suspend fun obtenerCarrito(@Path("userId") userId: Int): Response<CarritoResponse>

    /**
     * Sincroniza el carrito local con el backend (React Context)
     * POST /carrito/sync
     */
    @POST("carrito/sync")
    suspend fun sincronizarCarrito(@Body request: SyncCarritoRequest): Response<SyncCarritoResponse>

    /**
     * Agrega un item al carrito en el backend
     * POST /carrito/agregar
     */
    @POST("carrito/agregar")
    suspend fun agregarItem(@Body request: AgregarItemRequest): Response<CarritoResponse>

    /**
     * Elimina un item del carrito en el backend
     * DELETE /carrito/{userId}/producto/{productoId}
     */
    @DELETE("carrito/{userId}/producto/{productoId}")
    suspend fun eliminarItem(
        @Path("userId") userId: Int,
        @Path("productoId") productoId: Int
    ): Response<CarritoResponse>

    /**
     * Actualiza la cantidad de un item en el carrito
     * PUT /carrito/actualizar
     */
    @PUT("carrito/actualizar")
    suspend fun actualizarCantidad(@Body request: ActualizarCantidadRequest): Response<CarritoResponse>

    /**
     * Vacía el carrito del usuario
     * DELETE /carrito/{userId}
     */
    @DELETE("carrito/{userId}")
    suspend fun vaciarCarrito(@Path("userId") userId: Int): Response<Unit>
}

// ========== MODELOS DE REQUEST/RESPONSE ==========

/**
 * Respuesta del carrito (compatible con React Context)
 */
data class CarritoResponse(
    val userId: Int,
    val items: List<CarritoItemDto>,
    val total: Int,
    val cantidadItems: Int
)

/**
 * Item del carrito DTO (compatible con React)
 */
data class CarritoItemDto(
    val producto_id: Int,
    val producto_nombre: String,
    val price: Int,
    val cantidad: Int,
    val subtotal: Int,
    val imageUrl: String?,
    val category: String?,
    val stock: Int?
)

/**
 * Request para sincronizar carrito completo
 */
data class SyncCarritoRequest(
    val userId: Int,
    val items: List<CarritoItemDto>
)

/**
 * Response de sincronización
 */
data class SyncCarritoResponse(
    val success: Boolean,
    val message: String,
    val carrito: CarritoResponse?
)

/**
 * Request para agregar item
 */
data class AgregarItemRequest(
    val userId: Int,
    val productoId: Int,
    val cantidad: Int
)

/**
 * Request para actualizar cantidad
 */
data class ActualizarCantidadRequest(
    val userId: Int,
    val productoId: Int,
    val cantidad: Int
)

// ========== FUNCIONES DE CONVERSIÓN ==========

/**
 * Convierte CartItem a CarritoItemDto
 */
fun CartItem.toDto(): CarritoItemDto {
    return CarritoItemDto(
        producto_id = producto.id,
        producto_nombre = producto.name,
        price = producto.price,
        cantidad = cantidad,
        subtotal = subtotal,
        imageUrl = producto.imageUrl,
        category = producto.category,
        stock = producto.stock
    )
}

/**
 * Convierte CarritoItemDto a CartItem
 */
fun CarritoItemDto.toCartItem(): CartItem {
    return CartItem(
        producto = Producto(
            id = producto_id,
            name = producto_nombre,
            price = price,
            stock = stock ?: 0,
            category = category ?: "",
            imageUrl = imageUrl,
            description = null
        ),
        cantidad = cantidad
    )
}

/**
 * Convierte lista de CartItem a lista de DTOs
 */
fun List<CartItem>.toDtoList(): List<CarritoItemDto> {
    return map { it.toDto() }
}

/**
 * Convierte lista de DTOs a lista de CartItem
 */
fun List<CarritoItemDto>.toCartItemList(): List<CartItem> {
    return map { it.toCartItem() }
}

