plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinComposeCompiler)
    id("org.jetbrains.kotlin.kapt") // Restaurado KAPT
}

android {
    namespace = "com.example.mimascota" // Cambiado según tu requisito
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mimascota" // Cambiado según tu requisito
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Variables de entorno por defecto
        buildConfigField("String", "BASE_URL_DEV", "\"http://10.0.2.2:8080/api/\"")
        buildConfigField("String", "BASE_URL_PROD", "\"https://tiendamimascotabackends.onrender.com/api/\"")
    }

    buildTypes {
        debug {
            // Modo desarrollo - servidor local (emulador usa 10.0.2.2 para localhost)
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
            isMinifyEnabled = false
        }
        
        release {
            // Modo producción - servidor Render
            buildConfigField("String", "BASE_URL", "\"https://tiendamimascotabackends.onrender.com/api/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        // Ajustar language version para Kotlin 2.1
        languageVersion = "2.1"
    }

    // Configurar Kotlin Compiler Extension para Compose (requerido a partir de Kotlin 2.0)
    composeOptions {
        // Usar la versión alineada en el catálogo de versiones
        kotlinCompilerExtensionVersion = "2.1.20"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true // Habilitar BuildConfig
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Core / Activity
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation(platform(libs.androidx.compose.bom))

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel + lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil:2.3.0")

    // Gson / Retrofit / OkHttp
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Room (temporalmente con KAPT para mantener estabilidad)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler) // usar KSP en el futuro: reemplazar por `ksp(libs.androidx.room.compiler)` y habilitar plugin

    // RecyclerView y Material Components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Fragment y Navigation tradicional
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // LiveData y ViewModel tradicional
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Glide (mantener en kapt hasta confirmar soporte KSP)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.mockito:mockito-core:5.+")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}

// Nota: Para completar la migración a KSP deberías:
// 1) Revisar todos los módulos que usan `kapt` y sustituir por `ksp` si el procesador lo soporta.
// 2) Remover `kapt` plugin si ya no hay procesadores que lo requieran.
// 3) Ejecutar una build para validar la generación de código.
