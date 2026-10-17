package com.example.pruebas.domain

sealed class LotteryGame(
    val pValue: String,
    val name: String,
    val type: String,
    val apiId: String,
    val colorHex: Long
) {
    abstract fun calculatePrice(betCount: Int, hasJoker: Boolean = false, sortesJugados: Int): Double

    object Primitiva : LotteryGame("1", "La Primitiva", "LAPR", "04", 0xFF43A047) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
           ((betCount * 1.0) + (if (hasJoker) 1.0 else 0.0)) * sortesJugados
    }

    object Bonoloto : LotteryGame("2", "Bonoloto", "BONO", "01", 0xFF98A065) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = (betCount * 0.5) * sortesJugados
    }

    object Gordo : LotteryGame("4", "El Gordo", "ELGR", "05", 0xFFC0392B) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = (betCount * 1.5) * sortesJugados
    }

    object Euromillones : LotteryGame("7", "Euromillones", "EMIL", "02", 0xFF283593) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = (betCount * 2.5) * sortesJugados
    }

    object Nacional : LotteryGame("5", "Loteria Nacional", "LNAC","09", 0xFF0277BD) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = 3.0
    }

    object Eurodreams : LotteryGame("14", "Eurodreams", "EDMS", "14", 0xFF8E24AA) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = (betCount * 2.5) * sortesJugados
    }

    object Unknown : LotteryGame("", "Desconocido", "unknown", "", 0xFF000000) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = 0.0
    }

    companion object {
        fun fromPValue(pValue: String?): LotteryGame = when (pValue) {
            "1" -> Primitiva
            "2" -> Bonoloto
            "4" -> Gordo
            "7" -> Euromillones
            "5" -> Nacional
            "14" -> Eurodreams
            else -> Unknown
        }

        fun fromType(type: String?): LotteryGame = when (type?.uppercase()?.trim()) {
            "LAPR", "PRIMITIVA", "LA PRIMITIVA" -> Primitiva
            "BONO", "BONOLOTO" -> Bonoloto
            "ELGR", "GORDO", "EL GORDO", "ELGORDO" -> Gordo
            "EMIL", "EUROMILLONES" -> Euromillones
            "LNAC", "NACIONAL", "LOTERIA NACIONAL", "LOTERIANACIONAL" -> Nacional
            "EDMS", "EURODREAMS" -> Eurodreams
            else -> Unknown
        }
    }
}
