# Primer commit — cómo subirlo a GitHub

Repositorio del equipo: https://github.com/Angellssj/APLICACION_DESARROLLO_MOVIL

## Opción recomendada: conservar la historia existente de GitHub

1. Instala Git, inicia sesión y abre **Git Bash** o Terminal.
2. Clona el repositorio del equipo:

```bash
git clone https://github.com/Angellssj/APLICACION_DESARROLLO_MOVIL.git
cd APLICACION_DESARROLLO_MOVIL
```

3. Descomprime el ZIP entregado. **Copia el contenido de la carpeta `WIM100_PRIMER_COMMIT_PROYECTO` dentro de la carpeta clonada** (archivos `app`, `gradle`, `settings.gradle.kts`, etc.). Confirma la sustitución de los archivos anteriores; conserva la carpeta `.git` oculta del repositorio clonado. No crees otra carpeta del proyecto dentro del repositorio.
4. Abre y ejecuta el proyecto en Android Studio. Si aún no compila, corrige el problema antes de publicarlo como funcional.
5. Revisa qué cambió:

```bash
git status
git diff --stat
```

6. Realiza el primer commit propio de esta base:

```bash
git add .
git commit -m "feat: iniciar WIM 100 con acceso y registro local"
git push origin main
```

Si la rama por defecto es `master` u otra, reemplaza `main` por el nombre real (`git branch --show-current`). **No uses `git init`, `push --force` ni borres el historial del repositorio existente.**

Si `git push` es rechazado porque otra persona subió cambios:

```bash
git pull --rebase origin main
# Resolver conflictos si los hay; ejecutar las pruebas y después:
git push origin main
```

## Cómo organizar los siguientes commits

Un integrante = sus propios cambios, probados y ligados a su tarjeta Trello. Ejemplos futuros:

- `feat(HU-02): mejorar navegación y pantalla de inicio` — Ángel
- `feat(HU-06): agregar validación de teléfono` — Roselín
- `feat(HU-09): migrar almacenamiento a Room` — Andrés

No crear commits ficticios atribuidos a compañeros. La historia del repo debe mostrar contribuciones reales.

## Cosas que NO se suben

`local.properties`, `.gradle/`, `.idea/workspace.xml`, `build/`, contraseñas, datos reales de candidatas y archivos generados.
