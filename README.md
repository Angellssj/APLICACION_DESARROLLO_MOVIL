# 📱 WIM 100 – App de evaluación de candidatas

Aplicación Android para el programa **WIM 100** de **DYM Cyber**. Permite registrar candidatas a mentorías de ciberseguridad, evaluarlas con criterios ponderados, ver un ranking y que la Dirección resuelva quién queda seleccionada.

Proyecto académico de **Desarrollo de Aplicaciones Móviles (DSY1105)**, Duoc UC, 2026. Evaluación Parcial 4 (encargo) y 5 (defensa).

> Todos los datos de la app y del backend son **ficticios**, como exige el caso.

## 👥 Integrantes

| Integrante | GitHub |
|---|---|
| Ángel González | [@Angellssj](https://github.com/Angellssj) |
| Roselín Verdu | [@Roselin2831](https://github.com/Roselin2831) |
| Andrés Albornoz | [@Andres-albornoz](https://github.com/Andres-albornoz) |

- **Tablero Trello:** PEGAR_ENLACE_DEL_TABLERO
- **Repositorio del backend:** https://github.com/Andres-albornoz/wim100-backend

## 🚀 Funcionalidades

| Funcionalidad | Rol | Dónde vive el dato |
|---|---|---|
| Inicio de sesión con usuario y contraseña | Ambos | Microservicio (`/api/auth/login`) |
| Listar, registrar, editar y eliminar candidatas | Evaluadora | Microservicio + copia local en Room |
| Generar un nombre ficticio en el formulario | Evaluadora | API externa (randomuser.me) |
| Adjuntar el CV en PDF y abrirlo | Evaluadora | Archivo en el teléfono; al servidor viaja solo el nombre |
| Evaluar por criterios ponderados (puntaje de 0 a 100) | Evaluadora | Room |
| Ranking en vivo, con el primer lugar destacado | Ambos | Room |
| Seleccionar o no seleccionar a una candidata evaluada | Dirección | Microservicio (`PATCH /estado`) |
| Historial de acciones | Ambos | Room |
| Llamar y escribir un correo a la candidata | Ambos | Recursos nativos (intents) |

### Estados de una candidatura
`POSTULADA` → `EN_EVALUACION` → `EVALUADA` → `SELECCIONADA` / `NO_SELECCIONADA`

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin 2.2 + Jetpack Compose + Material 3 | Interfaz |
| MVVM + StateFlow | Estado de las pantallas |
| Navigation Compose | Navegación entre pantallas |
| Retrofit 2.11 + Gson + OkHttp logging | Consumo del microservicio y de la API externa |
| Room 2.8 | Evaluaciones, criterios, historial y copia local de candidatas |
| JUnit 4 + MockK + kotlinx-coroutines-test | Pruebas unitarias |
| Gradle (Kotlin DSL) + `signingConfigs` | Build y APK firmado |

`minSdk 24` · `targetSdk 36`

## 🏗️ Arquitectura

La app es **híbrida**. Las candidatas y el login viven en el servidor. Las evaluaciones, los criterios y el historial se guardan en Room, que además mantiene una copia local de las candidatas para que las evaluaciones (con llave foránea a la candidata) sigan funcionando.

```
Pantalla (Compose) → ViewModel → Repository ─┬→ Retrofit → wim100-backend (Spring Boot) → MySQL
                                             ├→ Retrofit → randomuser.me (API externa)
                                             └→ Room (SQLite en el teléfono)
```

```
app/src/main/java/com/example/appsemestral
├── data
│   ├── local/        Room: base de datos, DAOs y datos semilla
│   ├── remote/       Retrofit: WimApi, RandomUserApi, DTOs, mapper y mensajes de error
│   └── repository/   Candidata, Evaluacion, Historial, Auth y RandomUser
├── di/               AppContainer (inyección de dependencias manual)
├── model/            Modelos, cálculo de puntaje y ranking
├── nativo/           Recursos nativos (selector de PDF e intents)
├── navigation/       Rutas y NavHost
├── ui/screens/       Login, Candidatas, Formulario, Detalle, Evaluación y Ranking
├── ui/viewmodel/     Un ViewModel por pantalla
└── validation/       Validaciones del formulario
```

## 🔌 Endpoints

### Microservicio propio (`wim100-backend`)
URL base: `ApiConfig.BASE_URL` en `data/remote/RetrofitClient.kt`.

| Método | Ruta | Uso en la app | Respuestas |
|---|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión | 200 · 401 |
| GET | `/api/postulantes` | Listar | 200 |
| GET | `/api/postulantes/{id}` | Ver detalle | 200 · 404 |
| POST | `/api/postulantes` | Registrar | 201 · 400 · 409 |
| PUT | `/api/postulantes/{id}` | Editar | 200 · 400 · 404 · 409 |
| PATCH | `/api/postulantes/{id}/estado` | Cambiar estado al evaluar o resolver | 200 · 404 |
| DELETE | `/api/postulantes/{id}` | Eliminar | 204 · 404 |

### API externa
| Método | URL | Uso |
|---|---|---|
| GET | `https://randomuser.me/api/` | Botón **Generar nombre ficticio** del formulario |

Solo se usa el nombre. El correo y el teléfono de randomuser no cumplen las validaciones de la app (dominios ficticios y teléfono chileno), y el dato no llega al microservicio hasta que la usuaria guarda.

**Microservicio vs. API externa:** el microservicio es nuestro (lo programamos, controlamos sus datos y su base MySQL). La API externa es de terceros, pública y sin key; si se cae, la app muestra un mensaje y el formulario sigue funcionando.

## 📲 Recursos nativos

| Recurso | Dónde | Cómo |
|---|---|---|
| Selector de documentos (PDF) | Formulario → *Adjuntar CV en PDF* | `ActivityResultContracts.OpenDocument` + permiso de lectura persistente |
| Visor de PDF | Detalle → *Abrir CV* | `Intent.ACTION_VIEW` |
| Teléfono | Detalle → *Llamar* | `Intent.ACTION_DIAL` (no pide permiso de llamada) |
| Correo | Detalle → *Correo* | `Intent.ACTION_SENDTO` con `mailto:` |

## 💻 Cómo ejecutar (backend primero)

### 1. Levantar el backend
Requisitos: Java 21 y MySQL 8 encendido. Detalle en el [README del backend](https://github.com/Andres-albornoz/wim100-backend).

```powershell
git clone https://github.com/Andres-albornoz/wim100-backend.git
cd wim100-backend
$env:SPRING_DATASOURCE_PASSWORD="tu_clave_de_mysql"
.\mvnw.cmd spring-boot:run
```
Cuando aparezca `Started ... on port 8080`, abre `http://localhost:8080/api/postulantes`: deben salir 3 candidatas en JSON.

### 2. Ejecutar la app en el emulador
1. Clona este repositorio y ábrelo en Android Studio.
2. **Sync Project with Gradle Files**.
3. Elige un emulador y presiona **Run ▶**. En el emulador, `10.0.2.2` apunta al computador, así que no hay que cambiar nada.

### 3. Ejecutar en un teléfono físico
1. Conecta el teléfono y el computador a la misma red Wi-Fi.
2. Busca la IP del computador con `ipconfig` (dato "Dirección IPv4").
3. En `data/remote/RetrofitClient.kt`, cambia `BASE_URL` a `"http://TU_IP:8080/api/"` (con la barra final).
4. Permite Java en el firewall de Windows para redes privadas.
5. Comprueba desde el navegador del teléfono que `http://TU_IP:8080/api/postulantes` responde.

### Usuarios de prueba
| Usuario | Contraseña | Rol |
|---|---|---|
| `evaluadora1` | `wim2026` | Evaluadora |
| `evaluadora2` | `wim2026` | Evaluadora |
| `direccion` | `wim2026` | Dirección |

## 🧪 Pruebas unitarias

```powershell
.\gradlew.bat testDebugUnitTest
```

| Archivo | Qué prueba | Qué se mockea |
|---|---|---|
| `CandidataRepositoryTest` | CRUD remoto, copia en Room y manejo de errores | `WimApi`, DAO, historial |
| `AuthRepositoryTest` | Login contra el backend | `WimApi` |
| `SesionViewModelTest` | Estados del login | `AuthRepository` |
| `CandidatasViewModelTest` | Carga y refresco de la lista | `CandidataRepository` |
| `CandidataFormViewModelTest` | Formulario, validación y API externa | Repositorios |
| `CandidataValidatorTest` | Reglas del formulario | — |
| `CalculadoraPuntajeTest` | Puntaje ponderado | — |
| `RankingTest` | Orden y desempate del ranking | — |

La cobertura se mide sobre la lógica (`model`, `validation`, `data/repository` y `ui/viewmodel`): en Android Studio, clic derecho en la carpeta `test` → **Run with Coverage**.

## 🔐 APK firmado

- APK: [`release/app-release.apk`](release/app-release.apk)
- Keystore: [`keystore/wim100.jks`](keystore/wim100.jks), alias `wim100`
- Configuración: bloque `signingConfigs` en [`app/build.gradle.kts`](app/build.gradle.kts)

Para generarlo de nuevo: **Build → Generate Signed App Bundle / APK → APK**, con el keystore de arriba, variante `release`.

![APK firmado](docs/capturas/apk-firmado.png)
![Keystore](docs/capturas/keystore.png)

> El `.jks` y sus contraseñas se suben solo porque la evaluación lo exige. En un proyecto real nunca se publican en un repositorio.

---

Proyecto académico con fines educativos · Duoc UC · 2026
