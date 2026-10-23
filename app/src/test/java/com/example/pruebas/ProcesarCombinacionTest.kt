package com.example.pruebas

import com.example.pruebas.presentation.extraDetailScreen.parseCombinacion
import org.junit.Assert.assertEquals
import org.junit.Test

class ProcesarCombinacionTest {

    @Test
    fun testProcesarCombinacionPrimitiva() {
        val input = "04 - 19 - 27 - 31 - 34 - 39 C(5) R(9)"
        val result = parseCombinacion(input)

        assertEquals(listOf("04", "19", "27", "31", "34", "39"), result.combinacion)
        assertEquals("5", result.complimentario)
        assertEquals("9", result.reintegro)
        assertEquals(emptyList<String>(), result.estrellas)
    }

    @Test
    fun testProcesarCombinacionEuromillonesConETag() {
        val input = "04 - 19 - 27 - 31 - 34 E(05 - 09)"
        val result = parseCombinacion(input, "EMIL")

        assertEquals(listOf("04", "19", "27", "31", "34"), result.combinacion)
        assertEquals("", result.complimentario)
        assertEquals("", result.reintegro)
        assertEquals(listOf("05", "09"), result.estrellas)
    }

    @Test
    fun testProcesarCombinacionEuromillonesSieteNumeros() {
        val input = "04 - 19 - 27 - 31 - 34 - 05 - 09"
        val result = parseCombinacion(input, "EMIL")

        assertEquals(listOf("04", "19", "27", "31", "34"), result.combinacion)
        assertEquals(listOf("05", "09"), result.estrellas)
    }

    @Test
    fun testProcesarCombinacionElGordo() {
        val input = "04 - 19 - 27 - 31 - 34 Clave(5)"
        val result = parseCombinacion(input, "ELGR")

        assertEquals(listOf("04", "19", "27", "31", "34"), result.combinacion)
        assertEquals("5", result.reintegro)
    }

    @Test
    fun testProcesarCombinacionElGordoSeisNumeros() {
        val input = "04 - 19 - 27 - 31 - 34 - 05"
        val result = parseCombinacion(input, "ELGR")

        assertEquals(listOf("04", "19", "27", "31", "34"), result.combinacion)
        assertEquals("05", result.reintegro)
    }

    @Test
    fun testProcesarCombinacionEurodreamsConSTag() {
        val input = "04 - 19 - 27 - 31 - 34 - 39 S(5)"
        val result = parseCombinacion(input, "EDMS")

        assertEquals(listOf("04", "19", "27", "31", "34", "39"), result.combinacion)
        assertEquals("5", result.reintegro)
    }

    @Test
    fun testProcesarCombinacionEurodreamsConCTag() {
        val input = "04 - 19 - 27 - 31 - 34 - 39 C(5)"
        val result = parseCombinacion(input, "EDMS")

        assertEquals(listOf("04", "19", "27", "31", "34", "39"), result.combinacion)
        assertEquals("5", result.reintegro)
        assertEquals("", result.complimentario)
    }

    @Test
    fun testProcesarCombinacionEurodreamsSieteNumeros() {
        val input = "04 - 19 - 27 - 31 - 34 - 39 - 05"
        val result = parseCombinacion(input, "EDMS")

        assertEquals(listOf("04", "19", "27", "31", "34", "39"), result.combinacion)
        assertEquals("05", result.reintegro)
    }
}
