package com.antique.post.repo

import androidx.core.net.toUri
import com.antique.post.data.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryImpl(private val dispatcher: CoroutineDispatcher) : PostRepository {
    override suspend fun getCategories(): List<String> = withContext(dispatcher) {
        val response = Firebase.database.reference.child("Category").get().await()
        val categories = mutableListOf<String>()

        response.children.forEach {
            it.getValue(String::class.java)?.let { category ->
                categories.add(category)
            }
        }

        categories.toList()
    }

    override suspend fun registerPost(details: String, category: String, uris: List<String>): Boolean = withContext(dispatcher) {
        val images = mutableListOf<String>()
        uris.forEach {
            images.add(uploadImage(it))
        }

        val uid = Firebase.auth.currentUser?.uid.toString()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        val postId = Firebase.database.reference.child("User").child(uid).child("Post").push().key.toString()
        val post = Post(uid, details, images, category, date, postId)

        val userRef = Firebase.database.reference.child("User").child(uid).child("Post").child(postId)
        val postRef = Firebase.database.reference.child("Post").child(category).child(postId)

        userRef.setValue(post).await()
        postRef.setValue(post).await()

        validate(userRef) && validate(postRef)
    }

    private suspend fun uploadImage(uri: String): String {
        val fileName = SimpleDateFormat("yyyy-MM-dd-hhmmss").format(Date()) + ".jpg"
        Firebase.storage.reference.child("images/$fileName").putFile(uri.toUri()).await()
        return Firebase.storage.reference.child("images/$fileName").downloadUrl.await().toString()
    }

    private suspend fun validate(ref: DatabaseReference): Boolean = ref.get().await().exists()
}