package com.example.mimascota.util

import java.text.NumberFormat
import java.util.Locale

// Función para formatear montos en CLP
fun formatCurrencyCLP(amount: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    return nf.format(amount)
}

// Función para calcular el IVA (19%)
fun calculateIVA(price: Double): Double {
    return price * 0.19
}

// Función para agregar el IVA a un precio
fun addIVA(price: Double): Double {
    return price + calculateIVA(price)
}
