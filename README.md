# 🐾 TiendaMiMascota

<div align="center">

![Android](https://img.shields.io/badge/Android-7.0%2B-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5-blue?style=for-the-badge&logo=jetpackcompose)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![License](https://img.shields.io/badge/License-Academic-orange?style=for-the-badge)

### Aplicación móvil Android de e-commerce para productos de mascotas 🐶🐱

*Proyecto académico desarrollado con las últimas tecnologías de Android*

</div>

---

## 👥 Autores

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ddoblejotadev">
        <img src="https://github.com/ddoblejotadev.png" width="100px;" alt="Juan Llontop"/><br />
        <sub><b>Juan Llontop</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/yasser-duoc">
        <img src="https://github.com/yasser-duoc.png" width="100px;" alt="Yasser Illanes"/><br />
        <sub><b>Yasser Illanes</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 📱 Descripción

**TiendaMiMascota** es una aplicación móvil Android de e-commerce especializada en productos para mascotas, desarrollada como proyecto académico para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles**.

### 🎓 Contexto Académico

Este proyecto representa la culminación del aprendizaje en desarrollo móvil nativo para Android, aplicando las mejores prácticas y tecnologías modernas del ecosistema Android. La aplicación demuestra:

- **Implementación de arquitectura limpia**: Aplicación del patrón MVVM para separación de responsabilidades
- **Desarrollo declarativo con Jetpack Compose**: Construcción de interfaces de usuario modernas y reactivas
- **Gestión de estado eficiente**: Uso de StateFlow y LiveData para manejo de estado reactivo
- **Persistencia de datos**: Implementación de Room Database para almacenamiento local
- **Integración con APIs REST**: Consumo de servicios web mediante Retrofit
- **Programación asíncrona**: Uso de Coroutines para operaciones no bloqueantes
- **Material Design 3**: Aplicación de las últimas guías de diseño de Google

### 🎯 Propósito Educativo

La aplicación simula un caso de uso real de e-commerce, permitiendo a los usuarios:
- Registrarse e iniciar sesión en la plataforma
- Explorar un catálogo de productos organizados por categorías
- Buscar productos específicos mediante filtros
- Gestionar un carrito de compras con validación de stock
- Realizar compras simuladas con persistencia de órdenes
- Actualizar su perfil con foto de perfil capturada desde la cámara

El proyecto está diseñado para ser escalable, mantenible y seguir los principios SOLID, sirviendo como base para futuros proyectos profesionales.

## 🛠️ Stack Tecnológico

<table>
  <tr>
    <td><b>Lenguaje</b></td>
    <td>Kotlin 2.1</td>
  </tr>
  <tr>
    <td><b>UI Framework</b></td>
    <td>Jetpack Compose</td>
  </tr>
  <tr>
    <td><b>Diseño</b></td>
    <td>Material Design 3</td>
  </tr>
  <tr>
    <td><b>Arquitectura</b></td>
    <td>MVVM (Model-View-ViewModel)</td>
  </tr>
  <tr>
    <td><b>Base de Datos</b></td>
    <td>Room SQLite</td>
  </tr>
  <tr>
    <td><b>Asincronía</b></td>
    <td>Kotlin Coroutines + Flow</td>
  </tr>
  <tr>
    <td><b>Navegación</b></td>
    <td>Navigation Compose</td>
  </tr>
  <tr>
    <td><b>Networking</b></td>
    <td>Retrofit 2 + OkHttp 4</td>
  </tr>
  <tr>
    <td><b>Serialización</b></td>
    <td>Gson</td>
  </tr>
  <tr>
    <td><b>Imágenes</b></td>
    <td>Coil + Glide</td>
  </tr>
  <tr>
    <td><b>Inyección de Dependencias</b></td>
    <td>Manual (Repository Pattern)</td>
  </tr>
</table>

---

## 🎨 Características Técnicas

### 🏗️ Arquitectura MVVM

La aplicación implementa el patrón **Model-View-ViewModel** con las siguientes capas:

```
┌─────────────────────────────────────────────┐
│           📱 View Layer (UI)                │
│        Jetpack Compose Screens              │
├─────────────────────────────────────────────┤
│        🎯 ViewModel Layer                   │
│    StateFlow + LiveData Management          │
├─────────────────────────────────────────────┤
│        🔄 Repository Layer                  │
│   Data Sources Orchestration                │
├─────────────────────────────────────────────┤
│    🗄️ Data Layer                           │
│ Room Database │ REST API (Retrofit)         │
└─────────────────────────────────────────────┘
```

### 🔐 Sistema de Autenticación

- **JWT Token Management**: Almacenamiento seguro de tokens de autenticación
- **Session Persistence**: Mantención de sesión mediante Room Database
- **Validaciones**: Verificación en tiempo real de campos de formulario
- **Token Verification**: Validación automática de tokens en cada inicio de la app

### 💾 Persistencia de Datos

**Room Database** con las siguientes entidades:
- `Usuario`: Información de usuarios registrados
- `Producto`: Catálogo de productos con stock y precios
- `Carrito`: Items agregados al carrito de compras
- `Orden`: Historial de compras realizadas

**DAO (Data Access Objects)** con operaciones CRUD optimizadas usando Coroutines.

### 🌐 Consumo de API REST

Integración completa con backend mediante:
- **Retrofit 2**: Cliente HTTP type-safe
- **OkHttp Interceptors**: Logging y manejo de headers
- **Gson Converter**: Serialización/deserialización automática
- **Coroutines Support**: Llamadas asíncronas no bloqueantes

### 🎭 UI/UX Moderno

- **Jetpack Compose**: UI 100% declarativa sin XML
- **Material Design 3**: Componentes modernos y adaptables
- **Dark Theme Support**: Soporte para tema oscuro (preparado)
- **Responsive Design**: Adaptación a diferentes tamaños de pantalla
- **Animaciones**: Transiciones suaves entre pantallas

### 📸 Integración con Hardware

- **Cámara**: Captura de foto de perfil usando CameraX
- **Permisos Runtime**: Solicitud dinámica de permisos según Android 6.0+
- **Storage Access**: Almacenamiento de imágenes en caché local

---

## ⚡ Funcionalidades

### 🔐 Autenticación y Usuarios
- ✅ Registro de nuevos usuarios con validaciones
- ✅ Inicio de sesión con credenciales
- ✅ Verificación de token JWT
- ✅ Cierre de sesión seguro
- ✅ Persistencia de sesión entre reinicios
- ✅ Perfil de usuario editable
- ✅ Captura de foto de perfil con cámara

### 🛒 Gestión de Productos
- ✅ Catálogo completo de productos
- ✅ Filtrado por categorías (Alimento, Juguetes, Accesorios, Higiene)
- ✅ Búsqueda de productos por nombre
- ✅ Visualización de detalles de producto
- ✅ Verificación de stock en tiempo real
- ✅ Imágenes de productos con carga lazy

### 🛍️ Carrito de Compras
- ✅ Agregar/eliminar productos al carrito
- ✅ Modificar cantidades de productos
- ✅ Cálculo automático de totales
- ✅ Validación de stock antes de compra
- ✅ Persistencia del carrito localmente
- ✅ Vista previa del carrito

### 💳 Sistema de Órdenes
- ✅ Creación de órdenes de compra
- ✅ Historial de órdenes del usuario
- ✅ Detalle de cada orden realizada
- ✅ Persistencia de órdenes en Room Database
- ✅ Sincronización con backend

### 🎨 Interfaz de Usuario
- ✅ Diseño Material Design 3
- ✅ Navegación fluida entre pantallas
- ✅ Feedback visual en todas las acciones
- ✅ Estados de carga y error
- ✅ Validaciones en tiempo real
- ✅ Animaciones y transiciones suaves

---

## 🔗 Documentación de la API

### 🌐 URLs Base

| Entorno | URL Base | Uso |
|---------|----------|-----|
| **Producción** | `https://tiendamimascotabackends.onrender.com/api/` | Build Release |
| **Desarrollo** | `http://10.0.2.2:8080/api/` | Emulador Android |
| **Desarrollo Local** | `http://localhost:8080/api/` | Dispositivo físico en misma red |

> ⚠️ **Nota importante**: Las URLs del backend ya incluyen el sufijo `/api/`. No agregues `/api` nuevamente en los endpoints.

---

### 🔐 Autenticación

#### POST `/auth/login`
Iniciar sesión con credenciales de usuario.

**Request:**
```json
{
  "username": "usuario123",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "username": "usuario123",
    "email": "usuario@example.com",
    "nombre": "Usuario",
    "apellido": "Ejemplo"
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "error": "Credenciales inválidas"
}
```

---

#### POST `/auth/registro`
Registrar un nuevo usuario en la plataforma.

**Request:**
```json
{
  "username": "nuevouser",
  "password": "password123",
  "email": "nuevo@example.com",
  "nombre": "Nuevo",
  "apellido": "Usuario",
  "telefono": "+56912345678"
}
```

**Response (201 Created):**
```json
{
  "message": "Usuario registrado exitosamente",
  "usuario": {
    "id": 2,
    "username": "nuevouser",
    "email": "nuevo@example.com"
  }
}
```

**Response (400 Bad Request):**
```json
{
  "error": "El username ya existe"
}
```

---

#### GET `/auth/verificar`
Verificar validez del token JWT.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "valid": true,
  "usuario_id": 1
}
```

---

#### GET `/auth/usuario`
Obtener información del usuario autenticado.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "usuario123",
  "email": "usuario@example.com",
  "nombre": "Usuario",
  "apellido": "Ejemplo",
  "telefono": "+56912345678"
}
```

