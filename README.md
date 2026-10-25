# 📱 WIM 100 — Aplicación de Evaluación de Candidatas

**Duoc UC · DSY1105 · Desarrollo de Aplicaciones Móviles**

Proyecto académico de **Ángel González, Roselín Verdu y Andrés Albornoz**. La aplicación permite gestionar candidatas ficticias para un programa de selección, con interfaz en **Kotlin, Jetpack Compose y Material Design 3**.

> **Estado: versión inicial 0.1.0.** Es un primer incremento funcional para el primer commit del equipo, **no** la versión final de las evaluaciones EP2/EP3. Toda la información de prueba es ficticia.

## ✅ Funciones de esta versión (primer commit)

- Inicio de sesión **demostrativo** con nombre y selección de rol (Evaluadora o Dirección; sin autenticación real).
- Listado de candidatas; opción para agregar y abrir el detalle.
- Formulario: nombre y apellido, correo, área y años de experiencia.
- Validación de campos y mensajes visibles; bloqueo de correos duplicados.
- **Persistencia local** mediante `SharedPreferences` + JSON: los registros permanecen al cerrar y volver a abrir.
- Recurso nativo: selector de PDF del sistema Android para adjuntar CV; opción de abrirlo con otra aplicación.
- Animación sencilla de transición entre pantallas mediante `Crossfade`.
- Gestión de estado con `StateFlow` en un `ViewModel`.
- Pruebas unitarias de las reglas del formulario.

## 🛠 Tecnologías

Kotlin · Jetpack Compose · Material 3 · MVVM básico · StateFlow · SharedPreferences · Gradle · Git/GitHub · Trello.

**Pendiente para siguientes commits:** migración a Room, evaluación por criterios, ranking, historial, más pruebas y mejoras de diseño. **No afirmamos** que esas funciones estén ya implementadas.

## ▶️ Ejecutar en Android Studio / IntelliJ IDEA con soporte Android

1. Clona el repositorio o descomprime el ZIP.
2. Abre en el IDE la **carpeta raíz que contiene `settings.gradle.kts` y `gradlew.bat`**, no la subcarpeta `app`.
3. Instala JDK 21 y el Android SDK solicitado por Gradle (versión 36.1 según configuración original del equipo).
4. Haz **Sync Project with Gradle Files** con conexión a internet en la primera sincronización.
5. Inicia un emulador (Android API 26+ recomendado) o conecta un dispositivo con depuración USB.
6. Selecciona **app**, presiona **Run**.
7. En Windows, para las pruebas unitarias: `gradlew.bat testDebugUnitTest`.

## 🧪 Prueba manual del primer incremento

1. Ingresa con el nombre `Ángel González` y rol Evaluadora.
2. En **Registrar candidata**, prueba primero guardar con campos vacíos: aparecerán mensajes de error.
3. Registra: `Camila Torres`, `camila@ejemplo.cl`, `Desarrollo`, `2`.
4. Selecciona opcionalmente un PDF del teléfono y guarda.
5. Abre el detalle de Camila y comprueba sus datos (y el PDF si se adjuntó).
6. Cierra la app y vuelve a abrir: el registro debe continuar en el listado.
7. Repite el mismo correo: el registro duplicado debe rechazarse.

## 🗂 Estructura del código

```text
app/src/main/java/com/example/myapplication/
├── MainActivity.kt
├── model/Candidata.kt                   # Modelo
├── validation/CandidataValidator.kt    # Reglas puras
├── data/CandidataRepository.kt         # Guardar y recuperar datos locales
├── ui/viewmodel/WimViewModel.kt        # Estado y coordinación
├── ui/WimApp.kt                        # Pantallas y navegación simple
└── ui/theme/                            # Material 3
```

## 👥 Equipo y seguimiento

**Integrantes:** Ángel González · Roselín Verdu · Andrés Albornoz. Cada historia posterior debe tener asignado un responsable real, un commit identificable, evidencia de pruebas y una tarjeta Trello.

Consulta [`docs/PRIMER_COMMIT.md`](docs/PRIMER_COMMIT.md) para subir esta versión sin sobrescribir el trabajo de otros, y [`docs/TRELLO_SECUENCIA.md`](docs/TRELLO_SECUENCIA.md) para los siguientes incrementos.

**Nota:** antes de calificar el proyecto como operativo para la defensa, es indispensable compilarlo y ejecutarlo en Android Studio; esta entrega no incluye un APK certificado ni pruebas instrumentadas en emulador.
