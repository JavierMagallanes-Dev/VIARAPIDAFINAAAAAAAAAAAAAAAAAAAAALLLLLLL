package com.elp.viarapida.data.repository

import com.elp.viarapida.data.model.Usuario
import com.elp.viarapida.util.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Obtiene el usuario actual autenticado
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Verifica si hay un usuario logueado
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Registra un nuevo usuario
     */
    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String
    ): Result<Usuario> {
        return try {
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al crear usuario"))

            // 2. Crear documento de usuario en Firestore
            val usuario = Usuario(
                uid = firebaseUser.uid,
                email = email,
                nombre = nombre,
                apellido = apellido,
                telefono = telefono,
                fotoPerfil = "",
                fechaRegistro = Timestamp.now()
            )

            firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(firebaseUser.uid)
                .set(usuario)
                .await()

            Result.success(usuario)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            // 1. Autenticar en Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al iniciar sesión"))

            // 2. Obtener datos del usuario desde Firestore
            val usuarioDoc = firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val usuario = usuarioDoc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado en base de datos"))

            Result.success(usuario)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Envía email de recuperación de contraseña
     */
    suspend fun enviarEmailRecuperacion(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza la contraseña del usuario actual
     */
    suspend fun actualizarPassword(nuevaPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("Usuario no autenticado"))

            user.updatePassword(nuevaPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina la cuenta del usuario
     */
    suspend fun eliminarCuenta(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // 1. Eliminar documento de Firestore
            firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(user.uid)
                .delete()
                .await()

            // 2. Eliminar cuenta de Auth
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}