---

### 🛒 Productos (Públicos)

#### GET `/productos`
Listar todos los productos disponibles.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Alimento Premium Perro Adulto",
    "descripcion": "Alimento balanceado para perros adultos",
    "precio": 25990,
    "stock": 50,
    "categoria": "alimento",
    "imagen_url": "https://example.com/imagen1.jpg"
  },
  {
    "id": 2,
    "nombre": "Pelota de Goma",
    "descripcion": "Pelota resistente para perros",
    "precio": 5990,
    "stock": 100,
    "categoria": "juguetes",
    "imagen_url": "https://example.com/imagen2.jpg"
  }
]
```

---

#### GET `/productos/{id}`
Obtener detalles de un producto específico.

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Alimento Premium Perro Adulto",
  "descripcion": "Alimento balanceado para perros adultos de todas las razas",
  "precio": 25990,
  "stock": 50,
  "categoria": "alimento",
  "imagen_url": "https://example.com/imagen1.jpg",
  "peso": "15kg",
  "marca": "Premium Pet"
}
```

---

#### GET `/productos/categoria/{categoria}`
Filtrar productos por categoría.

**Categorías disponibles:** `alimento`, `juguetes`, `accesorios`, `higiene`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Alimento Premium Perro Adulto",
    "precio": 25990,
    "stock": 50,
    "categoria": "alimento"
  }
]
```

---

#### GET `/productos/buscar?q={termino}`
Buscar productos por término.

**Ejemplo:** `/productos/buscar?q=pelota`

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "nombre": "Pelota de Goma",
    "precio": 5990,
    "stock": 100
  }
]
```

