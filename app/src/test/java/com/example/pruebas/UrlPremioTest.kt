package com.example.pruebas

import com.example.pruebas.data.network.webView.multiplesSorteos
import com.example.pruebas.data.network.webView.urlProximosForGame
import com.example.pruebas.data.network.webView.urlPremioLNACPorNumero
import com.example.pruebas.data.network.webView.urlsGetPremio.formatNumbersString
import com.example.pruebas.data.network.webView.urlsGetPremio.transformListToParams
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioBONO
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEDMS
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioELGR
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioEMIL
import com.example.pruebas.data.network.webView.urlsGetPremio.urlPremioLAPR
import com.example.pruebas.domain.Ticket
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlPremioTest {

    @Test
    fun testFormatNumbersString() {
        assertEquals("4y5y6y23y36y15", formatNumbersString("04,05,06,23,36,15"))
        assertEquals("1y2y3y4y5y6", formatNumbersString("1 2 3 4 5 6"))
        assertEquals("8y20y24y1y29", formatNumbersString("08y20y24y01y29"))
        assertEquals("", formatNumbersString("   "))
    }

    @Test
    fun testTransformListToParams() {
        val numbers = listOf("04,05,06,23,36,15", "01,02,03,22,25,16")
        val params = transformListToParams(numbers)
        assertEquals("4y5y6y23y36y15", params["bloque1"])
        assertEquals("1y2y3y22y25y16", params["bloque2"])
    }

    @Test
    fun testUrlPremioBONO() {
        val ticket = Ticket(
            idSorteo = "937601050",
            numbers = listOf("04,05,06,23,36,15", "01,02,03,22,25,16"),
            reintegro = "4"
        )
        val url = urlPremioBONO(ticket)
        assertTrue(url.contains("drawId=937601050"))
        assertTrue(url.contains("modalidad=simple"))
        assertTrue(url.contains("bloque1=4y5y6y23y36y15"))
        assertTrue(url.contains("bloque2=1y2y3y22y25y16"))
        assertTrue(url.contains("reintegro=4"))
    }

    @Test
    fun testUrlPremioLAPR_SafeNulls() {
        val ticketWithNulls = Ticket(
            idSorteo = "1005204002",
            numbers = listOf("01,02,03,04,05,06"),
            reintegro = null,
            joker = null
        )
        // Should not throw NullPointerException
        val url = urlPremioLAPR(ticketWithNulls)
        assertTrue(url.contains("drawId=1005204002"))
        assertTrue(url.contains("bloque1=1y2y3y4y5y6"))
        assertTrue(!url.contains("reintegro="))
        assertTrue(!url.contains("joker="))
    }

    @Test
    fun testUrlPremioLAPR_WithJokerAndReintegro() {
        val ticket = Ticket(
            idSorteo = "1005204002",
            numbers = listOf("01,02,03,04,05,06"),
            reintegro = "4",
            joker = "4224561"
        )
        val url = urlPremioLAPR(ticket)
        assertTrue(url.contains("reintegro=4"))
        assertTrue(url.contains("joker=4224561"))
    }

    @Test
    fun testUrlPremioEMIL() {
        val ticket = Ticket(
            idSorteo = "1254702089",
            numbers = listOf("08,20,24,01,29"),
            stars = listOf("08,09")
        )
        val url = urlPremioEMIL(ticket)
        assertTrue(url.contains("drawId=1254702089"))
        assertTrue(url.contains("bloque1=8y20y24y1y29"))
        assertTrue(url.contains("estrellas1=8y9"))
    }

    @Test
    fun testUrlPremioEDMS() {
        val ticket = Ticket(
            idSorteo = "1176214043",
            numbers = listOf("28,27,01,02,03,16"),
            dreams = listOf("04")
        )
        val url = urlPremioEDMS(ticket)
        assertTrue(url.contains("drawId=1176214043"))
        assertTrue(url.contains("bloque1=28y27y1y2y3y16"))
        assertTrue(url.contains("numero1=4"))
    }

    @Test
    fun testUrlPremioELGR() {
        val ticket = Ticket(
            idSorteo = "940205013",
            numbers = listOf("28,27,01,02,03"),
            clave = listOf("04")
        )
        val url = urlPremioELGR(ticket)
        assertTrue(url.contains("drawId=940205013"))
        assertTrue(url.contains("bloque1=28y27y1y2y3"))
        assertTrue(url.contains("reintegro1=4"))
    }

    @Test
    fun testUrlPremioLNACPorNumero() {
        val url = urlPremioLNACPorNumero("81030", "123456")
        assertTrue(url.contains("decimo=81030"))
        assertTrue(url.contains("idSorteos=123456"))
    }

    @Test
    fun testUrlProximosForGame() {
        val urlLapr = urlProximosForGame("LAPR", 10)
        assertEquals("https://www.loteriasyapuestas.es/servicios/proximosv3?game_id=LAPR&num=10", urlLapr)

        val urlBono = urlProximosForGame("BONO", 5)
        assertEquals("https://www.loteriasyapuestas.es/servicios/proximosv3?game_id=BONO&num=5", urlBono)
    }

    @Test
    fun testMultiplesSorteos() {
        val ticket1 = Ticket(idSorteo = "1325401286", sorteosJugados = 2)
        val list1 = multiplesSorteos(ticket1)
        assertEquals(2, list1.size)
        assertEquals("1325401286", list1[0])
        assertEquals("1325501287", list1[1])

        val ticket2 = Ticket(idSorteo = "1005204002", sorteosJugados = 3)
        val list2 = multiplesSorteos(ticket2)
        assertEquals(3, list2.size)
        assertEquals("1005204002", list2[0])
        assertEquals("1005304003", list2[1])
        assertEquals("1005404004", list2[2])
    }
}
