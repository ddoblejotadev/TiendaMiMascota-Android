package com.example.mimascota.data.database

import android.content.Context
import android.util.Log
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
                val dbName = "tienda_mascota.db"
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbName
                ).fallbackToDestructiveMigration(true)

                val instance = try {
                    builder.build()
                } catch (e: IllegalStateException) {
                    // Room schema identity mismatch -> delete DB and recreate (safe for dev)
                    Log.w("AppDatabase", "Room schema mismatch detected, deleting DB and recreating: ${e.message}")
                    try {
                        context.deleteDatabase(dbName)
                    } catch (ex: Exception) {
                        Log.e("AppDatabase", "Failed to delete database file: ${ex.message}")
                    }
                    builder.build()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