---

#### POST `/productos/verificar-stock`
Verificar disponibilidad de stock para múltiples productos.

**Request:**
```json
{
  "items": [
    { "producto_id": 1, "cantidad": 2 },
    { "producto_id": 2, "cantidad": 1 }
  ]
}
```

**Response (200 OK):**
```json
{
  "disponible": true,
  "items": [
    { "producto_id": 1, "disponible": true, "stock_actual": 50 },
    { "producto_id": 2, "disponible": true, "stock_actual": 100 }
  ]
}
```

---

### 💳 Órdenes (Requieren Autenticación)

#### POST `/ordenes`
Crear una nueva orden de compra.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request (snake_case):**
```json
{
  "usuario_id": 1,
  "items": [
    {
      "producto_id": 1,
      "cantidad": 2,
      "precio_unitario": 25990
    }
  ],
  "total": 51980,
  "direccion_envio": "Av. Principal 123, Santiago",
  "metodo_pago": "tarjeta"
}
```

**Response (201 Created):**
```json
{
  "orden_id": 1,
  "estado": "pendiente",
  "fecha_creacion": "2025-12-17T15:30:00Z",
  "total": 51980
}
```

---

#### GET `/ordenes/usuario/{usuarioId}`
Obtener historial de órdenes de un usuario.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "orden_id": 1,
    "fecha_creacion": "2025-12-17T15:30:00Z",
    "total": 51980,
    "estado": "completada",
    "items_count": 2
  }
]
```

---

#### GET `/ordenes/{ordenId}`
Obtener detalle completo de una orden.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "orden_id": 1,
  "usuario_id": 1,
  "fecha_creacion": "2025-12-17T15:30:00Z",
  "estado": "completada",
  "total": 51980,
  "direccion_envio": "Av. Principal 123, Santiago",
  "metodo_pago": "tarjeta",
  "items": [
    {
      "producto_id": 1,
      "nombre_producto": "Alimento Premium Perro Adulto",
      "cantidad": 2,
      "precio_unitario": 25990,
      "subtotal": 51980
    }
  ]
}
```

---

### 📋 Headers Recomendados

| Header | Valor | Cuándo usar |
|--------|-------|-------------|
| `Content-Type` | `application/json` | Todas las peticiones POST/PUT |
| `Accept` | `application/json` | Todas las peticiones |
| `Authorization` | `Bearer {token}` | Endpoints autenticados |

---

### 💡 Tips de Integración

#### 🔧 Configuración de OkHttp

```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()
```

#### 📱 Uso en Emulador Android

Para conectar desde el emulador Android al backend local:
- Usar `10.0.2.2` en lugar de `localhost`
- Ejemplo: `http://10.0.2.2:8080/api/`

#### ⏱️ Manejo de Cold Starts (Render)

Si usas Render para el backend:
- La primera petición puede tardar 30-50 segundos si el servicio está dormido
- Aumentar timeout de OkHttp a 60 segundos
- Implementar retry logic para la primera petición

#### 🔒 Manejo de Token JWT

```kotlin
// Interceptor para agregar token automáticamente
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider()
        
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        
        return chain.proceed(newRequest)
    }
}
```

---

## 🏗️ Arquitectura del Proyecto

### 📐 Patrón MVVM Implementado

```
┌─────────────────────────────────────────────┐
│         📱 View Layer (UI)                  │
│  Jetpack Compose Screens & Components      │
│  - LoginScreen                              │
│  - ProductListScreen                        │
│  - CartScreen                               │
│  - ProfileScreen                            │
└─────────────┬───────────────────────────────┘
              │ Observes StateFlow/LiveData
              ↓
┌─────────────────────────────────────────────┐
│       🎯 ViewModel Layer                    │
│  Business Logic & State Management          │
│  - AuthViewModel                            │
│  - ProductViewModel                         │
│  - CartViewModel                            │
└─────────────┬───────────────────────────────┘
              │ Calls Repository Methods
              ↓
┌─────────────────────────────────────────────┐
│       🔄 Repository Layer                   │
│  Data Orchestration & Business Rules        │
│  - UserRepository                           │
│  - ProductRepository                        │
│  - OrderRepository                          │
└─────┬───────────────────────┬───────────────┘
      │                       │
      ↓                       ↓
┌─────────────┐      ┌──────────────────┐
│ 🗄️ Room DB  │      │ 🌐 Retrofit API  │
│   Local     │      │   Remote         │
│   - DAO     │      │   - ApiService   │
│   - Entity  │      │   - DTOs         │
└─────────────┘      └──────────────────┘
```

### 🔄 Flujo de Datos

1. **UI (View)** emite eventos de usuario
2. **ViewModel** procesa el evento y actualiza el estado
3. **Repository** coordina fuentes de datos (local/remoto)
4. **Data Sources** proveen/almacenan información
5. **ViewModel** expone el nuevo estado vía StateFlow
6. **UI** reacciona automáticamente a los cambios de estado

