package com.antique.home.repo

import com.antique.common.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostDetailsRepositoryImpl(private val dispatcher: CoroutineDispatcher) : PostDetailsRepository {
    override suspend fun getPostDetails(postId: String, category: String): PostDetails = withContext(dispatcher) {
        val post = Firebase.database.reference.child(Constant.POST_NODE).child(category).child(postId).get().await().getValue(Post::class.java)
            ?: throw RuntimeException(Constant.CLASS_CAST_EXCEPTION)

        val comments = mutableListOf<Comment>()
        Firebase.database.reference.child(Constant.POST_NODE).child(category).child(postId).child(Constant.COMMENT_NODE).get().await().children.forEach {
            it.getValue(Comment::class.java)?.let { comment ->
                comments.add(comment)
            }
        }

        PostDetails(post.author, post.details, post.images, post.category, post.date, post.postId, comments)
    }

    override suspend fun getAuthor(uid: String): User = withContext(dispatcher) {
        Firebase.database.reference.child(Constant.USER_NODE).child(uid).get().await().getValue(User::class.java)
            ?: throw RuntimeException(Constant.CLASS_CAST_EXCEPTION)
    }


}