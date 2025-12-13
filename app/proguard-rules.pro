# Reglas para mantener las clases del modelo de datos intactas.
-keep class com.example.mimascota.model.** { *; }

# Reglas adicionales para asegurar el correcto funcionamiento de Gson.
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class com.google.gson.annotations.SerializedName {
    <fields>;
}

# Si usas Exposed, también es bueno mantener las clases de tabla.
-keep class org.jetbrains.exposed.sql.Table { *; }
-keep class org.jetbrains.exposed.dao.id.IdTable { *; }