---

## 🚀 Instalación y Configuración

### 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

| Software | Versión Mínima | Recomendada |
|----------|---------------|-------------|
| **Android Studio** | Hedgehog (2023.1.1) | Ladybug o superior |
| **JDK** | 17 | 17 |
| **Gradle** | 8.0 | 8.2+ |
| **Android SDK** | API 24 (Android 7.0) | API 36 (Android 14+) |

### 🛠️ Instalación Paso a Paso

#### 1️⃣ Clonar el Repositorio

```bash
git clone https://github.com/ddoblejotadev/TiendaMiMascota-Android.git
cd TiendaMiMascota-Android
```

#### 2️⃣ Abrir con Android Studio

1. Abre Android Studio
2. Selecciona **File → Open**
3. Navega hasta la carpeta del proyecto clonado
4. Selecciona la carpeta raíz del proyecto
5. Haz clic en **OK**

#### 3️⃣ Sincronizar Gradle

Android Studio automáticamente detectará el proyecto y te pedirá sincronizar Gradle:

1. Espera a que aparezca el banner "Gradle files have changed"
2. Haz clic en **Sync Now**
3. Espera a que descargue todas las dependencias (puede tardar varios minutos)

Si no aparece automáticamente:
- **File → Sync Project with Gradle Files**

#### 4️⃣ Configurar Backend (Opcional)

##### Opción A: Usar Backend en Producción (Render)
No requiere configuración adicional. La app está pre-configurada para usar el backend en Render.

##### Opción B: Usar Backend Local
Si tienes el backend corriendo localmente:

1. Abre `app/build.gradle.kts`
2. En el bloque `buildTypes { debug { ... } }` verifica:
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
```
3. Asegúrate de que tu backend esté corriendo en `localhost:8080`

#### 5️⃣ Ejecutar la Aplicación

##### En Emulador:
1. Click en **Tools → Device Manager**
2. Crea un nuevo dispositivo virtual o selecciona uno existente
3. Recomendado: **Pixel 5 o superior con API 34**
4. Click en el botón ▶️ **Run** (o `Shift + F10`)

##### En Dispositivo Físico:
1. Habilita **Opciones de Desarrollador** en tu dispositivo:
   - Ve a **Ajustes → Acerca del teléfono**
   - Toca **Número de compilación** 7 veces
2. Habilita **Depuración USB** en **Opciones de Desarrollador**
3. Conecta tu dispositivo vía USB
4. Autoriza la depuración USB cuando se solicite
5. Selecciona tu dispositivo en Android Studio
6. Click en ▶️ **Run**

#### 6️⃣ Verificar Instalación

Una vez que la app se ejecute:
1. Deberías ver la pantalla de Login
2. Puedes registrarte o usar credenciales de prueba:
   - Usuario: `admin`
   - Contraseña: `admin`

---

### 🔧 Troubleshooting

#### ❌ Error: "SDK location not found"

**Solución:**
Crea un archivo `local.properties` en la raíz del proyecto con:
```properties
sdk.dir=/ruta/a/tu/Android/Sdk
```

En Windows:
```properties
sdk.dir=C\:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk
```

En macOS:
```properties
sdk.dir=/Users/TuUsuario/Library/Android/sdk
```

En Linux:
```properties
sdk.dir=/home/TuUsuario/Android/Sdk
```

---

#### ❌ Error: "Kotlin version mismatch"

**Solución:**
1. **File → Invalidate Caches → Invalidate and Restart**
2. Espera a que Android Studio reinicie
3. Vuelve a sincronizar Gradle

---

#### ❌ Error: "Unable to resolve dependency"

**Solución:**
1. Verifica tu conexión a Internet
2. **File → Settings → Build → Gradle**
3. Marca **Offline mode** y desmárcala
4. Sincroniza nuevamente

---

#### ❌ Error: "Manifest merger failed"

**Solución:**
1. **Build → Clean Project**
2. **Build → Rebuild Project**
3. Si persiste, elimina la carpeta `app/build` manualmente

---

#### ❌ Error de conexión al backend

**Síntomas:**
- Timeout en peticiones
- No carga productos
- Login no funciona

**Soluciones:**

1. **Para emulador + backend local:**
   - Usa `http://10.0.2.2:8080/api/` en lugar de `localhost`
   - Verifica que el backend esté corriendo
   - Verifica el puerto (debe ser 8080)

2. **Para dispositivo físico + backend local:**
   - Conecta el dispositivo a la misma red WiFi que tu PC
   - Usa la IP local de tu PC (ej: `http://192.168.1.10:8080/api/`)
   - Asegúrate de que el firewall permita conexiones en el puerto 8080

3. **Para backend en Render:**
   - La primera petición puede tardar 30-50 segundos (cold start)
   - Espera pacientemente en la pantalla de login
   - Si falla, intenta nuevamente después de 1 minuto

---

#### ❌ Error: "Insufficient memory for the Java Runtime Environment"

**Solución:**
Aumenta la memoria asignada a Gradle:

