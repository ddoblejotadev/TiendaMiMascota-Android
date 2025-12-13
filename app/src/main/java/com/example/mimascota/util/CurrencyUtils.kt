package com.example.mimascota.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    private const val IVA_RATE = 0.19 // 19%

    fun formatAsCLP(amount: Double?): String {
        val finalAmount = amount ?: 0.0
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        return format.format(finalAmount)
    }

    /**
     * Extrae la porción de IVA de un precio que ya lo tiene incluido.
     * La fórmula es: Total / (1 + Tasa de IVA) * Tasa de IVA
     */
    fun getIVAFromTotalPrice(priceWithIVA: Double?): Double {
        val finalAmount = priceWithIVA ?: 0.0
        if (finalAmount <= 0) return 0.0
        // Primero calculamos la base imponible y luego la diferencia
        val base = finalAmount / (1 + IVA_RATE)
        return finalAmount - base
    }

    /**
     * Calcula la base imponible (subtotal) a partir de un precio que incluye IVA.
     */
    fun getBasePrice(priceWithIVA: Double?): Double {
        val finalAmount = priceWithIVA ?: 0.0
        if (finalAmount <= 0) return 0.0
        return finalAmount / (1 + IVA_RATE)
    }
}
