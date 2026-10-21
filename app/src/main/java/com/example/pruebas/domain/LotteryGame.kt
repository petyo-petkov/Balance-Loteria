package com.example.pruebas.domain

private fun combinations(n: Int, k: Int): Int {
    if (k < 0 || n < 0 || k > n) return 0
    if (k == 0 || k == n) return 1
    val effectiveK = if (k > n - k) n - k else k
    var res = 1L
    for (i in 1..effectiveK) {
        res = res * (n - i + 1) / i
    }
    return res.toInt()
}

private fun parseBlockNumbers(blockString: String?): List<String> {
    if (blockString.isNullOrBlank()) return emptyList()
    return blockString.split(",", " ", "y")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

sealed class LotteryGame(
    val pValue: String,
    val name: String,
    val type: String,
    val apiId: String,
    val colorHex: Long
) {
    abstract fun calculatePrice(betCount: Int, hasJoker: Boolean = false, sortesJugados: Int): Double

    open fun calculateBetsCount(
        numbers: List<String>,
        stars: List<String>? = null,
        dreams: List<String>? = null,
        clave: List<String>? = null
    ): Int {
        if (numbers.isEmpty()) return 1
        return numbers.sumOf { block ->
            val count = parseBlockNumbers(block).size
            if (count > 0) 1 else 0
        }.coerceAtLeast(1)
    }

    open fun isMultiple(
        numbers: List<String>,
        stars: List<String>? = null,
        dreams: List<String>? = null,
        clave: List<String>? = null
    ): Boolean = false

    object Primitiva : LotteryGame("1", "La Primitiva", "LAPR", "04", 0xFF43A047) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
            ((betCount * 1.0) + (if (hasJoker) 1.0 else 0.0)) * sortesJugados

        override fun calculateBetsCount(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Int {
            if (numbers.isEmpty()) return 1
            var totalBets = 0
            for (block in numbers) {
                val count = parseBlockNumbers(block).size
                totalBets += when {
                    count == 5 -> 44
                    count >= 6 -> combinations(count, 6)
                    else -> 0
                }
            }
            return totalBets.coerceAtLeast(1)
        }

        override fun isMultiple(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Boolean {
            return numbers.any { block ->
                val count = parseBlockNumbers(block).size
                count == 5 || count > 6
            }
        }
    }

    object Bonoloto : LotteryGame("2", "Bonoloto", "BONO", "01", 0xFF98A065) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
            (betCount * 0.5) * sortesJugados

        override fun calculateBetsCount(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Int {
            if (numbers.isEmpty()) return 1
            var totalBets = 0
            for (block in numbers) {
                val count = parseBlockNumbers(block).size
                totalBets += when {
                    count == 5 -> 44
                    count >= 6 -> combinations(count, 6)
                    else -> 0
                }
            }
            return totalBets.coerceAtLeast(1)
        }

        override fun isMultiple(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Boolean {
            return numbers.any { block ->
                val count = parseBlockNumbers(block).size
                count == 5 || count > 6
            }
        }
    }

    object Gordo : LotteryGame("4", "El Gordo", "ELGR", "05", 0xFFC0392B) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
            (betCount * 1.5) * sortesJugados

        override fun calculateBetsCount(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Int {
            if (numbers.isEmpty()) return 1
            var totalBets = 0
            for (block in numbers) {
                val count = parseBlockNumbers(block).size
                totalBets += if (count >= 5) combinations(count, 5) else 0
            }
            return totalBets.coerceAtLeast(1)
        }

        override fun isMultiple(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Boolean {
            return numbers.any { block ->
                val count = parseBlockNumbers(block).size
                count > 5
            }
        }
    }

    object Euromillones : LotteryGame("7", "Euromillones", "EMIL", "02", 0xFF283593) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
            (betCount * 2.5) * sortesJugados

        override fun calculateBetsCount(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Int {
            if (numbers.isEmpty()) return 1
            var totalBets = 0
            for (i in numbers.indices) {
                val nCount = parseBlockNumbers(numbers[i]).size
                val sCount = parseBlockNumbers(stars?.getOrNull(i)).size.coerceAtLeast(if (nCount >= 5) 2 else 0)
                val numComb = if (nCount >= 5) combinations(nCount, 5) else 0
                val starComb = if (sCount >= 2) combinations(sCount, 2) else 1
                totalBets += numComb * starComb
            }
            return totalBets.coerceAtLeast(1)
        }

        override fun isMultiple(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Boolean {
            return numbers.indices.any { i ->
                val nCount = parseBlockNumbers(numbers[i]).size
                val sCount = parseBlockNumbers(stars?.getOrNull(i)).size
                (nCount > 0 && (nCount != 5 || sCount > 2))
            }
        }
    }

    object Nacional : LotteryGame("5", "Loteria Nacional", "LNAC", "09", 0xFF0277BD) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) = 3.0
    }

    object Eurodreams : LotteryGame("14", "Eurodreams", "EDMS", "14", 0xFF8E24AA) {
        override fun calculatePrice(betCount: Int, hasJoker: Boolean, sortesJugados: Int) =
            (betCount * 2.5) * sortesJugados

        override fun calculateBetsCount(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Int {
            if (numbers.isEmpty()) return 1
            var totalBets = 0
            for (i in numbers.indices) {
                val nCount = parseBlockNumbers(numbers[i]).size
                val dCount = parseBlockNumbers(dreams?.getOrNull(i)).size.coerceAtLeast(if (nCount > 0) 1 else 0)
                val numComb = when {
                    nCount == 5 -> 35
                    nCount >= 6 -> combinations(nCount, 6)
                    else -> 0
                }
                totalBets += numComb * dCount
            }
            return totalBets.coerceAtLeast(1)
        }

        override fun isMultiple(
            numbers: List<String>,
            stars: List<String>?,
            dreams: List<String>?,
            clave: List<String>?
        ): Boolean {
            return numbers.indices.any { i ->
                val nCount = parseBlockNumbers(numbers[i]).size
                val dCount = parseBlockNumbers(dreams?.getOrNull(i)).size
                nCount == 5 || nCount > 6 || dCount > 1
            }
        }
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

