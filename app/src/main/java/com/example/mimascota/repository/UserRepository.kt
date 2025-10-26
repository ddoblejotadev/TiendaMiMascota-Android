package com.example.mimascota.repository

import android.content.Context
import com.example.mimascota.Model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class UserRepository {

    private val fileName = "users.json"

    // Guardar un usuario en filesDir con validación de duplicados
    fun guardarUsuarioEnArchivo(context: Context, user: User): Boolean {
        return try {
            val gson = Gson()
            val archivo = File(context.filesDir, fileName)

            val usuariosExistentes: MutableList<User> = if (archivo.exists()) {
                val json = archivo.readText()
                if (json.isNotEmpty()) {
                    val listType = object : TypeToken<MutableList<User>>() {}.type
                    gson.fromJson(json, listType)
                } else mutableListOf()
            } else mutableListOf()

            val existe = usuariosExistentes.any {
                it.id == user.id ||
                        it.run.equals(user.run, ignoreCase = true) ||
                        it.username.equals(user.username, ignoreCase = true) ||
                        it.email.equals(user.email, ignoreCase = true)
            }

            if (existe) return false

            usuariosExistentes.add(user)
            archivo.writeText(gson.toJson(usuariosExistentes))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Obtener usuarios desde filesDir o fallback a assets
    fun obtenerUsuarios(context: Context): List<User> {
        val archivo = File(context.filesDir, fileName)
        return try {
            if (archivo.exists()) {
                val json = archivo.readText()
                if (json.isNotEmpty()) {
                    val listType = object : TypeToken<List<User>>() {}.type
                    Gson().fromJson<List<User>>(json, listType) ?: emptyList()
                } else emptyList()
            } else {
                val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<User>>() {}.type
                Gson().fromJson<List<User>>(json, listType) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Generar ID automático basado en usuarios existentes
    fun generarNuevoId(context: Context): Int {
        val usuarios = obtenerUsuarios(context)
        return if (usuarios.isEmpty()) 1 else usuarios.maxOf { it.id } + 1
    }

}