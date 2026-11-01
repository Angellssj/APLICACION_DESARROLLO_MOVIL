package com.example.myapplication

import com.example.myapplication.validation.CandidataValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CandidataValidatorTest {
    @Test fun aceptaDatosValidos() {
        assertTrue(CandidataValidator.validar("Camila Torres", "camila@ejemplo.cl", "Desarrollo", "2").isEmpty())
    }

    @Test fun rechazaNombreIncompleto() {
        assertTrue(CandidataValidator.validar("Ana", "ana@ejemplo.cl", "Datos", "1").containsKey("nombre"))
    }

    @Test fun rechazaCorreoSinArroba() {
        assertTrue(CandidataValidator.validar("Ana López", "ana-ejemplo.cl", "Datos", "1").containsKey("correo"))
    }

    @Test fun exigeArea() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", " ", "1").containsKey("area"))
    }

    @Test fun rechazaExperienciaNegativa() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "-1").containsKey("experiencia"))
    }

    @Test fun rechazaExperienciaTexto() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "dos").containsKey("experiencia"))
    }

    @Test fun telefonoEsOpcional() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "1", "").isEmpty())
    }

    @Test fun aceptaTelefonoConFormato() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "1", "+56 9 1234-5678").isEmpty())
    }

    @Test fun rechazaTelefonoCortoOConLetras() {
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "1", "1234").containsKey("telefono"))
        assertTrue(CandidataValidator.validar("Ana López", "ana@ejemplo.cl", "Datos", "1", "abc12345678").containsKey("telefono"))
    }

    @Test fun aceptaPuntajesDentroDelRango() {
        assertNull(CandidataValidator.validarEvaluacion(listOf(1, 5, 10, 7)))
    }

    @Test fun rechazaPuntajeFueraDeRango() {
        assertNotNull(CandidataValidator.validarEvaluacion(listOf(0, 5, 10, 7)))
        assertNotNull(CandidataValidator.validarEvaluacion(listOf(1, 5, 11, 7)))
    }

    @Test fun rechazaCantidadIncorrectaDeCriterios() {
        assertNotNull(CandidataValidator.validarEvaluacion(listOf(5, 5, 5)))
    }
}
