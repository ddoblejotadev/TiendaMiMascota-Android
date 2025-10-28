package com.example.mimascota.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mimascota.data.dao.CartItemDao
import com.example.mimascota.data.dao.UserDao
import com.example.mimascota.data.entity.CartItemEntity
import com.example.mimascota.data.entity.UserEntity

/**
 * AppDatabase: Clase principal de la base de datos Room
 * Define todas las entidades y DAOs
 */
@Database(
    entities = [UserEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Declarar DAOs disponibles
    abstract fun userDao(): UserDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Función para obtener instancia de la base de datos (Singleton Pattern)
         * Garantiza que solo exista una instancia de la BD en toda la aplicación
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_mascota.db" // Nombre del archivo de BD
                )
                    .fallbackToDestructiveMigration() // Para desarrollo: borra BD si hay cambios de versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

