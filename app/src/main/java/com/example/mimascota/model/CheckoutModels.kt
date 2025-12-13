package com.example.mimascota.model

import com.google.gson.annotations.SerializedName

// ... (otros modelos)

data class ProductoRequest(
    val nombre: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val imageUrl: String?,
    val destacado: Boolean,
    val valoracion: Double,
    val precioAnterior: Int
)

data class CrearOrdenRequest(
    @SerializedName("usuario_id")
    val usuarioId: Long,
    @SerializedName("es_invitado")
    val esInvitado: Boolean = false,
    val items: List<ItemOrden>,
    @SerializedName("datos_envio")
    val datosEnvio: DatosEnvio,
    val subtotal: Double,
    val total: Double,
    val estado: String = "completada"
)

data class ItemOrden(
    @SerializedName("producto_id")
    val productoId: Int,
    val cantidad: Int,
    @SerializedName("precio_unitario")
    val precioUnitario: Double
)

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

data class OrdenResponse(
    val id: Long,
    @SerializedName("numeroOrden")
    val numeroOrden: String?,
    val fecha: String?,
    val estado: String?,
    val total: Double?,
    val mensaje: String?,
    val items: List<ProductoOrden>?
)

data class OrdenHistorial(
    val id: Long,
    @SerializedName("numeroOrden")
    val numeroOrden: String?,
    val fecha: String?,
    val estado: String?,
    val total: Double?,
    val subtotal: Double?,
    @SerializedName("esInvitado")
    val esInvitado: Boolean?,
    // Corregido: El backend usa camelCase para este campo en las órdenes
    @SerializedName("usuarioId") 
    val usuarioId: Long?,
    @SerializedName("datosEnvio")
    val datosEnvio: DatosEnvioResponse?,
    val productos: List<ProductoOrden>?
)

data class DatosEnvioResponse(
    val nombre: String?,
    val email: String?,
    val telefono: String?,
    val direccion: String?,
    val ciudad: String?,
    val region: String?,
    @SerializedName("codigoPostal")
    val codigoPostal: String?,
    @SerializedName("metodoPago")
    val metodoPago: String?
)

data class ProductoOrden(
    @SerializedName("productoId")
    val productoId: Int,
    val nombre: String?,
    val cantidad: Int?,
    @SerializedName("precioUnitario")
    val precioUnitario: Double?,
    val imagen: String?
)

// ... (otros modelos)

data class VerificarStockRequest(
    val items: List<StockItem>
)

data class StockItem(
    @SerializedName("productoId")
    val productoId: Int,

    val cantidad: Int
)

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

enum class EstadoOrden(val displayName: String, val value: String) {
    PENDIENTE("Pendiente", "pendiente"),
    PROCESANDO("Procesando", "procesando"),
    COMPLETADA("Completada", "completada"),
    ENVIADO("Enviado", "enviado"),
    ENTREGADO("Entregado", "entregado"),
    CANCELADO("Cancelado", "cancelado")
}