1. Abre o crea el archivo `gradle.properties` (en la raíz del proyecto)
2. Agrega o modifica:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```
3. Sincroniza Gradle nuevamente

---

#### ❌ App se cierra al abrir la cámara

**Solución:**
1. Verifica permisos de cámara en el dispositivo
2. **Ajustes → Apps → TiendaMiMascota → Permisos → Cámara → Permitir**
3. Si usas emulador, asegúrate de que tenga cámara virtual configurada

---

### 🔍 Logs y Debugging

Para ver logs detallados en Android Studio:

1. Abre **Logcat** (pestaña inferior)
2. Filtra por: `com.example.mimascota`
3. Selecciona nivel: **Debug** o **Verbose**

Para ver logs de red (Retrofit):
```kotlin
// Ya configurado en el proyecto
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

---

### ⚙️ Build Variants

El proyecto soporta 2 build variants:

| Variant | Backend URL | Uso |
|---------|------------|-----|
| **debug** | `http://10.0.2.2:8080/api/` | Desarrollo local |
| **release** | `https://tiendamimascotabackends.onrender.com/api/` | Producción |

Para cambiar:
1. **Build → Select Build Variant**
2. Selecciona `debug` o `release`

---

### 📱 Requisitos del Dispositivo

**Especificaciones mínimas:**
- **Android:** 7.0 Nougat (API 24) o superior
- **RAM:** 2 GB mínimo (4 GB recomendado)
- **Espacio:** 100 MB libres
- **Conexión:** Internet (WiFi o datos móviles)
- **Permisos:** Cámara, Almacenamiento (opcional)

---

## 🗺️ Roadmap

### 🚀 Versión 1.0 (Actual)
- ✅ Sistema de autenticación completo
- ✅ Catálogo de productos con búsqueda
- ✅ Carrito de compras funcional
- ✅ Historial de órdenes
- ✅ Perfil de usuario con foto

### 📋 Versión 1.1 (Próximamente)
- ⏳ **Push Notifications**: Notificaciones de ofertas y órdenes
- ⏳ **Favoritos**: Marcar productos como favoritos
- ⏳ **Filtros avanzados**: Filtrar por precio, marca, valoración
- ⏳ **Ordenamiento**: Ordenar productos por diferentes criterios
- ⏳ **Compartir productos**: Compartir productos con otros usuarios

### 🎯 Versión 1.2 (Planificado)
- 📅 **Sistema de valoraciones**: Calificar productos y dejar reseñas
- 📅 **Wishlist**: Lista de deseos persistente
- 📅 **Historial de búsquedas**: Guardar búsquedas recientes
- 📅 **Modo oscuro**: Tema oscuro completo
- 📅 **Múltiples direcciones**: Gestionar varias direcciones de envío

### 🔮 Versión 2.0 (Futuro)
- 💡 **Chat de soporte**: Chat en tiempo real con soporte
- 💡 **Pagos integrados**: Integración con pasarelas de pago
- 💡 **Seguimiento de pedidos**: Tracking en tiempo real
- 💡 **Ofertas personalizadas**: Recomendaciones basadas en IA
- 💡 **Multi-idioma**: Soporte para inglés y español
- 💡 **Modo offline**: Funcionamiento sin conexión a internet

### 🛠️ Mejoras Técnicas Planeadas
- 🔧 Migrar de KAPT a KSP para procesamiento de anotaciones
- 🔧 Implementar Hilt para inyección de dependencias
- 🔧 Agregar tests unitarios y de integración
- 🔧 Implementar CI/CD con GitHub Actions
- 🔧 Optimización de rendimiento con Baseline Profiles
- 🔧 Implementar WorkManager para sincronización en background

---

## 🤝 Contribución

Aunque este es un proyecto académico, ¡las contribuciones son bienvenidas! Si deseas colaborar:

### 📝 Guías para Contribuir

#### 1️⃣ Fork del Proyecto
```bash
# Haz fork del repositorio en GitHub
# Luego clona tu fork
git clone https://github.com/tu-usuario/TiendaMiMascota-Android.git
cd TiendaMiMascota-Android
```

#### 2️⃣ Crea una Rama
```bash
# Crea una rama para tu feature o fix
git checkout -b feature/nueva-funcionalidad
# o
git checkout -b fix/correccion-bug
```

#### 3️⃣ Realiza tus Cambios
- Sigue las convenciones de código existentes
- Comenta tu código cuando sea necesario
- Escribe commits descriptivos
- Asegúrate de que tu código compile sin errores

#### 4️⃣ Commit y Push
```bash
# Agrega tus cambios
git add .

# Commit con mensaje descriptivo
git commit -m "feat: agregar funcionalidad de favoritos"

# Push a tu fork
git push origin feature/nueva-funcionalidad
```

#### 5️⃣ Abre un Pull Request
- Ve a tu fork en GitHub
- Click en "New Pull Request"
- Describe tus cambios detalladamente
- Espera feedback y aprobación

---

### 🎨 Convenciones de Código

#### Nomenclatura
- **Clases**: PascalCase → `ProductViewModel`
- **Funciones**: camelCase → `fetchProducts()`
- **Variables**: camelCase → `productList`
- **Constantes**: UPPER_SNAKE_CASE → `MAX_RETRY_ATTEMPTS`
- **Composables**: PascalCase → `ProductCard()`

#### Estructura de Archivos
- Un archivo por clase
- Nombre del archivo = Nombre de la clase
- Agrupar por funcionalidad, no por tipo

