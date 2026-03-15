# ZenithPortfolio

A cryptocurrency portfolio tracker built with **Kotlin Multiplatform (KMP)**, featuring native UI for both **Android** (Jetpack Compose) and **iOS** (SwiftUI).

---

## Table of Contents / Tabla de Contenidos

- [English](#english)
- [Spanish / Espanol](#espanol)

---

<a id="english"></a>

# English

## Overview

ZenithPortfolio is a cross-platform cryptocurrency tracker that lets you monitor real-time crypto prices, search coins, manage favorites, and view price history charts. It uses a shared Kotlin module for business logic and networking, with platform-native UI on each target.

## Screenshots

> _Screenshots coming soon._

## Features

- Real-time crypto prices from CoinGecko API
- Search cryptocurrencies
- Favorites with local persistence (SQLDelight)
- Price history charts (24h, 7d, 30d, 90d)
- Pull-to-refresh
- Dark theme
- MVI architecture with shared business logic

## Architecture

```
ZenithPortfolio/
├── shared/                  # Kotlin Multiplatform shared module
│   └── src/
│       ├── commonMain/      # Business logic, repositories, MVI, networking
│       ├── androidMain/     # Android-specific implementations (OkHttp, SQLDelight Android driver)
│       ├── iosMain/         # iOS-specific implementations (Darwin client, Native driver)
│       └── commonTest/      # Shared unit tests
├── composeApp/              # Android app (Jetpack Compose UI)
│   └── src/
│       ├── commonMain/      # Compose Multiplatform UI code
│       └── androidMain/     # Android-specific Compose code
└── iosApp/                  # iOS app (SwiftUI)
    ├── iosApp/              # Swift source files
    ├── iosApp.xcodeproj/    # Xcode project
    └── Configuration/       # Build configuration (Config.xcconfig)
```

**Pattern:** MVI (Model-View-Intent) in the shared module. ViewModels expose `StateFlow` consumed by platform UI.

## Tech Stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin | 2.3.0 |
| Build tool | Gradle | 8.14.3 |
| Android Gradle Plugin | AGP | 8.13.2 |
| Compose Multiplatform | JetBrains Compose | 1.9.3 |
| Networking | Ktor | 3.3.3 |
| Serialization | Kotlinx Serialization | 1.9.0 |
| Local storage | SQLDelight | 2.0.2 |
| Dependency Injection | Koin | 4.1.1 |
| Navigation | Voyager | 1.1.0-beta03 |
| Image loading | Coil | 3.0.4 |
| Charts (Android) | Vico | 2.0.0-beta.1 |
| Coroutines | Kotlinx Coroutines | 1.10.2 |
| Date/Time | Kotlinx Datetime | 0.6.1 |
| Android compileSdk | | 36 |
| Android minSdk | | 24 |
| Android targetSdk | | 36 |

## Requirements

### Common

| Tool | Minimum Version | Notes |
|---|---|---|
| **JDK** | 17 | Required for Kotlin 2.3 and AGP 8.13 |
| **Kotlin** | 2.3.0 | Bundled with the Gradle build |
| **Gradle** | 8.14.3 | Bundled via the Gradle Wrapper (`./gradlew`) |

### Android Development

| Tool | Minimum Version | Notes |
|---|---|---|
| **Android Studio** | Ladybug (2024.2) or newer | Must support AGP 8.13 and Compose Multiplatform |
| **Android SDK** | API 36 (compile/target) | Install via SDK Manager |
| **Android SDK** | API 24 (minimum) | App runs on Android 7.0+ |
| **KMP plugin** | Install from Android Studio Marketplace | "Kotlin Multiplatform" plugin |

### iOS Development

| Tool | Minimum Version | Notes |
|---|---|---|
| **macOS** | 14.0 (Sonoma) or newer | Required for modern Xcode |
| **Xcode** | 15.0 or newer | For Swift, SwiftUI, and iOS SDK |
| **iOS Deployment Target** | Check `Config.xcconfig` | Typically iOS 16.0+ |
| **CocoaPods** | Not required | This project uses direct framework integration, not CocoaPods |

> **Note:** iOS can only be built on macOS. The shared framework is compiled as a static XCFramework directly from the Gradle build -- no CocoaPods or SPM needed.

## Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/Israel1907/ZenithPortfolio.git
cd ZenithPortfolio
```

### 2. Open in Android Studio (Android)

1. Open Android Studio.
2. Select **File > Open** and navigate to the `ZenithPortfolio` root folder.
3. Wait for Gradle sync to complete. This may take several minutes on the first run as it downloads all dependencies.
4. If prompted, install any missing SDK components via the SDK Manager.
5. Make sure you have the **Kotlin Multiplatform** plugin installed (Settings > Plugins > Marketplace > search "Kotlin Multiplatform").

### 3. Run on Android

1. Connect a physical Android device (USB debugging enabled) or start an Android emulator (API 24+).
2. In Android Studio, select the **composeApp** run configuration from the dropdown.
3. Select your target device.
4. Click the green **Run** button (or press `Shift + F10`).

Alternatively, from the terminal:

```bash
./gradlew :composeApp:installDebug
```

### 4. Run on iOS

> **Prerequisite:** You must be on macOS with Xcode installed.

#### Option A: From Xcode (recommended)

1. First, build the shared framework from the terminal:

   ```bash
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```

   (Use `linkDebugFrameworkIosArm64` for a physical device.)

2. Open the Xcode project:

   ```bash
   open iosApp/iosApp.xcodeproj
   ```

3. In Xcode, select a simulator (e.g., iPhone 16) or a connected physical device.
4. Click the **Run** button (or press `Cmd + R`).

#### Option B: From Android Studio with KMP plugin

1. If you have the KMP plugin installed and configured, you may see an **iosApp** run configuration in Android Studio.
2. Select it and choose a simulator.
3. Click **Run**.

> **Tip:** The first iOS build takes significantly longer because it compiles the Kotlin shared framework to native code.

## Build Commands Reference

| Command | Description |
|---|---|
| `./gradlew :composeApp:assembleDebug` | Build Android debug APK |
| `./gradlew :composeApp:installDebug` | Build and install on connected Android device |
| `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` | Build shared framework for iOS Simulator (Apple Silicon) |
| `./gradlew :shared:linkDebugFrameworkIosArm64` | Build shared framework for iOS device |
| `./gradlew :shared:testDebugUnitTest` | Run shared module unit tests (Android) |
| `./gradlew clean` | Clean all build outputs |

## Troubleshooting

### Gradle Sync Fails

- **"Could not resolve..."**: Make sure you have internet access. Check that `google()` and `mavenCentral()` repositories are reachable.
- **JDK version mismatch**: Ensure Android Studio is using JDK 17+. Go to **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK** and select JDK 17.
- **Out of memory**: The project sets `-Xmx4096M` in `gradle.properties`. If you still get OOM errors, increase this value or close other applications.

### Android Build Issues

- **"SDK not found"**: Open SDK Manager and install API 36.
- **Compose preview not working**: Make sure you have the latest Compose Multiplatform plugin and that your Android Studio version is compatible.

### iOS Build Issues

- **"Framework not found Shared"**: You need to build the shared framework first. Run:
  ```bash
  ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
  ```
- **Xcode build error after Kotlin update**: Clean the build folder in Xcode (**Product > Clean Build Folder**, or `Shift + Cmd + K`), then rebuild.
- **Simulator architecture mismatch**: If you are on an Apple Silicon Mac, use `IosSimulatorArm64`. If on Intel, use `IosX64` (you may need to add this target in `shared/build.gradle.kts`).
- **"No such module 'Shared'"**: Make sure the framework search paths in Xcode point to the correct build output directory.

### General KMP Issues

- **"Unresolved reference" in shared code**: Run **File > Sync Project with Gradle Files** in Android Studio.
- **iOS changes not reflected**: The shared framework is not rebuilt automatically by Xcode. Rebuild it via Gradle before running in Xcode.
- **Slow first build**: This is normal. KMP compiles Kotlin to JVM bytecode (Android) and native code (iOS). Subsequent builds use Gradle caching and are faster.

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

<a id="espanol"></a>

# Espanol

## Descripcion General

ZenithPortfolio es un rastreador de criptomonedas multiplataforma que permite monitorear precios en tiempo real, buscar monedas, gestionar favoritos y ver graficos de historial de precios. Utiliza un modulo compartido de Kotlin para la logica de negocio y la red, con UI nativa en cada plataforma.

## Capturas de Pantalla

> _Capturas de pantalla proximamente._

## Caracteristicas

- Precios de criptomonedas en tiempo real desde la API de CoinGecko
- Busqueda de criptomonedas
- Favoritos con persistencia local (SQLDelight)
- Graficos de historial de precios (24h, 7d, 30d, 90d)
- Deslizar para actualizar (pull-to-refresh)
- Tema oscuro
- Arquitectura MVI con logica de negocio compartida

## Arquitectura

```
ZenithPortfolio/
├── shared/                  # Modulo compartido de Kotlin Multiplatform
│   └── src/
│       ├── commonMain/      # Logica de negocio, repositorios, MVI, networking
│       ├── androidMain/     # Implementaciones especificas de Android (OkHttp, driver SQLDelight Android)
│       ├── iosMain/         # Implementaciones especificas de iOS (cliente Darwin, driver nativo)
│       └── commonTest/      # Tests unitarios compartidos
├── composeApp/              # App Android (UI con Jetpack Compose)
│   └── src/
│       ├── commonMain/      # Codigo UI con Compose Multiplatform
│       └── androidMain/     # Codigo Compose especifico de Android
└── iosApp/                  # App iOS (SwiftUI)
    ├── iosApp/              # Archivos fuente Swift
    ├── iosApp.xcodeproj/    # Proyecto Xcode
    └── Configuration/       # Configuracion de build (Config.xcconfig)
```

**Patron:** MVI (Model-View-Intent) en el modulo compartido. Los ViewModels exponen `StateFlow` consumido por la UI de cada plataforma.

## Stack Tecnologico

| Categoria | Biblioteca | Version |
|---|---|---|
| Lenguaje | Kotlin | 2.3.0 |
| Herramienta de build | Gradle | 8.14.3 |
| Android Gradle Plugin | AGP | 8.13.2 |
| Compose Multiplatform | JetBrains Compose | 1.9.3 |
| Red | Ktor | 3.3.3 |
| Serializacion | Kotlinx Serialization | 1.9.0 |
| Almacenamiento local | SQLDelight | 2.0.2 |
| Inyeccion de dependencias | Koin | 4.1.1 |
| Navegacion | Voyager | 1.1.0-beta03 |
| Carga de imagenes | Coil | 3.0.4 |
| Graficos (Android) | Vico | 2.0.0-beta.1 |
| Corrutinas | Kotlinx Coroutines | 1.10.2 |
| Fecha/Hora | Kotlinx Datetime | 0.6.1 |
| Android compileSdk | | 36 |
| Android minSdk | | 24 |
| Android targetSdk | | 36 |

## Requisitos

### Comunes

| Herramienta | Version Minima | Notas |
|---|---|---|
| **JDK** | 17 | Requerido para Kotlin 2.3 y AGP 8.13 |
| **Kotlin** | 2.3.0 | Incluido con el build de Gradle |
| **Gradle** | 8.14.3 | Incluido mediante el Gradle Wrapper (`./gradlew`) |

### Desarrollo Android

| Herramienta | Version Minima | Notas |
|---|---|---|
| **Android Studio** | Ladybug (2024.2) o superior | Debe soportar AGP 8.13 y Compose Multiplatform |
| **Android SDK** | API 36 (compilacion/objetivo) | Instalar via SDK Manager |
| **Android SDK** | API 24 (minimo) | La app funciona en Android 7.0+ |
| **Plugin KMP** | Instalar desde el Marketplace de Android Studio | Plugin "Kotlin Multiplatform" |

### Desarrollo iOS

| Herramienta | Version Minima | Notas |
|---|---|---|
| **macOS** | 14.0 (Sonoma) o superior | Requerido para versiones modernas de Xcode |
| **Xcode** | 15.0 o superior | Para Swift, SwiftUI y el SDK de iOS |
| **iOS Deployment Target** | Verificar `Config.xcconfig` | Tipicamente iOS 16.0+ |
| **CocoaPods** | No requerido | Este proyecto usa integracion directa de framework, no CocoaPods |

> **Nota:** iOS solo puede compilarse en macOS. El framework compartido se compila como un XCFramework estatico directamente desde el build de Gradle -- no se necesita CocoaPods ni SPM.

## Configuracion e Instalacion

### 1. Clonar el repositorio

```bash
git clone https://github.com/Israel1907/ZenithPortfolio.git
cd ZenithPortfolio
```

### 2. Abrir en Android Studio (Android)

1. Abre Android Studio.
2. Selecciona **File > Open** y navega a la carpeta raiz `ZenithPortfolio`.
3. Espera a que se complete la sincronizacion de Gradle. Esto puede tardar varios minutos la primera vez mientras se descargan todas las dependencias.
4. Si se te solicita, instala los componentes del SDK faltantes via el SDK Manager.
5. Asegurate de tener instalado el plugin **Kotlin Multiplatform** (Settings > Plugins > Marketplace > buscar "Kotlin Multiplatform").

### 3. Ejecutar en Android

1. Conecta un dispositivo Android fisico (depuracion USB activada) o inicia un emulador Android (API 24+).
2. En Android Studio, selecciona la configuracion de ejecucion **composeApp** del menu desplegable.
3. Selecciona tu dispositivo objetivo.
4. Haz clic en el boton verde **Run** (o presiona `Shift + F10`).

Alternativamente, desde la terminal:

```bash
./gradlew :composeApp:installDebug
```

### 4. Ejecutar en iOS

> **Prerequisito:** Debes estar en macOS con Xcode instalado.

#### Opcion A: Desde Xcode (recomendado)

1. Primero, compila el framework compartido desde la terminal:

   ```bash
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```

   (Usa `linkDebugFrameworkIosArm64` para un dispositivo fisico.)

2. Abre el proyecto de Xcode:

   ```bash
   open iosApp/iosApp.xcodeproj
   ```

3. En Xcode, selecciona un simulador (ej., iPhone 16) o un dispositivo fisico conectado.
4. Haz clic en el boton **Run** (o presiona `Cmd + R`).

#### Opcion B: Desde Android Studio con el plugin KMP

1. Si tienes el plugin KMP instalado y configurado, puede que veas una configuracion de ejecucion **iosApp** en Android Studio.
2. Seleccionala y elige un simulador.
3. Haz clic en **Run**.

> **Consejo:** La primera compilacion de iOS tarda considerablemente mas porque compila el framework compartido de Kotlin a codigo nativo.

## Referencia de Comandos de Build

| Comando | Descripcion |
|---|---|
| `./gradlew :composeApp:assembleDebug` | Compilar APK de depuracion para Android |
| `./gradlew :composeApp:installDebug` | Compilar e instalar en dispositivo Android conectado |
| `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` | Compilar framework compartido para simulador iOS (Apple Silicon) |
| `./gradlew :shared:linkDebugFrameworkIosArm64` | Compilar framework compartido para dispositivo iOS |
| `./gradlew :shared:testDebugUnitTest` | Ejecutar tests unitarios del modulo compartido (Android) |
| `./gradlew clean` | Limpiar todos los archivos de compilacion |

## Solucion de Problemas

### La sincronizacion de Gradle falla

- **"Could not resolve..."**: Asegurate de tener acceso a internet. Verifica que los repositorios `google()` y `mavenCentral()` sean accesibles.
- **Version de JDK incorrecta**: Asegurate de que Android Studio este usando JDK 17+. Ve a **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK** y selecciona JDK 17.
- **Sin memoria**: El proyecto configura `-Xmx4096M` en `gradle.properties`. Si aun obtienes errores de memoria, aumenta este valor o cierra otras aplicaciones.

### Problemas de compilacion en Android

- **"SDK not found"**: Abre el SDK Manager e instala la API 36.
- **La vista previa de Compose no funciona**: Asegurate de tener el plugin de Compose Multiplatform mas reciente y que tu version de Android Studio sea compatible.

### Problemas de compilacion en iOS

- **"Framework not found Shared"**: Necesitas compilar el framework compartido primero. Ejecuta:
  ```bash
  ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
  ```
- **Error de compilacion en Xcode despues de actualizar Kotlin**: Limpia la carpeta de build en Xcode (**Product > Clean Build Folder**, o `Shift + Cmd + K`), luego recompila.
- **Incompatibilidad de arquitectura del simulador**: Si estas en un Mac con Apple Silicon, usa `IosSimulatorArm64`. Si estas en Intel, usa `IosX64` (puede que necesites agregar este target en `shared/build.gradle.kts`).
- **"No such module 'Shared'"**: Asegurate de que las rutas de busqueda del framework en Xcode apunten al directorio correcto de salida del build.

### Problemas generales de KMP

- **"Unresolved reference" en codigo compartido**: Ejecuta **File > Sync Project with Gradle Files** en Android Studio.
- **Los cambios de iOS no se reflejan**: El framework compartido no se recompila automaticamente desde Xcode. Recompilalo via Gradle antes de ejecutar en Xcode.
- **Primera compilacion lenta**: Esto es normal. KMP compila Kotlin a bytecode JVM (Android) y codigo nativo (iOS). Las compilaciones siguientes usan el cache de Gradle y son mas rapidas.

## Licencia

Este proyecto esta licenciado bajo la **Licencia MIT**. Consulta [LICENSE](LICENSE) para mas detalles.
