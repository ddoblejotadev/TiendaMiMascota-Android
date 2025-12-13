package com.example.mimascota.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Objeto de utilidad para funciones relacionadas con la moneda.
 */
object CurrencyUtils {

    private const val IVA_RATE = 0.19 // 19%

    /**
     * Formatea un número Double como moneda CLP (Peso Chileno).
     *
     * @param amount El monto a formatear.
     * @return Un String con el formato de moneda, por ejemplo, "$15.990".
     */
    fun formatAsCLP(amount: Double?): String {
        val finalAmount = amount ?: 0.0
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        // Los precios en CLP generalmente no usan decimales, así que los removemos.
        format.maximumFractionDigits = 0
        return format.format(finalAmount)
    }

    /**
     * Calcula la porción de IVA de un precio que ya lo tiene incluido.
     * La fórmula es: Total / (1 + Tasa de IVA) * Tasa de IVA
     *
     * @param priceWithIVA El precio total con el IVA incluido.
     * @return El monto que corresponde solo al IVA.
     */
    fun getIVAFromTotalPrice(priceWithIVA: Double?): Double {
        val finalAmount = priceWithIVA ?: 0.0
        if (finalAmount <= 0) return 0.0
        return finalAmount - (finalAmount / (1 + IVA_RATE))
    }
}
