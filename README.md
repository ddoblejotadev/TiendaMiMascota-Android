# 🐾 TiendaMiMascota-Android

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF.svg?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.7-4285F4.svg?style=for-the-badge&logo=jetpackcompose)
![Material Design 3](https://img.shields.io/badge/Material%203-M3-4A856F.svg?style=for-the-badge&logo=material-design)

Aplicación móvil Android de e-commerce para una tienda de mascotas, con un panel de administración completo y una sección dedicada a la adopción de animales desde una API externa.

## 👥 Autores

- [Juan Llontop](https://github.com/ddoblejotadev)
- [Yasser Illanes](https://github.com/yasser-duoc)
- **Asistente de IA**: Gemini

## ⚡ Funcionalidades

La aplicación se divide en dos grandes módulos: la tienda para clientes y un back-office para administradores.

### 🛍️ Para Clientes

- **Autenticación Completa**: Registro, inicio de sesión y sesión persistente con `TokenManager`.
- **Catálogo de Productos**: Visualización de productos con imágenes, precios y detalles.
- **Carrito de Compras**: Añadir, modificar y eliminar productos del carrito.
- **Proceso de Compra**: Simulación de checkout con validación de stock y creación de órdenes.
- **Adopción de Mascotas**: Integración con la API de `huachitos.cl` para buscar y ver detalles de animales en adopción.
- **Perfil de Usuario**: Visualización y edición de datos personales y foto de perfil (captura con cámara).

### 👑 Panel de Administrador

- **Dashboard Centralizado**: Una interfaz única para gestionar todos los aspectos de la tienda.
- **Gestión de Productos (CRUD)**: 
  - **C**rear nuevos productos.
  - **R**evisar el catálogo completo.
  - **U**pdate (actualizar) productos existentes.
  - **D**elete (eliminar) productos.
- **Gestión de Órdenes**: 
  - Visualizar todos los pedidos agrupados por usuario.
  - Desplegar los detalles de cada orden.
  - Actualizar el estado de un pedido (Pendiente, En Proceso, Enviado, etc.).
- **Gestión de Usuarios**: 
  - Ver la lista completa de usuarios registrados.
  - Eliminar usuarios de la base de datos con diálogo de confirmación.

## 🛠️ Stack Tecnológico

- **Lenguaje**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) para una UI moderna y declarativa.
- **Diseño**: [Material Design 3](https://m3.material.io/) para una interfaz atractiva y consistente.
- **Arquitectura**: MVVM (Model-View-ViewModel).
- **Asincronía**: Coroutines y `StateFlow` para una gestión de estado reactiva y eficiente.
- **Navegación**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) para la navegación entre pantallas.
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) para las llamadas a la API REST y [OkHttp](https://square.github.io/okhttp/) como cliente HTTP.
- **Persistencia Local**: [Room](https://developer.android.com/training/data-storage/room) para el almacenamiento local de datos de sesión.
- **Carga de Imágenes**: [Coil](https://coil-kt.github.io/coil/) para la carga eficiente de imágenes desde URLs.

## 🔗 Endpoints

La aplicación consume dos APIs distintas:

1.  **Backend Propio (Tienda)**
    - **Base (Producción)**: `https://tiendamimascotabackends.onrender.com/api/`
    - **Base (Desarrollo)**: `http://10.0.2.2:8080/api/` (para emulador)

2.  **API Externa (Adopción)**
    - **Base**: `https://huachitos.cl/api/`

> **Nota**: Las URLs base ya incluyen `/api/` cuando corresponde. No es necesario añadirlo de nuevo en los endpoints.

## 🏗️ Arquitectura MVVM

La aplicación sigue un patrón MVVM bien definido para separar responsabilidades y facilitar el mantenimiento.

```
+-----------------------+
|      📱 VIEW (UI)     |
| (Composable Screens)  |
+-----------+-----------+
            |
            v
+-----------+-----------+
|   🎯 VIEWMODEL       |
| (Lógica de UI y Estado) |
+-----------+-----------+
            |
            v
+-----------+-----------+
|   🔄 REPOSITORY      |
| (Fuente de Datos Única) |
+-----------+-----------+
            |           
     +------+------+
     |             |
     v             v
+----+-----+   +----+-----+
| 🌐 API   |   | 💾 ROOM   |
| (Retrofit) |   | (Local)  |
+----------+   +----------+
```

## 🚀 Instalación y Requisitos

1.  Clonar el repositorio: `git clone <URL>`
2.  Abrir el proyecto con Android Studio (Hedgehog o superior).
3.  Sincronizar el proyecto con Gradle.
4.  Ejecutar en un emulador o dispositivo físico.

**Requisitos Mínimos:**
- **minSdk**: 24 (Android 7.0 Nougat)
- **targetSdk**: 34 (Android 14)

## 🔑 Credenciales de Prueba

- **Administrador**:
  - **Usuario**: `admin`
  - **Contraseña**: `admin`
- **Usuario Cliente**:
  - Se puede registrar cualquier usuario desde la pantalla de registro de la aplicación.

## 📂 Estructura del Proyecto

```
com.example.mimascota
├── client/          # Configuración de Retrofit (OkHttp, Interceptores)
├── model/           # Clases de datos (DTOs) y modelos de la UI
├── repository/      # Repositorios que gestionan las fuentes de datos
├── service/         # Interfaces de Retrofit para las APIs
├── ui/              # Clases y Activities relacionadas con la UI
├── util/            # Clases de utilidad (TokenManager, ConnectionTester)
├── view/            # Pantallas de Jetpack Compose
└── viewModel/       # ViewModels con la lógica de negocio de cada pantalla
```

---

⭐ **Desarrollado con Kotlin y Jetpack Compose** ⭐
