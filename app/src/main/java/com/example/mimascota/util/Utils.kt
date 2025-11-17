package com.example.mimascota.util

import java.text.NumberFormat
import java.util.Locale

// Función sencilla para formatear montos en CLP de forma didáctica
fun formatCurrencyCLP(amount: Int): String {
    val nf = NumberFormat.getInstance(Locale.forLanguageTag("es-CL"))
    return nf.format(amount)
}