#### Commits
Seguimos [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Cambios de formato (no afectan lógica)
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Tareas de mantenimiento

**Ejemplos:**
```bash
git commit -m "feat: agregar filtro por precio en productos"
git commit -m "fix: corregir crash al cargar imagen de perfil"
git commit -m "docs: actualizar README con nuevas instrucciones"
```

---

### 🐛 Reportar Bugs

Si encuentras un bug:

1. **Verifica** que no esté ya reportado en [Issues](https://github.com/ddoblejotadev/TiendaMiMascota-Android/issues)
2. **Crea un nuevo Issue** con:
   - Título descriptivo
   - Pasos para reproducir
   - Comportamiento esperado vs actual
   - Screenshots si aplica
   - Versión de Android
   - Modelo de dispositivo/emulador

---

### 💡 Sugerir Funcionalidades

¿Tienes una idea genial? ¡Compártela!

1. Abre un [Issue](https://github.com/ddoblejotadev/TiendaMiMascota-Android/issues)
2. Usa el label `enhancement`
3. Describe:
   - El problema que resolvería
   - Cómo lo imaginas funcionando
   - Mockups o ejemplos si es posible

---

### ✅ Checklist para Pull Requests

Antes de enviar tu PR, asegúrate de:

- [ ] El código compila sin errores ni warnings
- [ ] Has probado tu código en emulador/dispositivo
- [ ] Sigues las convenciones de código del proyecto
- [ ] Has actualizado la documentación si es necesario
- [ ] Tu código no rompe funcionalidades existentes
- [ ] Los commits tienen mensajes descriptivos
- [ ] Has resuelto conflictos de merge si existen

---

## 🔑 Credenciales de Prueba

### 👤 Usuario de Prueba

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin`

**Usuario Normal:**
- Puedes registrar tu propio usuario desde la app
- O usar credenciales de prueba si el backend las proporciona

---

## 📦 Dependencias Principales

### 🎨 UI & Compose
```gradle
// Jetpack Compose BOM - Gestión centralizada de versiones
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3:1.2.1")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose")
```

### 🧭 Navegación
```gradle
implementation("androidx.navigation:navigation-compose:2.7.7")
implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
```

### 🔄 ViewModel & Lifecycle
```gradle
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
```

### 🗄️ Room Database
```gradle
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")
```

### 🌐 Networking
```gradle
// Retrofit - Cliente HTTP
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp - Cliente HTTP subyacente
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// Gson - Serialización JSON
implementation("com.google.code.gson:gson:2.10.1")
```

### 📸 Imágenes
```gradle
// Coil - Image loading para Compose
implementation("io.coil-kt:coil-compose:2.3.0")
implementation("io.coil-kt:coil:2.3.0")

// Glide - Image loading tradicional
implementation("com.github.bumptech.glide:glide:4.16.0")
kapt("com.github.bumptech.glide:compiler:4.16.0")
```

### ⚡ Coroutines
```gradle
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### 🧪 Testing
```gradle
// Unit Testing
testImplementation("junit:junit:4.13.2")
testImplementation("androidx.arch.core:core-testing:2.2.0")
testImplementation("io.mockk:mockk:1.13.5")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// Android Instrumented Testing
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")

// Debug
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

---

## 📂 Estructura del Proyecto

### 🗂️ Organización de Carpetas

```
com.example.mimascota/
├── 📱 view/                    # Capa de Presentación (UI)
│   ├── screens/               # Pantallas Compose
│   │   ├── LoginScreen.kt     # Autenticación
│   │   ├── RegisterScreen.kt  # Registro de usuarios
│   │   ├── HomeScreen.kt      # Pantalla principal
│   │   ├── ProductListScreen.kt # Lista de productos
│   │   ├── ProductDetailScreen.kt # Detalle de producto
│   │   ├── CartScreen.kt      # Carrito de compras
│   │   ├── ProfileScreen.kt   # Perfil de usuario
│   │   └── OrderHistoryScreen.kt # Historial de órdenes
│   └── components/            # Componentes reutilizables
│       ├── ProductCard.kt     # Tarjeta de producto
│       ├── CartItem.kt        # Item del carrito
│       └── LoadingIndicator.kt # Indicador de carga
│
├── 🎯 viewModel/               # Capa de Lógica de Negocio
│   ├── AuthViewModel.kt       # Gestión de autenticación
│   ├── ProductViewModel.kt    # Lógica de productos
│   ├── CartViewModel.kt       # Gestión del carrito
│   ├── OrderViewModel.kt      # Lógica de órdenes
│   └── ProfileViewModel.kt    # Gestión de perfil
│
├── 📦 model/                   # Modelos de Dominio
│   ├── Usuario.kt             # Modelo de usuario
│   ├── Producto.kt            # Modelo de producto
│   ├── Carrito.kt             # Modelo del carrito
│   ├── Orden.kt               # Modelo de orden
│   └── request/               # DTOs para requests
│       ├── LoginRequest.kt
│       ├── RegistroRequest.kt
│       └── OrdenRequest.kt
│
├── 🔄 repository/              # Capa de Acceso a Datos
│   ├── UserRepository.kt      # Operaciones de usuarios
│   ├── ProductRepository.kt   # Operaciones de productos
│   ├── CartRepository.kt      # Operaciones del carrito
│   └── OrderRepository.kt     # Operaciones de órdenes
│
├── 🗄️ data/                    # Capa de Datos
│   ├── entity/                # Entidades Room (DB Local)
│   │   ├── UsuarioEntity.kt   # Usuario en BD
│   │   ├── ProductoEntity.kt  # Producto en BD
│   │   ├── CarritoEntity.kt   # Carrito en BD
│   │   └── OrdenEntity.kt     # Orden en BD
│   │
│   ├── dao/                   # Data Access Objects
│   │   ├── UsuarioDao.kt      # CRUD de usuarios
│   │   ├── ProductoDao.kt     # CRUD de productos
│   │   ├── CarritoDao.kt      # CRUD del carrito
│   │   └── OrdenDao.kt        # CRUD de órdenes
│   │
│   └── database/              # Configuración de BD
│       └── AppDatabase.kt     # Configuración Room
│
├── 🌐 service/                 # Servicios Externos
│   ├── ApiService.kt          # Definición de endpoints
│   └── RetrofitClient.kt      # Configuración Retrofit
│
├── 🔌 client/                  # Clientes HTTP
│   └── ApiClient.kt           # Cliente centralizado
│
├── 🎨 ui/                      # Recursos de UI
│   ├── theme/                 # Tema de la app
│   │   ├── Color.kt           # Paleta de colores
│   │   ├── Theme.kt           # Configuración de tema
│   │   └── Type.kt            # Tipografía
│   │
│   ├── activity/              # Activities (legacy)
│   │   └── MainActivity.kt    # Activity principal
│   │
│   ├── adapter/               # Adapters RecyclerView
│   └── fragment/              # Fragments (legacy)
│
├── 🛠️ util/                    # Utilidades
│   ├── Constants.kt           # Constantes globales
│   ├── Extensions.kt          # Extension functions
│   ├── ValidationUtils.kt     # Validaciones
│   └── DateUtils.kt           # Manejo de fechas
│
└── 📱 MainActivity.kt          # Punto de entrada
```

### 📝 Descripción de Responsabilidades

#### 📱 View Layer
**Responsabilidad:** Presentación visual y manejo de interacciones del usuario.
- Contiene composables de Jetpack Compose
- No contiene lógica de negocio
- Observa estados del ViewModel
- Emite eventos de usuario al ViewModel

#### 🎯 ViewModel Layer
**Responsabilidad:** Lógica de presentación y gestión de estado.
- Procesa eventos de usuario
- Mantiene el estado de la UI en StateFlow/LiveData
- Coordina llamadas al Repository
- Maneja lógica de validación
- Sobrevive a cambios de configuración

#### 🔄 Repository Layer
**Responsabilidad:** Abstracción de fuentes de datos y lógica de negocio.
- Decide cuándo usar datos locales vs remotos
- Implementa estrategias de caché
- Coordina operaciones entre DAO y API
- Transforma entidades a modelos de dominio
- Maneja la sincronización de datos

#### 🗄️ Data Layer
**Responsabilidad:** Persistencia y acceso a datos.
- **Entities:** Representan tablas de Room Database
- **DAOs:** Definen operaciones CRUD con SQL
- **Database:** Configuración de Room con migraciones

#### 🌐 Service Layer
**Responsabilidad:** Comunicación con APIs externas.
- Define contratos de API con Retrofit
- Maneja serialización/deserialización
- Configura interceptores y timeouts

#### 🛠️ Util Layer
**Responsabilidad:** Funciones auxiliares reutilizables.
- Validaciones (email, teléfono, etc.)
- Formateo de fechas y números
- Extension functions de Kotlin
- Constantes globales

---

### 🔄 Flujo de Datos en el Proyecto

```mermaid
Usuario → View → ViewModel → Repository → [Data Source] → Repository → ViewModel → View → Usuario
                                              │
                                              ├── Room Database (Local)
                                              └── Retrofit API (Remote)
```

1. **Usuario** interactúa con la **View**
2. **View** notifica al **ViewModel** del evento
3. **ViewModel** procesa y llama al **Repository**
4. **Repository** decide fuente de datos (local/remote)
5. **Data Source** (Room/API) provee/almacena datos
6. Datos fluyen de vuelta a través de las capas
7. **View** se actualiza automáticamente (reactivo)

---

## 📸 Capturas de Pantalla

> 📝 **Nota:** Esta sección está preparada para incluir capturas de pantalla de la aplicación.

### 🔐 Autenticación
<div align="center">

| Login | Registro |
|-------|----------|
| *Próximamente* | *Próximamente* |

</div>

### 🛒 Productos y Carrito
<div align="center">

| Catálogo | Detalle | Carrito |
|----------|---------|---------|
| *Próximamente* | *Próximamente* | *Próximamente* |

</div>

### 👤 Perfil y Órdenes
<div align="center">

| Perfil | Historial | Detalle Orden |
|--------|-----------|---------------|
| *Próximamente* | *Próximamente* | *Próximamente* |

</div>

---

## ✅ Características Implementadas

### 🎯 Core Features
- ✅ **Autenticación completa** con JWT y persistencia local
- ✅ **Gestión de productos** con categorías y búsqueda
- ✅ **Carrito de compras** funcional con validación de stock
- ✅ **Sistema de órdenes** con historial y detalles
- ✅ **Perfil de usuario** con foto y edición

### 🏛️ Arquitectura y Patrones
- ✅ **Patrón MVVM** completamente implementado
- ✅ **Clean Architecture** con separación de capas
- ✅ **Repository Pattern** para abstracción de datos
- ✅ **Single Source of Truth** con Room como fuente principal

### 🎨 UI/UX
- ✅ **Jetpack Compose** UI 100% declarativa
- ✅ **Material Design 3** con componentes modernos
- ✅ **Navigation Compose** para flujo entre pantallas
- ✅ **Estados de UI** (Loading, Success, Error)
- ✅ **Validaciones en tiempo real** con feedback visual

### 💾 Persistencia y Datos
- ✅ **Room Database** para almacenamiento local
- ✅ **Retrofit** para consumo de API REST
- ✅ **Coroutines** para operaciones asíncronas
- ✅ **StateFlow/LiveData** para gestión de estado reactiva
- ✅ **Caché local** de productos y órdenes

### 📱 Funciones Nativas
- ✅ **Integración con cámara** para foto de perfil
- ✅ **Permisos runtime** para Android 6.0+
- ✅ **Almacenamiento local** de imágenes
- ✅ **Notificaciones** (preparado para implementar)

### 🔒 Seguridad
- ✅ **Autenticación JWT** con tokens seguros
- ✅ **Validación de entrada** en todos los formularios
- ✅ **Manejo seguro** de contraseñas
- ✅ **Headers de seguridad** en peticiones HTTP

---

## 📝 Notas Importantes

### 💡 Buenas Prácticas Implementadas

- ✅ **Separación de responsabilidades**: Cada capa tiene una función específica
- ✅ **Principio DRY**: Código reutilizable en componentes y utilidades
- ✅ **SOLID Principles**: Diseño orientado a interfaces y responsabilidad única
- ✅ **Null Safety**: Uso extensivo de null safety de Kotlin
- ✅ **Immutability**: Preferencia por `val` sobre `var` y data classes inmutables
- ✅ **Coroutines**: Manejo eficiente de operaciones asíncronas
- ✅ **Error Handling**: Gestión de errores con try-catch y estados

### 🎓 Aprendizajes del Proyecto

Este proyecto fue desarrollado con propósitos educativos y demuestra:

1. **Arquitectura escalable**: MVVM permite agregar features sin afectar código existente
2. **UI Moderna**: Jetpack Compose simplifica el desarrollo de interfaces
3. **Persistencia**: Room Database facilita el almacenamiento estructurado
4. **Networking**: Retrofit + Coroutines para llamadas API eficientes
5. **Estado Reactivo**: StateFlow y LiveData para UI reactiva
6. **Lifecycle Awareness**: ViewModels sobreviven a cambios de configuración

### 🔍 Detalles Técnicos

- **Base de Datos**: Room SQLite con migraciones versionadas
- **Persistencia de Sesión**: Token JWT almacenado localmente
- **Caché de Imágenes**: Coil con caché en disco y memoria
- **Timeout de Red**: 60 segundos para cold starts de Render
- **Min SDK**: 24 (Android 7.0) - Cubre >95% de dispositivos
- **Target SDK**: 36 (Android 14) - Latest features

### ⚠️ Limitaciones Conocidas

- **No hay pago real**: Simulación de compras sin integración de pagos
- **Backend externo**: Dependencia de servidor Render (puede estar dormido)
- **Sin modo offline completo**: Requiere internet para funciones principales
- **Sin tests exhaustivos**: Tests básicos implementados
- **Sin CI/CD**: No hay pipeline automatizado de despliegue

---

## 🆘 Soporte y Contacto

### 📧 Contacto

¿Tienes preguntas o necesitas ayuda?

- **GitHub Issues**: [Abrir Issue](https://github.com/ddoblejotadev/TiendaMiMascota-Android/issues)
- **Juan Llontop**: [@ddoblejotadev](https://github.com/ddoblejotadev)
- **Yasser Illanes**: [@yasser-duoc](https://github.com/yasser-duoc)

### 🔗 Enlaces Útiles

- [Documentación Oficial de Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Guía de Arquitectura Android](https://developer.android.com/topic/architecture)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Material Design 3](https://m3.material.io/)

### 📚 Recursos de Aprendizaje

Si estás aprendiendo desarrollo Android, estos recursos te ayudarán:

- [Android Developers Codelabs](https://developer.android.com/courses)
- [Kotlin by JetBrains](https://kotlinlang.org/docs/getting-started.html)
- [Compose Pathway](https://developer.android.com/courses/pathways/compose)
- [Android Architecture Components](https://developer.android.com/topic/architecture)

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo uso académico.

**Proyecto Académico** - DSY1105 Desarrollo de Aplicaciones Móviles  
**Institución**: DUOC UC  
**Año**: 2025  
**Propósito**: Educativo y demostrativo

---

## 🙏 Agradecimientos

- **DUOC UC** por la formación en desarrollo móvil
- **Profesor(es)** del curso DSY1105 por la guía y feedback
- **Google** por las herramientas y librerías de Android
- **JetBrains** por Kotlin y las herramientas de desarrollo
- **Comunidad de Android** por la documentación y ejemplos

---

## ⭐ Reconocimientos

Si este proyecto te fue útil, considera:

- ⭐ Dar una estrella al repositorio
- 🔀 Hacer fork para tus propios proyectos
- 📣 Compartir con otros estudiantes
- 🐛 Reportar bugs o sugerir mejoras
- 🤝 Contribuir con código o documentación

---

<div align="center">

### 🐾 Hecho con ❤️ usando Kotlin y Jetpack Compose

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

**[⬆ Volver arriba](#-tiendamimascota)**

</div>
