package com.example.appsemestral.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CandidataValidatorTest {

    @Test
    fun `nombre requiere entre 3 y 60 caracteres`() {
        assertFalse(CandidataValidator.nombre("").esValido)
        assertFalse(CandidataValidator.nombre("Al").esValido)
        assertTrue(CandidataValidator.nombre("Ana Demo").esValido)
    }

    @Test
    fun `correo valida el formato`() {
        assertFalse(CandidataValidator.correo("sin-arroba").esValido)
        assertTrue(CandidataValidator.correo("ana@ejemplo.cl").esValido)
    }

    @Test
    fun `telefono acepta 9 digitos chilenos y los formatea`() {
        assertTrue(CandidataValidator.telefono("9 1111 2222").esValido)
        assertFalse(CandidataValidator.telefono("1234").esValido)
        assertEquals("+56 9 1111 2222", CandidataValidator.formatearTelefono("911112222"))
        assertEquals("+56 9 1111 2222", CandidataValidator.formatearTelefono("+56 9 1111 2222"))
    }

    @Test
    fun `anios debe estar entre 0 y 50`() {
        assertFalse(CandidataValidator.anios("-1").esValido)
        assertFalse(CandidataValidator.anios("51").esValido)
        assertFalse(CandidataValidator.anios("abc").esValido)
        assertTrue(CandidataValidator.anios("10").esValido)
    }

    @Test
    fun `resumen requiere al menos 30 caracteres`() {
        assertFalse(CandidataValidator.resumen("muy corto").esValido)
        assertTrue(CandidataValidator.resumen("Resumen ficticio de al menos treinta caracteres.").esValido)
    }

    @Test
    fun `linkedin debe empezar con https`() {
        assertFalse(CandidataValidator.linkedin("http://x.com").esValido)
        assertTrue(CandidataValidator.linkedin("https://www.linkedin.com/in/ana").esValido)
    }
}
