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

## 🔗 Endpoints utilizados

Base (producción): `https://tiendamimascotabackends.onrender.com/api/`  
Base (desarrollo/emulador): `http://10.0.2.2:8080/api/`

> Nota: las URLs del backend ya incluyen el sufijo `/api/` — no agregues `/api` otra vez en los endpoints.

### Autenticación
- `POST auth/login` — Login (body: `LoginRequest`)  
- `POST auth/registro` — Registro (body: `RegistroRequest`)  
- `GET auth/verificar` — Verificar token JWT (requiere Authorization: Bearer)
- `GET auth/usuario` — Obtener usuario actual (requiere Authorization)
- `POST auth/logout` — Cerrar sesión

### Productos (públicos)
- `GET productos` — Listar todos los productos (devuelve array directo)
- `GET productos/{id}` — Obtener producto por id
- `GET productos/categoria/{categoria}` — Filtrar por categoría
- `GET productos/buscar?q={termino}` — Buscar productos
- `POST productos/verificar-stock` — Verificar stock para items enviados

### Órdenes (requieren JWT)
- `POST ordenes` — Crear orden (envío JSON con snake_case)
- `GET ordenes/usuario/{usuarioId}` — Obtener órdenes de un usuario
- `GET ordenes/{ordenId}` — Obtener detalle de orden

### Headers recomendados
- Para peticiones JSON: `Content-Type: application/json`, `Accept: application/json`  
- Para peticiones autenticadas: `Authorization: Bearer {token}`

### Consejos de uso
- En emulador Android usar `10.0.2.2` para apuntar al backend en `localhost:8080` del equipo.  
- Si pruebas contra la URL en Render, la primera petición puede tardar 30–50s si el servicio está dormido; aumenta timeout OkHttp a 60s para evitar timeouts en esa primera petición.

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
