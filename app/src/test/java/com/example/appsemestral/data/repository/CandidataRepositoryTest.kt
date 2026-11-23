package com.example.appsemestral.data.repository

import com.example.appsemestral.data.local.CandidataDao
import com.example.appsemestral.data.remote.EstadoRequest
import com.example.appsemestral.data.remote.PostulanteDto
import com.example.appsemestral.data.remote.WimApi
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.EstadoCandidatura
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class CandidataRepositoryTest {

    private val api = mockk<WimApi>()
    private val dao = mockk<CandidataDao>(relaxed = true)
    private val historial = mockk<HistorialRepository>(relaxed = true)
    private lateinit var repo: CandidataRepository

    private fun dto(id: Long) = PostulanteDto(
        id = id, nombre = "Ana Demo", correo = "ana@ejemplo.cl", telefono = "+56 9 1111 2222",
        area = "Seguridad ofensiva (Red Team)", aniosExperiencia = 5,
        resumenExperiencia = "Resumen ficticio de al menos treinta caracteres.",
        linkedinUrl = "https://www.linkedin.com/in/ana-demo", estado = "POSTULADA",
        fechaRegistro = 1L, registradaPor = "Evaluadora Demo"
    )

    private fun local(id: Long = 0, cvUri: String? = null) = Candidata(
        id = id, nombre = "Ana Demo", correo = "ana@ejemplo.cl", telefono = "+56 9 1111 2222",
        area = "Seguridad ofensiva (Red Team)", aniosExperiencia = 5,
        resumenExperiencia = "Resumen ficticio de al menos treinta caracteres.",
        linkedinUrl = "https://www.linkedin.com/in/ana-demo", cvUri = cvUri,
        cvNombre = "cv.pdf", registradaPor = "Evaluadora Demo"
    )

    @Before
    fun setUp() {
        repo = CandidataRepository(dao, api, historial)
    }

    @Test
    fun `registrar guarda la copia local con el id que entrega el servidor`() = runTest {
        coEvery { api.crear(any()) } returns dto(10)

        val id = repo.registrar(local(cvUri = "content://cv"))

        assertEquals(10L, id)
        coVerify { dao.upsert(match { it.id == 10L && it.cvUri == "content://cv" }) }
    }

    @Test
    fun `refrescar conserva el cvUri local y borra las que ya no existen`() = runTest {
        coEvery { api.listar() } returns listOf(dto(1))
        coEvery { dao.obtenerTodas() } returns listOf(local(id = 1, cvUri = "content://local"))

        repo.refrescar()

        coVerify { dao.eliminarNoIncluidas(listOf(1L)) }
        coVerify { dao.upsertTodas(match { it.single().cvUri == "content://local" }) }
    }

    @Test
    fun `eliminar borra la copia local aunque el servidor responda 404`() = runTest {
        coEvery { api.eliminar(5) } returns Response.error(404, "".toResponseBody())

        repo.eliminar(5, "Evaluadora Demo")

        coVerify { dao.eliminar(5) }
    }

    @Test(expected = HttpException::class)
    fun `eliminar lanza error si el servidor responde 500`() = runTest {
        coEvery { api.eliminar(5) } returns Response.error(500, "".toResponseBody())
        repo.eliminar(5, "Evaluadora Demo")
    }

    @Test
    fun `resolver envia el estado al servidor y actualiza Room`() = runTest {
        coEvery { dao.obtenerPorId(3) } returns local(id = 3).copy(estado = EstadoCandidatura.EVALUADA)
        coEvery { api.cambiarEstado(3, any()) } returns dto(3)

        repo.resolver(3, EstadoCandidatura.SELECCIONADA, "Dirección Demo")

        coVerify { api.cambiarEstado(3, EstadoRequest("SELECCIONADA")) }
        coVerify { dao.actualizarEstado(3, EstadoCandidatura.SELECCIONADA) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `resolver rechaza estados que no son de cierre`() = runTest {
        repo.resolver(3, EstadoCandidatura.POSTULADA, "Dirección Demo")
    }

    @Test
    fun `sincronizarEstado envia el estado local al servidor`() = runTest {
        coEvery { dao.obtenerPorId(4) } returns local(id = 4).copy(estado = EstadoCandidatura.EVALUADA)
        coEvery { api.cambiarEstado(4, any()) } returns dto(4)

        repo.sincronizarEstado(4)

        coVerify { api.cambiarEstado(4, EstadoRequest("EVALUADA")) }
    }
}
