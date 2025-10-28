# 🐾 TiendaMiMascota

Aplicación móvil Android de e-commerce para productos de mascotas.

## 👥 Autores

- [Juan Llontop](https://github.com/ddoblejotadev)
- [Yasser Illanes](https://github.com/yasser-duoc)

## 📱 Descripción

Aplicación de tienda online para productos de mascotas desarrollada con Kotlin y Jetpack Compose. Permite a los usuarios registrarse, explorar productos, gestionar un carrito de compras y realizar compras simuladas.

## 🛠️ Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI declarativa
- **Material Design 3** - Diseño visual
- **Room SQLite** - Persistencia local
- **MVVM** - Arquitectura
- **Coroutines** - Programación asíncrona
- **Navigation Compose** - Navegación

## ⚡ Funcionalidades

- 🔐 Registro y login de usuarios
- 🛒 Catálogo de productos por categorías
- 🛍️ Carrito de compras
- 💾 Persistencia con Room SQLite
- 📸 Captura de foto de perfil con cámara
- ✅ Validaciones en tiempo real
- 🎨 Interfaz Material Design 3

## 🏗️ Arquitectura

Implementa el patrón **MVVM** (Model-View-ViewModel):

```
📱 View (UI)
    ↓
🎯 ViewModel (Lógica)
    ↓
🔄 Repository (Datos)
    ↓
🗄️ Room Database (Persistencia)
```

## 🚀 Instalación

1. Clonar el repositorio
2. Abrir con Android Studio
3. Sync Gradle
4. Ejecutar en emulador o dispositivo

**Requisitos:**
- Android Studio Hedgehog o superior
- minSdk: 24 (Android 7.0)
- targetSdk: 34 (Android 14)

## 🔑 Credenciales

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin`

**Usuario normal:**
- Registrar desde la app

## 📦 Dependencias Principales

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Room Database
implementation("androidx.room:room-ktx")
kapt("androidx.room:room-compiler")

// Navigation
implementation("androidx.navigation:navigation-compose")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
```

## 📂 Estructura del Proyecto

```
com.example.mimascota/
├── View/              # Pantallas UI
├── ViewModel/         # Lógica de negocio
├── Model/             # Modelos de datos
├── data/
│   ├── entity/        # Entidades Room
│   ├── dao/           # Data Access Objects
│   └── database/      # Configuración BD
└── repository/        # Acceso a datos
```

## 🧪 Características Implementadas

- ✅ Autenticación con Room
- ✅ Validación de formularios
- ✅ Persistencia de datos
- ✅ Gestión de estado con StateFlow
- ✅ Recursos nativos (Cámara)
- ✅ Navegación entre pantallas
- ✅ Carrito de compras
- ✅ Material Design 3

## 📝 Notas

- La aplicación usa Room SQLite para almacenamiento local
- Los datos persisten entre sesiones
- Implementa permisos en runtime para la cámara
- Sigue principios de Clean Architecture

## 📄 Licencia

Proyecto académico - DSY1105 Desarrollo de Aplicaciones Móviles

---

⭐ **Desarrollado con Kotlin y Jetpack Compose**

