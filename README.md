# 📱 WIM 100 – Aplicación de Evaluación de Candidatas

## 📌 Descripción del proyecto

WIM 100 es un proyecto académico desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** de Duoc UC.

La aplicación busca facilitar el registro, evaluación y seguimiento de candidatas de un programa de selección, mediante una interfaz móvil sencilla, organizada e intuitiva.

El objetivo es aplicar los conocimientos adquiridos durante el semestre, priorizando la funcionalidad, la facilidad de uso y la organización del código.

## 🎯 Objetivo general

Desarrollar una aplicación móvil Android que permita gestionar candidaturas y evaluaciones, utilizando Kotlin, Jetpack Compose y almacenamiento local.

## 🚀 Funcionalidades del proyecto

- Registro y consulta de candidatas.
- Formularios con validación de datos.
- Navegación entre las diferentes pantallas.
- Evaluación de candidatas mediante puntajes.
- Visualización de resultados.
- Gestión de información mediante almacenamiento local.
- Perfiles de Evaluadora y Dirección.
- Integración de recursos nativos del dispositivo.
- Interfaz diseñada con Material Design 3.

**Estado:** proyecto académico en desarrollo y validación funcional.

## 🛠️ Tecnologías del proyecto

| Tecnología | Utilidad |
|---|---|
| Kotlin | Lenguaje de programación |
| Jetpack Compose | Desarrollo de interfaces |
| Material Design 3 | Diseño visual |
| MVVM | Organización de la aplicación |
| Room / SQLite | Almacenamiento local |
| Gradle | Gestión de dependencias |
| Git y GitHub | Control de versiones |
| Trello | Organización de tareas |

## 🏗️ Arquitectura

El proyecto está organizado siguiendo los principios del patrón MVVM.

- **Model:** representa los datos de la aplicación.
- **View:** contiene las pantallas e interfaces.
- **ViewModel:** administra los estados y la lógica de presentación.
- **Repository:** organiza el acceso a los datos.

Esta separación facilita la comprensión y el mantenimiento del código.

## 💻 Cómo ejecutar el proyecto

1. Descargar o clonar este repositorio.
2. Abrir la carpeta del proyecto Android en Android Studio o en IntelliJ IDEA con soporte Android configurado.
3. Configurar el Android SDK.
4. Sincronizar las dependencias mediante Gradle.
5. Seleccionar un emulador Android o conectar un dispositivo físico.
6. Ejecutar el módulo `app`.

## 👥 Integrantes

**Equipo de desarrollo:**

- Ángel González
- Andrés Albornoz
- Roselín Verdu

**Institución:** Duoc UC  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Código:** DSY1105

## 📚 Contexto académico

El proyecto forma parte de las evaluaciones parciales del semestre y constituye un avance para la Evaluación Final Transversal (EFT).

Se busca demostrar conocimientos en diseño visual, programación Kotlin, navegación, gestión de estados, persistencia local y recursos nativos.

## 📈 Mejoras futuras

- Fortalecer las pruebas funcionales.
- Mejorar las animaciones y la experiencia visual.
- Optimizar las validaciones.
- Incorporar las observaciones recibidas durante la defensa.

---

**Proyecto académico desarrollado con fines educativos – Duoc UC, 2026.**

---

## 🔌 Conexión con backend (EP4)

La app es **híbrida**: postulantes y login viven en el microservicio `wim100-backend` (Spring Boot + MySQL);
evaluaciones, criterios e historial siguen en Room, que además guarda una copia local de los postulantes.

- Backend: https://github.com/Andres-albornoz/wim100-backend
- API externa: [randomuser.me](https://randomuser.me) (solo genera un nombre ficticio en el formulario).

### Endpoints consumidos (`ApiConfig.BASE_URL = http://10.0.2.2:8080/api/`)
| Método | Ruta | Uso |
|---|---|---|
| GET | /postulantes | Listar |
| POST | /postulantes | Registrar |
| PUT | /postulantes/{id} | Editar |
| PATCH | /postulantes/{id}/estado | Estado tras evaluar / resolver |
| DELETE | /postulantes/{id} | Eliminar |
| POST | /auth/login | Iniciar sesión |

### Cómo ejecutar (backend primero)
1. Levantar MySQL y el backend (`./mvnw spring-boot:run` en `wim100-backend`).
2. Abrir esta app en Android Studio y ejecutarla en el emulador.
3. Ingresar con `evaluadora1` / `wim2026` (rol Evaluadora) o `direccion` / `wim2026` (rol Dirección).
4. En un teléfono físico, cambiar `BASE_URL` por la IP del computador (misma Wi-Fi, puerto 8080 abierto).

### Pruebas unitarias (MockK)
`./gradlew testDebugUnitTest` → 26 pruebas de repositorios, ViewModels, validadores y cálculo de puntaje.

### APK firmado
`release/app-release.apk`, firmado con `keystore/wim100.jks` (alias `wim100`) mediante `signingConfigs` en `app/build.gradle.kts`.
El `.jks` y sus contraseñas se incluyen solo porque la evaluación lo exige; en un proyecto real nunca se suben a un repositorio.
