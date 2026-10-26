# Entorno de desarrollo en la nube

Los scripts preparan el checkout existente en
`/workspace/APLICACION_DESARROLLO_MOVIL`. Cada tarea en la nube ya está aislada;
no es necesario crear un Git worktree salvo que el usuario lo solicite.

## Instalación

Desde la raíz del repositorio:

```bash
bash scripts/cloud/install.sh
```

El script instala JDK 21 y las herramientas del SDK Android, configura el proxy
de Java a partir de `HTTPS_PROXY` y consulta las dependencias de la aplicación.
Usa Android SDK 36.1, build-tools 36.0.0 y el wrapper Gradle 9.4.1 del proyecto.
Requiere Bash, Python 3, curl, tar, unzip y el almacén de certificados Java del
sistema en `/etc/ssl/certs/java/cacerts`.

Los paquetes y las cachés quedan fuera del checkout, bajo `/workspace`. El
script verifica las descargas del JDK y las herramientas Android, conserva la
verificación TLS y no guarda credenciales. La aplicación no necesita un backend
ni secretos para compilar o ejecutar pruebas locales.

## Compilación y pruebas

Activa las herramientas en cada nueva terminal:

```bash
cd /workspace/APLICACION_DESARROLLO_MOVIL
source scripts/cloud/env.sh
bash gradlew :app:assembleDebug :app:testDebugUnitTest --max-workers=2 --console=plain
```

Se usa `bash gradlew` porque el wrapper del repositorio no tiene permiso de
ejecución. Tras una compilación correcta, el APK estará en
`app/build/outputs/apk/debug/app-debug.apk` y los resultados de pruebas en
`app/build/test-results/testDebugUnitTest`.

## Resultado de la configuración inicial

La instalación y la consulta de dependencias finalizaron correctamente en dos
ejecuciones consecutivas. La compilación alcanzó `:app:compileDebugKotlin`, pero
falló por errores del código existente:

- `RankingScreen.kt`: import inválido de `KeyboardOptions`.
- `WimApp.kt`: llamada a una función composable desde la función de navegación,
  asignación incompatible al estado `Pantalla` y una rama `RANKING` faltante.

Estos errores impiden generar el APK y ejecutar `:app:testDebugUnitTest`. No se
modificó el código de la aplicación durante la configuración.

Como diagnóstico independiente, se ejecutaron los siete tests JVM existentes
contra las reglas puras de validación, con Kotlin 2.2.10 y JUnit 4.13.2: todos
pasaron. Esa ejecución aislada no valida el módulo Android completo.

No hay un dispositivo ni un emulador configurado. La interfaz y las pruebas
instrumentadas requieren uno disponible por separado.
