package com.example.myapplication

import com.example.myapplication.validation.CandidataValidator
import org.junit.Assert.assertEquals
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
}
