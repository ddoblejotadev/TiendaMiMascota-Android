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
 * AppDatabase: Base de datos Room principal
 *
 * Entidades: UserEntity, CartItemEntity
 * DAOs: UserDao, CartItemDao
 * Patrón: Singleton
 */
@Database(
    entities = [UserEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_mascota.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

