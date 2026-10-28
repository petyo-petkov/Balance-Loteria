package com.example.pruebas.presentation

data class ScannerUiState(
    val isScanning: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
