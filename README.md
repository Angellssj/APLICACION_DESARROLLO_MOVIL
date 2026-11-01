# 📱 WIM 100 — Aplicación de Evaluación de Candidatas

**Duoc UC · DSY1105 · Desarrollo de Aplicaciones Móviles**

Proyecto académico de **Ángel González, Roselín Verdu y Andrés Albornoz**. La aplicación permite gestionar y evaluar candidatas ficticias para un programa de selección, con interfaz en **Kotlin, Jetpack Compose y Material Design 3**.

> **Estado: versión 0.2.0.** Compila, genera el APK de debug y pasa las 16 pruebas unitarias. **No se ha probado en emulador ni en dispositivo:** hacerlo es el siguiente paso antes de la defensa. Toda la información de prueba es ficticia.

## ✅ Funciones

- Inicio de sesión **demostrativo** con nombre y rol (sin autenticación real). El rol define qué se puede hacer:
  - **Evaluadora:** registra candidatas y las evalúa.
  - **Dirección:** consulta el ranking y puede eliminar candidatas (con confirmación).
- Listado de candidatas (`LazyColumn`), registro y detalle.
- Formulario: nombre y apellido, correo, área, años de experiencia, teléfono (opcional) y CV en PDF (opcional, se muestra el nombre del archivo).
- Validación de campos con mensajes visibles; bloqueo de correos duplicados (índice único en la base de datos).
- **Evaluación por criterios:** competencia técnica, comunicación, trabajo en equipo y resolución de problemas, de 1 a 10, con comentario opcional. Cada candidata conserva su historial de evaluaciones (quién, cuándo y qué puntajes).
- **Ranking** ordenado por el promedio de todas sus evaluaciones; las candidatas sin evaluar quedan al final.
- **Persistencia con Room** (`wim100.db`). Los registros de la versión 0.1.0 guardados en `SharedPreferences` se importan una sola vez al abrir la nueva versión.
- Recurso nativo: selector de PDF del sistema Android y apertura del CV con otra aplicación.
- Animación de transición entre pantallas con `Crossfade`.
- Estado con `StateFlow` en un `ViewModel`.

## 🛠 Tecnologías

Kotlin · Jetpack Compose · Material 3 · MVVM · StateFlow · Room (KSP) · Gradle · Git/GitHub · Trello.

**Pendiente:** pruebas instrumentadas y de interfaz en emulador, edición de candidatas, y más mejoras de diseño.

## ▶️ Ejecutar en Android Studio

1. Clona el repositorio o descomprime el ZIP **en una ruta sin tildes ni caracteres especiales** (por ejemplo `C:\dev\WIM100`). Android Gradle Plugin falla en Windows con rutas como `...\Programación\...`.
2. Abre en Android Studio la **carpeta raíz que contiene `settings.gradle.kts` y `gradlew.bat`**, no la subcarpeta `app`.
3. Instala JDK 21 y el Android SDK 37 (`compileSdk = 37`, `targetSdk = 36`, `minSdk = 24`). Android Studio los ofrece al sincronizar.
4. Haz **Sync Project with Gradle Files** con conexión a internet en la primera sincronización.
5. Inicia un emulador (Android API 26+ recomendado) o conecta un dispositivo con depuración USB.
6. Selecciona **app** y presiona **Run**.
7. Pruebas unitarias desde la terminal: `gradlew.bat testDebugUnitTest`.

## 🧪 Prueba manual sugerida

1. Ingresa como `Ángel González`, rol **Evaluadora**.
2. En **Registrar**, guarda con campos vacíos: aparecerán mensajes de error. Prueba también un teléfono inválido como `1234`.
3. Registra `Camila Torres`, `camila@ejemplo.cl`, `Desarrollo`, `2`, y opcionalmente un PDF.
4. Abre el detalle de Camila, pulsa **Evaluar candidata**, mueve los controles y guarda.
5. Registra una segunda candidata, evalúala con otros puntajes y revisa el orden en **Ranking**.
6. Cierra la app y vuelve a abrirla: registros y evaluaciones deben seguir ahí.
7. Repite el mismo correo: debe rechazarse.
8. Sal, ingresa con rol **Dirección** y comprueba que no aparece "Evaluar" pero sí "Eliminar candidata".

## 🗂 Estructura del código

```text
app/src/main/java/com/example/myapplication/
├── MainActivity.kt
├── model/
│   ├── Candidata.kt          # Entidad Room
│   ├── Evaluacion.kt         # Entidad Room + criterios y escala
│   ├── Ranking.kt            # Regla pura del ranking
│   └── Rol.kt                # Perfiles de la sesión
├── validation/CandidataValidator.kt   # Reglas puras de formulario y puntajes
├── data/
│   ├── WimDao.kt / WimDatabase.kt     # Room
│   └── CandidataRepository.kt         # Acceso a datos + importación desde v0.1.0
├── ui/
│   ├── viewmodel/WimViewModel.kt      # Estado y coordinación
│   ├── WimApp.kt                      # Navegación, sesión, listado, formulario, detalle
│   ├── EvaluacionScreens.kt           # Evaluación y ranking
│   └── theme/                         # Material 3
└── (tests) validation + ranking en app/src/test
```

## 👥 Equipo y seguimiento

**Integrantes:** Ángel González · Roselín Verdu · Andrés Albornoz. Cada historia debe tener asignado un responsable real, un commit identificable, evidencia de pruebas y una tarjeta Trello.

Consulta [`docs/PRIMER_COMMIT.md`](docs/PRIMER_COMMIT.md) para subir esta versión sin sobrescribir el trabajo de otros, y [`docs/TRELLO_SECUENCIA.md`](docs/TRELLO_SECUENCIA.md) para el seguimiento de las historias.

**Nota:** antes de calificar el proyecto como operativo para la defensa, es indispensable ejecutarlo en un emulador o dispositivo; esta entrega no incluye pruebas instrumentadas ni un APK certificado.
