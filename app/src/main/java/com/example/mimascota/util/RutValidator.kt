package com.example.mimascota.util

/**
 * RutValidator: Validador de RUT chileno (Rol Único Tributario)
 *
 * Funcionalidades:
 * - Validación con algoritmo módulo 11
 * - Formateo automático a formato XX.XXX.XXX-X
 * - Limpieza de caracteres no numéricos
 *
 * Uso:
 * ```
 * val esValido = RutValidator.esValido("12345678-9")
 * val rutFormateado = RutValidator.formatear("123456789")
 * ```
 */
object RutValidator {

    /**
     * Valida un RUT chileno usando el algoritmo módulo 11
     *
     * @param rut RUT a validar (puede tener formato XX.XXX.XXX-X o sin formato)
     * @return true si el RUT es válido, false en caso contrario
     *
     * Ejemplos:
     * - esValido("12.345.678-9") → true/false según dígito verificador
     * - esValido("12345678-9") → true/false según dígito verificador
     * - esValido("123456789") → true/false según dígito verificador
     * - esValido("") → false
     * - esValido(null) → false
     */
    fun esValido(rut: String?): Boolean {
        if (rut.isNullOrBlank()) return false

        // Limpiar RUT (quitar puntos, guiones y espacios)
        val rutLimpio = limpiar(rut)

        // Validar longitud mínima (mínimo 7 dígitos + 1 verificador)
        if (rutLimpio.length < 2) return false

        try {
            // Separar número y dígito verificador
            val numero = rutLimpio.dropLast(1)
            val digitoVerificador = rutLimpio.last().uppercaseChar()

            // Validar que el número sea numérico
            if (!numero.all { it.isDigit() }) return false

            // Calcular dígito verificador esperado
            val digitoCalculado = calcularDigitoVerificador(numero)

            // Comparar
            return digitoVerificador == digitoCalculado

        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Formatea un RUT al formato estándar chileno XX.XXX.XXX-X
     *
     * @param rut RUT a formatear (puede venir sin formato)
     * @return RUT formateado o string vacío si es inválido
     *
     * Ejemplos:
     * - formatear("123456789") → "12.345.678-9"
     * - formatear("12345678-9") → "12.345.678-9"
     * - formatear("12.345.678-9") → "12.345.678-9"
     * - formatear("") → ""
     * - formatear(null) → ""
     */
    fun formatear(rut: String?): String {
        if (rut.isNullOrBlank()) return ""

        // Limpiar RUT
        val rutLimpio = limpiar(rut)

        if (rutLimpio.length < 2) return rutLimpio

        try {
            // Separar número y dígito verificador
            val numero = rutLimpio.dropLast(1)
            val dv = rutLimpio.last().uppercaseChar()

            // Formatear número con puntos (de derecha a izquierda cada 3 dígitos)
            val numeroFormateado = numero.reversed()
                .chunked(3)
                .joinToString(".")
                .reversed()

            return "$numeroFormateado-$dv"

        } catch (_: Exception) {
            return rutLimpio
        }
    }

    /**
     * Limpia un RUT quitando puntos, guiones y espacios
     *
     * @param rut RUT a limpiar
     * @return RUT sin caracteres especiales
     */
    private fun limpiar(rut: String): String {
        return rut.replace(".", "")
            .replace("-", "")
            .replace(" ", "")
            .trim()
    }

    /**
     * Calcula el dígito verificador usando el algoritmo módulo 11
     *
     * @param numero Número del RUT sin dígito verificador
     * @return Dígito verificador calculado ('0'-'9' o 'K')
     */
    private fun calcularDigitoVerificador(numero: String): Char {
        var suma = 0
        var multiplicador = 2

        // Recorrer de derecha a izquierda
        for (i in numero.length - 1 downTo 0) {
            val digito = numero[i].digitToInt()
            suma += digito * multiplicador

            // El multiplicador va de 2 a 7 y se repite
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        // Calcular módulo 11
        val resto = suma % 11
        val dv = 11 - resto

        return when (dv) {
            11 -> '0'
            10 -> 'K'
            else -> dv.toString()[0]
        }
    }
}
