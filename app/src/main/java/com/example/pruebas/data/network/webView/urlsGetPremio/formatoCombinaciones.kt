package com.example.pruebas.data.network.webView.urlsGetPremio

fun transformListToParams(combinaciones: List<String>): Map<String, String> {
    return combinaciones.mapIndexed { index, combinacion ->
        val key = "bloque${index + 1}"
        val value = formatNumbersString(combinacion)
        key to value
    }.filter { it.second.isNotBlank() }.toMap()
}

fun formatNumbersString(rawNumbers: String): String {
    return rawNumbers
        .split(",", " ", "y")
        .filter { it.isNotBlank() }
        .mapNotNull { it.trim().toIntOrNull() }
        .joinToString("y")
}