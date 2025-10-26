# 🐾 Mi Mascota - Tienda de Mascotas Android

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)

Aplicación móvil Android para una tienda de productos de mascotas. Proyecto educativo desarrollado con Kotlin y Jetpack Compose.

---

## ✅ Requerimientos Implementados

### Pantallas Principales

| # | Pantalla | Estado |
|---|----------|--------|
| 1 | Pantalla de Login | ✅ |
| 2 | Pantalla de Registro (con validación RUT) | ✅ |
| 3 | Catálogo de Productos | ✅ |
| 4 | Detalle del Producto | ✅ |
| 5 | Carrito de Compras Funcional | ✅ |
| 6 | Compra Exitosa | ✅ |
| 7 | Compra Rechazada | ✅ |
| 8 | Panel Administrativo (Back Office) | ✅ |
| 9 | Lista de Productos (Admin) | ✅ |
| 10 | Agregar Producto (Visual) | ✅ |

---

## 🚀 Instalación

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17 o superior
- SDK Android 24 o superior

### Pasos
1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar la aplicación (▶️)

---

## 📖 Guía de Uso

### Usuario Normal
1. **Registrarse:** Completa el formulario con RUT, email, contraseña, etc.
2. **Login:** Inicia sesión con tus credenciales
3. **Explorar Catálogo:** Ver productos con precios y stock
4. **Agregar al Carrito:** Selecciona productos y cantidades
5. **Finalizar Compra:** Completa tu pedido

### Administrador
- **Usuario:** `admin`
- **Contraseña:** `admin`
- Acceso exclusivo al **Panel Administrativo**
- Ver lista de productos con información administrativa
- Formulario visual para agregar productos (sin funcionalidad de guardado)

---

## 🛠️ Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM
- **Navegación:** Navigation Compose
- **Imágenes:** Coil
- **JSON:** Gson
- **Estado:** StateFlow

---

## 📁 Estructura del Proyecto

```
app/src/main/
├── java/com/example/mimascota/
│   ├── MainActivity.kt
│   ├── Model/
│   │   ├── Producto.kt
│   │   ├── CartItem.kt
│   │   └── User.kt
│   ├── View/
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── CatalogoScreen.kt
│   │   ├── DetalleProductoScreen.kt
│   │   ├── CarritoScreen.kt
│   │   ├── CompraExitosaScreen.kt
│   │   ├── CompraRechazadaScreen.kt
│   │   ├── BackOfficeScreen.kt
│   │   ├── AgregarProductoScreen.kt
│   │   └── AboutUsScreen.kt
│   ├── ViewModel/
│   │   ├── AuthViewModel.kt
│   │   ├── CatalogoViewModel.kt
│   │   └── CartViewModel.kt
│   └── repository/
│       ├── ProductoRepository.kt
│       └── UserRepository.kt
└── assets/
    ├── products.json
    └── users.json
```

---

## 🎯 Funcionalidades Destacadas

### Sistema de Autenticación
- Login con validación de credenciales
- Registro con validación de RUT (dígito verificador)
- Usuario administrador especial (admin/admin)
- Control de acceso por roles
- Cerrar sesión

### Gestión de Stock
- Validación en tiempo real
- Indicadores visuales (verde/naranja/rojo)
- Advertencias cuando se excede stock
- Validación en checkout

### Carrito de Compras
- Agregar/quitar productos
- Modificar cantidades
- Cálculo automático de totales
- Validación de stock antes de comprar

### Compra
- **Compra Exitosa:** Número de pedido, detalles, total
- **Compra Rechazada:** 3 tipos de error (stock, pago, conexión)
- Opciones para corregir o reintentar

### Panel Administrativo
- Acceso exclusivo para admin
- Lista completa de productos
- Formulario visual para agregar productos (sin guardado)

---

## 📊 Datos de Prueba

### Credenciales Admin
```
Usuario: admin
Contraseña: admin
```

### Productos de Ejemplo
- Pelota para perro - $3.990 (Stock: 3)
- Shampoo para gato - $5.990 (Stock: 2)
- Alimento Premium - $15.990 (Stock: 5)
- Collar de cuero - $8.990 (Stock: 4)

---

## 🐛 Resolución de Problemas

### Las imágenes no cargan
- Verifica conexión a internet
- Revisa configuración de Coil en `build.gradle`

### No veo el Panel Administrativo
- Asegúrate de iniciar sesión con `admin`/`admin`
- Verifica que el botón aparezca en la sección de administración del Home

### Error al finalizar compra
- Verifica que haya stock disponible
- Revisa que el `CartViewModel` esté correctamente configurado

---

## 📄 Licencia

Proyecto educativo de código abierto bajo licencia MIT.

---

## 👥 Autores

- **[ddoblejotadev](https://github.com/ddoblejotadev)** - Desarrollador
- **[yasser-illanes-rocha](https://github.com/yasser-duoc)** - Desarrollador

Proyecto educativo para curso de Desarrollo Android.

---

**© 2025 Mi Mascota - ¡Todo para tu mejor amigo! 🐶🐱**
