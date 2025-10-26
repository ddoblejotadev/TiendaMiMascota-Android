# 🐾 Mi Mascota - Tienda de Mascotas Android

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)

Aplicación móvil Android para una tienda de productos de mascotas. Proyecto educativo desarrollado con Kotlin y Jetpack Compose siguiendo las mejores prácticas de desarrollo Android moderno.

## 📱 Descripción

**Mi Mascota** es una aplicación e-commerce completa para la venta de productos para mascotas. Incluye gestión de usuarios, catálogo de productos, carrito de compras con validación de stock en tiempo real, y un flujo de compra completo con manejo de errores.

### 🎯 Propósito Educativo

Este proyecto está diseñado como material educativo para un curso introductorio de desarrollo Android, demostrando:
- Arquitectura MVVM (Model-View-ViewModel)
- Jetpack Compose para UI declarativa
- Navegación con Navigation Compose
- State management con StateFlow
- Validación de datos en múltiples niveles
- Manejo de errores y feedback al usuario

## ✨ Características Principales

### 🔐 Autenticación
- **Pantalla de Registro** con validación de campos (nombre, RUT, email, contraseña, dirección)
- **Pantalla de Login** con diseño moderno y logo de la marca
- Validación de RUT con dígito verificador
- Persistencia de usuarios en JSON local

### 🛍️ Catálogo y Productos
- **Catálogo de Productos** con lista scrollable
- **Detalle de Producto** con información completa
- Imágenes de productos cargadas con Coil
- Indicadores visuales de stock disponible (colores semafóricos)
- Búsqueda y filtrado por categorías

### 🛒 Carrito de Compras Funcional
- Agregar/quitar productos del carrito
- Modificar cantidades con controles +/-
- **Validación de stock en tiempo real**
- Indicadores visuales cuando se excede el stock
- Cálculo automático de totales y subtotales
- Badge con cantidad de items en el ícono del carrito

### 💳 Proceso de Compra
- **Validación optimista**: permite agregar al carrito con advertencias
- **Validación final en checkout**: verifica stock antes de finalizar
- **Pantalla de Compra Exitosa** con:
  - Número de pedido único generado
  - Fecha y hora de la compra
  - Lista detallada de productos
  - Total pagado
  - Opciones para continuar comprando o volver al inicio

- **Pantalla de Compra Rechazada** con tres tipos de error:
  - ❌ **Stock Insuficiente**: lista productos específicos con problema
  - ❌ **Error de Pago**: simulación de fallo en el pago (10%)
  - ❌ **Error de Conexión**: simulación de fallo de red (10%)
  - Sugerencias específicas para cada tipo de error
  - Opciones para reintentar o revisar carrito

### 📊 Gestión de Stock
- Stock real por producto (desde JSON)
- Validación en tiempo real al agregar productos
- Feedback visual en múltiples niveles:
  - Snackbars informativos
  - Indicadores en catálogo (verde/naranja/rojo)
  - Highlight en carrito (fondo rojo si excede)
  - Tarjeta de advertencia en detalle
  - Validación final en checkout
- No bloquea la UI, permite agregar con advertencia

### 🎨 Interfaz de Usuario
- **Material Design 3** con theme dinámico
- **Jetpack Compose** para UI declarativa
- Diseño responsive y adaptativo
- Iconos descriptivos en formularios
- Cards elevadas con sombras
- Gradientes y colores semafóricos
- Animaciones suaves de navegación
- FloatingActionButton para "Ir a pagar"

### 🔍 Otras Funcionalidades
- Pantalla "Acerca de Nosotros"
- Navegación con botón "Atrás" en todas las pantallas
- Manejo de estados (loading, error, success)
- Feedback inmediato al usuario (snackbars)
- Formateo de precios en pesos chilenos

## 🛠️ Tecnologías Utilizadas

### Lenguaje y Framework
- **Kotlin** - Lenguaje de programación moderno para Android
- **Jetpack Compose** - UI Toolkit declarativo
- **Material Design 3** - Sistema de diseño de Google

### Arquitectura y Patrones
- **MVVM** (Model-View-ViewModel)
- **StateFlow** para manejo de estado reactivo
- **Navigation Compose** para navegación entre pantallas
- **Sealed Classes** para resultados y estados

### Librerías Principales
```gradle
// UI y Compose
androidx.compose.ui
androidx.compose.material3
androidx.compose.material:material-icons-extended

// Navegación
androidx.navigation:navigation-compose

// Imágenes
io.coil-kt:coil-compose

// JSON
com.google.code.gson:gson

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
```

### Persistencia de Datos
- **JSON local** en `assets/` para productos y usuarios
- **StateFlow** para estado en memoria durante la sesión

## 📁 Estructura del Proyecto

