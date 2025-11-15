package com.example.mimascota.Model

import com.google.gson.annotations.SerializedName

/**
 * Modelos para el flujo de checkout y órdenes
 */

// ============= Request Models =============

/**
 * Request para verificar stock antes del checkout
 */
data class VerificarStockRequest(
    val items: List<StockItem>
)

data class StockItem(
    val productoId: Int,
    val cantidad: Int
)

/**
 * Request para crear una orden de compra
 */
data class CrearOrdenRequest(
    val usuarioId: Int,
    val esInvitado: Boolean = false,
    val datosEnvio: DatosEnvio,
    val items: List<OrdenItem>,
    val total: Int
)

data class DatosEnvio(
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val region: String,
    val codigoPostal: String?,
    val notas: String?
)

data class OrdenItem(
    val productoId: Int,
    val productoNombre: String,
    val cantidad: Int,
    val precioUnitario: Int,
    val subtotal: Int
)

// ============= Response Models =============

/**
 * Response de verificación de stock
 */
data class VerificarStockResponse(
    val disponible: Boolean,
    val productosAgotados: List<ProductoAgotado>?
)

data class ProductoAgotado(
    val productoId: Int,
    val productoNombre: String,
    val stockDisponible: Int,
    val cantidadSolicitada: Int
)

/**
 * Response al crear una orden
 */
data class OrdenResponse(
    @SerializedName("orden_id")
    val ordenId: Int,
    val numeroOrden: String,
    val estado: String,
    val total: Int,
    val fechaCreacion: String,
    val mensaje: String
)

/**
 * Modelo completo de una orden (para historial)
 */
data class Orden(
    @SerializedName("orden_id")
    val ordenId: Int,
    val numeroOrden: String,
    val usuarioId: Int,
    val estado: String, // PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO
    val total: Int,
    val datosEnvio: DatosEnvio,
    val items: List<OrdenItem>,
    val fechaCreacion: String,
    val fechaActualizacion: String?
)

/**
 * Estados de orden
 */
enum class EstadoOrden(val displayName: String) {
    PENDIENTE("Pendiente"),
    PROCESANDO("Procesando"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}

