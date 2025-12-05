package com.elp.viarapida.data.repository

import com.elp.viarapida.data.model.Usuario
import com.elp.viarapida.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class UsuarioRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /**
     * Obtiene los datos del usuario actual desde Firestore
     */
    suspend fun getUsuarioActual(): Result<Usuario> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val usuarioDoc = firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(userId)
                .get()
                .await()

            val usuario = usuarioDoc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            Result.success(usuario)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un usuario por su ID
     */
    suspend fun getUsuarioPorId(userId: String): Result<Usuario> {
        return try {
            val usuarioDoc = firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(userId)
                .get()
                .await()

            val usuario = usuarioDoc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            Result.success(usuario)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos del perfil del usuario
     */
    suspend fun actualizarPerfil(
        nombre: String,
        apellido: String,
        telefono: String
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val updates = hashMapOf<String, Any>(
                "nombre" to nombre,
                "apellido" to apellido,
                "telefono" to telefono
            )

            firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(userId)
                .update(updates)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza la foto de perfil (URL)
     */
    suspend fun actualizarFotoPerfil(photoUrl: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(userId)
                .update("fotoPerfil", photoUrl)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sube una foto de perfil a Firebase Storage y actualiza la URL
     */
    suspend fun subirFotoPerfil(imageFile: File): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // 1. Subir imagen a Storage
            val storageRef = storage.reference
                .child("perfil_fotos/$userId.jpg")

            val uploadTask = storageRef.putFile(android.net.Uri.fromFile(imageFile)).await()

            // 2. Obtener URL de descarga
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            // 3. Actualizar URL en Firestore
            actualizarFotoPerfil(downloadUrl)

            Result.success(downloadUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el email del usuario
     */
    suspend fun actualizarEmail(nuevoEmail: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // 1. Actualizar en Auth
            user.updateEmail(nuevoEmail).await()

            // 2. Actualizar en Firestore
            firestore.collection(Constants.COLLECTION_USUARIOS)
                .document(user.uid)
                .update("email", nuevoEmail)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si un email ya está registrado
     */
    suspend fun emailYaExiste(email: String): Boolean {
        return try {
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            methods.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            false
        }
    }
}