package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

/**
 * Modelos para el flujo de checkout y órdenes
 * IMPORTANTE: Usar @SerializedName para mapear snake_case del backend
 */

// ============= Request Models =============

/**
 * Request para crear una orden de compra
 */
data class CrearOrdenRequest(
    @SerializedName("usuario_id")
    val usuarioId: Long,

    @SerializedName("es_invitado")
    val esInvitado: Boolean = false,

    val items: List<ItemOrden>,

    @SerializedName("datos_envio")
    val datosEnvio: DatosEnvio,

    // Cambiado a Double para cumplir con el backend que maneja precios con punto
    val subtotal: Double,
    val total: Double,
    val estado: String = "completada"
)

/**
 * Item dentro de una orden
 */
data class ItemOrden(
    @SerializedName("producto_id")
    val productoId: Int,

    val cantidad: Int,

    @SerializedName("precio_unitario")
    val precioUnitario: Double
)

/**
 * Datos de envío de la orden
 */
data class DatosEnvio(
    @SerializedName("nombre_completo")
    val nombreCompleto: String,

    val email: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val region: String,

    @SerializedName("codigo_postal")
    val codigoPostal: String,

    @SerializedName("metodo_pago")
    val metodoPago: String = "tarjeta",

    val pais: String = "Chile"
)

// ============= Response Models =============

/**
 * Response al crear una orden
 */
data class OrdenResponse(
    val id: Long,

    @SerializedName("numeroOrden")
    val numeroOrden: String,

    val fecha: String,
    val estado: String,
    // Cambiado a Double
    val total: Double,
    val mensaje: String?,
    val items: List<ProductoOrden>?
)

/**
 * Orden del historial
 */
data class OrdenHistorial(
    val id: Long,

    @SerializedName("numeroOrden")
    val numeroOrden: String,

    val fecha: String,
    val estado: String,
    val total: Double,
    val subtotal: Double,

    @SerializedName("esInvitado")
    val esInvitado: Boolean,

    @SerializedName("usuarioId")
    val usuarioId: Long,

    @SerializedName("datosEnvio")
    val datosEnvio: DatosEnvioResponse,

    val productos: List<ProductoOrden>
)

/**
 * Datos de envío en la respuesta
 */
data class DatosEnvioResponse(
    val nombre: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val region: String,

    @SerializedName("codigoPostal")
    val codigoPostal: String?,

    @SerializedName("metodoPago")
    val metodoPago: String?
)

/**
 * Producto dentro de una orden
 */
data class ProductoOrden(
    @SerializedName("productoId")
    val productoId: Int,

    val nombre: String,
    val cantidad: Int,

    @SerializedName("precioUnitario")
    val precioUnitario: Double,

    val imagen: String?
)

/**
 * Request para verificar stock antes del checkout
 */
data class VerificarStockRequest(
    val items: List<StockItem>
)

data class StockItem(
    @SerializedName("productoId")
    val productoId: Int,

    val cantidad: Int
)

/**
 * Response de verificación de stock
 */
data class VerificarStockResponse(
    val disponible: Boolean,

    @SerializedName("productosAgotados")
    val productosAgotados: List<ProductoAgotado>?
)

data class ProductoAgotado(
    @SerializedName("productoId")
    val productoId: Int,

    @SerializedName("productoNombre")
    val productoNombre: String,

    @SerializedName("stockDisponible")
    val stockDisponible: Int,

    @SerializedName("cantidadSolicitada")
    val cantidadSolicitada: Int
)

/**
 * Estados de orden
 */
enum class EstadoOrden(val displayName: String, val value: String) {
    PENDIENTE("Pendiente", "pendiente"),
    PROCESANDO("Procesando", "procesando"),
    COMPLETADA("Completada", "completada"),
    ENVIADO("Enviado", "enviado"),
    ENTREGADO("Entregado", "entregado"),
    CANCELADO("Cancelado", "cancelado")
}