```
app/src/main/
├── java/com/example/mimascota/
│   ├── MainActivity.kt                 # Actividad principal con NavHost
│   │
│   ├── Model/                          # Modelos de datos
│   │   ├── Producto.kt
│   │   ├── CartItem.kt
│   │   └── Usuario.kt
│   │
│   ├── View/                           # Pantallas (Composables)
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── CatalogoScreen.kt
│   │   ├── DetalleProductoScreen.kt
│   │   ├── CarritoScreen.kt
│   │   ├── CompraExitosaScreen.kt
│   │   ├── CompraRechazadaScreen.kt
│   │   └── AboutUsScreen.kt
│   │
│   ├── ViewModel/                      # ViewModels (Lógica de negocio)
│   │   ├── AuthViewModel.kt
│   │   ├── CatalogoViewModel.kt
│   │   └── CartViewModel.kt
│   │
│   └── repository/                     # Repositorios (Acceso a datos)
│       └── ProductoRepository.kt
│
└── assets/                             # Archivos JSON
    ├── products.json                   # Catálogo de productos
    └── users.json                      # Base de datos de usuarios
```

## 🚀 Instalación y Ejecución

### Requisitos Previos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- SDK Android 24 (Nougat) o superior
- Emulador Android o dispositivo físico

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/TiendaMiMascota-Android.git
   cd TiendaMiMascota-Android
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta del proyecto

3. **Sincronizar Gradle**
   - El proyecto sincronizará automáticamente
   - Esperar a que descargue todas las dependencias

4. **Ejecutar la aplicación**
   - Seleccionar un emulador o dispositivo físico
   - Click en el botón "Run" (▶️) o `Shift + F10`

## 📖 Guía de Uso

### 1. Registro e Inicio de Sesión
1. Al abrir la app, verás la pantalla de **Registro**
2. Completa el formulario con:
   - Nombre completo
   - RUT (formato: 12345678-9)
   - Email
   - Contraseña
   - Dirección
3. Presiona "Registrar"
4. Navega a "Iniciar sesión" e ingresa tus credenciales

### 2. Explorar el Catálogo
1. Desde el Home, selecciona "Catálogo"
2. Verás los productos con:
   - Imagen, nombre y precio
   - Indicador de stock disponible
   - Controles para agregar al carrito
3. Haz clic en un producto para ver el detalle completo

### 3. Agregar al Carrito
1. En el catálogo o detalle, haz clic en "Agregar al carrito"
2. Si excedes el stock, verás una advertencia: "⚠️ Stock limitado"
3. Los productos excedidos se destacan en rojo en el carrito
4. Puedes aumentar/disminuir cantidades con los botones +/-

### 4. Realizar una Compra
1. Haz clic en el ícono del carrito 🛒 o en "Ir a pagar"
2. Revisa tu carrito y las cantidades
3. Presiona "Finalizar compra 🐶"
4. Posibles resultados:
   - **✅ Compra Exitosa** (80% probabilidad si hay stock)
   - **❌ Stock Insuficiente** (si excedes stock disponible)
   - **❌ Error de Pago** (10% simulado)
   - **❌ Error de Conexión** (10% simulado)

### 5. Probar Errores de Stock
Para ver rápidamente el error de stock:
1. Ve al "Shampoo para gato" (Stock: 2)
2. Agrégalo **3 veces**
3. Ve al carrito (verás fondo rojo en el item)
4. Intenta finalizar compra
5. Verás la pantalla de error con: "Shampoo: Tienes 3, Disponibles 2"

## 🎓 Conceptos de Aprendizaje

Este proyecto enseña:

### Principiantes
- ✅ Jetpack Compose básico
- ✅ Navegación entre pantallas
- ✅ State management con `remember` y `mutableStateOf`
- ✅ Listas con `LazyColumn`
- ✅ Formularios y validación

### Intermedio
- ✅ MVVM Architecture
- ✅ StateFlow y collectAsState
- ✅ Sealed Classes para resultados
- ✅ UI condicional compleja
- ✅ Validación en múltiples niveles
- ✅ Feedback visual al usuario

### Avanzado
- ✅ Validación optimista vs bloqueante
- ✅ Composición y reusabilidad de componentes
- ✅ Manejo de estado complejo
- ✅ Material Design 3 completo
- ✅ Patrones de navegación

## 📝 Datos de Prueba

### Productos Disponibles
| Producto | Precio | Stock |
|----------|--------|-------|
| Pelota para perro | $3.990 | 3 |
| Shampoo para gato | $5.990 | 2 |
| Alimento Premium | $15.990 | 5 |
| Collar de cuero | $8.990 | 4 |

### Usuario de Prueba
Puedes crear cualquier usuario desde el registro, o usar datos de prueba que hayas creado anteriormente.

## 🐛 Resolución de Problemas

### El gradiente no se ve correctamente
- Asegúrate de usar Material Design 3 con `MaterialTheme`
- Verifica que los colores del theme estén configurados

### Las imágenes no cargan
- Verifica conexión a internet (usa URLs de productos)
- Revisa que Coil esté correctamente configurado en `build.gradle`

### Error al finalizar compra
- Verifica que el stock en `products.json` sea mayor a 0
- Revisa que el `CartViewModel` esté correctamente inyectado

## 🤝 Contribuciones

Este es un proyecto educativo. Las contribuciones son bienvenidas:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT para fines educativos.

## 👥 Autores

- **Equipo de Desarrollo** - Proyecto educativo para curso de Android

## 🙏 Agradecimientos

- Material Design 3 por las guías de diseño
- Jetpack Compose por simplificar el desarrollo de UI
- La comunidad de Android por los recursos y documentación

---

**© 2025 Mi Mascota - Todos los derechos reservados**

🐶 ¡Todo para tu mejor amigo! 🐱

