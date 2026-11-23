package com.example.appsemestral.nativo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Recursos nativos de Android usados por la app:
 * - Selector de documentos del sistema (Storage Access Framework) para adjuntar el CV en PDF.
 * - Intents implícitos para abrir el PDF, marcar un teléfono y escribir un correo.
 */
object RecursosNativos {

    const val MIME_PDF = "application/pdf"

    /**
     * Pide permiso de lectura persistente sobre el PDF elegido, para poder abrirlo
     * después de reiniciar la app, y devuelve el nombre visible del archivo.
     */
    fun prepararCv(context: Context, uri: Uri): String {
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Algunos proveedores no entregan permisos persistentes; el archivo sirve igual en esta sesión.
        }
        return nombreArchivo(context, uri) ?: uri.lastPathSegment ?: "cv.pdf"
    }

    private fun nombreArchivo(context: Context, uri: Uri): String? =
        try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
        } catch (_: Exception) {
            null
        }

    /** Devuelve un mensaje de error para mostrar, o null si se abrió bien. */
    fun abrirPdf(context: Context, uri: String): String? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(uri), MIME_PDF)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return lanzar(context, intent, "No hay una app para abrir PDF en este teléfono")
    }

    fun llamar(context: Context, telefono: String): String? =
        lanzar(
            context,
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${telefono.filter { it.isDigit() || it == '+' }}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            "No hay una app de teléfono disponible"
        )

    fun escribirCorreo(context: Context, correo: String, asunto: String): String? =
        lanzar(
            context,
            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$correo"))
                .putExtra(Intent.EXTRA_SUBJECT, asunto)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            "No hay una app de correo configurada"
        )

    private fun lanzar(context: Context, intent: Intent, sinApp: String): String? =
        try {
            context.startActivity(intent)
            null
        } catch (_: ActivityNotFoundException) {
            sinApp
        } catch (_: SecurityException) {
            "No hay permiso para abrir el archivo. Vuelve a adjuntar el CV."
        }
}
