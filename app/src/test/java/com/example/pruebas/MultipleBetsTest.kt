package com.example.pruebas

import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioBONO
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEDMS
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioELGR
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEMIL
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioLAPR
import com.example.pruebas.domain.LotteryGame
import com.example.pruebas.domain.Ticket
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MultipleBetsTest {

    @Test
    fun testPrimitivaMultipleBets() {
        val cases = listOf(
            7 to 7.00,
            8 to 28.00,
            5 to 44.00,
            9 to 84.00,
            10 to 210.00,
            11 to 462.00
        )

        for (case in cases) {
            val numCount = case.first
            val expectedPrice = case.second
            val numbersList = (1..numCount).map { it.toString().padStart(2, '0') }
            val block = numbersList.joinToString(",")
            val numbers = listOf(block)

            val betCount = LotteryGame.Primitiva.calculateBetsCount(numbers)
            val price = LotteryGame.Primitiva.calculatePrice(betCount, hasJoker = false, sortesJugados = 1)
            val isMultiple = LotteryGame.Primitiva.isMultiple(numbers)

            assertTrue("Primitiva $numCount numbers should be multiple", isMultiple)
            assertEquals("Primitiva $numCount numbers price mismatch", expectedPrice, price, 0.001)

            val ticket = Ticket(gameId = "LAPR", numbers = numbers, multiple = isMultiple)
            val url = urlPremioLAPR(ticket)
            assertTrue(url.contains("modalidad=multiple"))
        }

        // Simple bet check
        val simpleNumbers = listOf("01,02,03,04,05,06")
        val simpleCount = LotteryGame.Primitiva.calculateBetsCount(simpleNumbers)
        val simplePrice = LotteryGame.Primitiva.calculatePrice(simpleCount, hasJoker = false, sortesJugados = 1)
        assertFalse(LotteryGame.Primitiva.isMultiple(simpleNumbers))
        assertEquals(1.00, simplePrice, 0.001)
    }

    @Test
    fun testBonolotoMultipleBets() {
        val cases = listOf(
            7 to 3.50,
            8 to 14.00,
            5 to 22.00,
            9 to 42.00,
            10 to 105.00,
            11 to 231.00
        )

        for (case in cases) {
            val numCount = case.first
            val expectedPrice = case.second
            val numbersList = (1..numCount).map { it.toString().padStart(2, '0') }
            val block = numbersList.joinToString(",")
            val numbers = listOf(block)

            val betCount = LotteryGame.Bonoloto.calculateBetsCount(numbers)
            val price = LotteryGame.Bonoloto.calculatePrice(betCount, hasJoker = false, sortesJugados = 1)
            val isMultiple = LotteryGame.Bonoloto.isMultiple(numbers)

            assertTrue("Bonoloto $numCount numbers should be multiple", isMultiple)
            assertEquals("Bonoloto $numCount numbers price mismatch", expectedPrice, price, 0.001)

            val ticket = Ticket(gameId = "BONO", numbers = numbers, multiple = isMultiple)
            val url = urlPremioBONO(ticket)
            assertTrue(url.contains("modalidad=multiple"))
        }
    }

    @Test
    fun testEuromillonesMultipleBets() {
        val cases = listOf(
            Pair(5, 3) to 7.50,
            Pair(5, 4) to 15.00,
            Pair(5, 5) to 25.00,
            Pair(6, 2) to 15.00,
            Pair(6, 3) to 45.00,
            Pair(6, 4) to 90.00,
            Pair(6, 5) to 150.00,
            Pair(7, 2) to 52.50,
            Pair(7, 3) to 157.50,
            Pair(7, 4) to 315.00,
            Pair(7, 5) to 525.00,
            Pair(8, 2) to 140.00,
            Pair(8, 3) to 420.00,
            Pair(8, 4) to 840.00,
            Pair(8, 5) to 1400.00,
            Pair(9, 2) to 315.00,
            Pair(9, 3) to 945.00,
            Pair(9, 4) to 1890.00,
            Pair(9, 5) to 3150.00,
            Pair(10, 2) to 630.00,
            Pair(10, 3) to 1890.00,
            Pair(10, 4) to 3780.00,
            Pair(10, 5) to 6300.00
        )

        for ((counts, expectedPrice) in cases) {
            val (numCount, starCount) = counts
            val numbersList = (1..numCount).map { it.toString().padStart(2, '0') }
            val starsList = (1..starCount).map { it.toString().padStart(2, '0') }

            val numbers = listOf(numbersList.joinToString(","))
            val stars = listOf(starsList.joinToString(","))

            val betCount = LotteryGame.Euromillones.calculateBetsCount(numbers, stars)
            val price = LotteryGame.Euromillones.calculatePrice(betCount, hasJoker = false, sortesJugados = 1)
            val isMultiple = LotteryGame.Euromillones.isMultiple(numbers, stars)

            assertTrue("Euromillones $numCount numbers, $starCount stars should be multiple", isMultiple)
            assertEquals("Euromillones $numCount numbers, $starCount stars price mismatch", expectedPrice, price, 0.001)

            val ticket = Ticket(gameId = "EMIL", numbers = numbers, stars = stars, multiple = isMultiple)
            val url = urlPremioEMIL(ticket)
            assertTrue(url.contains("modalidad=multiple"))
        }
    }

    @Test
    fun testEurodreamsMultipleBets() {
        val cases = listOf(
            7 to 17.50,
            8 to 70.00,
            5 to 87.50,
            9 to 210.00,
            10 to 525.00
        )

        for (case in cases) {
            val numCount = case.first
            val expectedPrice = case.second
            val numbersList = (1..numCount).map { it.toString().padStart(2, '0') }
            val numbers = listOf(numbersList.joinToString(","))
            val dreams = listOf("01")

            val betCount = LotteryGame.Eurodreams.calculateBetsCount(numbers, dreams = dreams)
            val price = LotteryGame.Eurodreams.calculatePrice(betCount, hasJoker = false, sortesJugados = 1)
            val isMultiple = LotteryGame.Eurodreams.isMultiple(numbers, dreams = dreams)

            assertTrue("Eurodreams $numCount numbers should be multiple", isMultiple)
            assertEquals("Eurodreams $numCount numbers price mismatch", expectedPrice, price, 0.001)

            val ticket = Ticket(gameId = "EDMS", numbers = numbers, dreams = dreams, multiple = isMultiple)
            val url = urlPremioEDMS(ticket)
            assertTrue(url.contains("modalidad=multiple"))
        }
    }

    @Test
    fun testGordoMultipleBets() {
        val cases = listOf(
            6 to 9.00,
            7 to 31.50,
            8 to 84.00,
            9 to 189.00,
            10 to 378.00,
            11 to 693.00
        )

        for (case in cases) {
            val numCount = case.first
            val expectedPrice = case.second
            val numbersList = (1..numCount).map { it.toString().padStart(2, '0') }
            val numbers = listOf(numbersList.joinToString(","))
            val clave = listOf("01")

            val betCount = LotteryGame.Gordo.calculateBetsCount(numbers, clave = clave)
            val price = LotteryGame.Gordo.calculatePrice(betCount, hasJoker = false, sortesJugados = 1)
            val isMultiple = LotteryGame.Gordo.isMultiple(numbers, clave = clave)

            assertTrue("Gordo $numCount numbers should be multiple", isMultiple)
            assertEquals("Gordo $numCount numbers price mismatch", expectedPrice, price, 0.001)

            val ticket = Ticket(gameId = "ELGR", numbers = numbers, clave = clave, multiple = isMultiple)
            val url = urlPremioELGR(ticket)
            assertTrue(url.contains("modalidad=multiple"))
        }
    }
}
