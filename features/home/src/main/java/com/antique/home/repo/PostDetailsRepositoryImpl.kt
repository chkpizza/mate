package com.antique.home.repo

import com.antique.common.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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

    override suspend fun registerComment(postOverview: PostOverview, details: String): Comment = withContext(dispatcher) {
        val commentId = Firebase.database.reference.child(Constant.POST_NODE).child(postOverview.category).child(postOverview.postId).child(Constant.COMMENT_NODE).push().key.toString()

        val uid = Firebase.auth.currentUser?.uid.toString()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        val comment = Comment(postOverview, uid, details, date, commentId)

        val commentRef = Firebase.database.reference.child(Constant.POST_NODE).child(postOverview.category).child(postOverview.postId).child(Constant.COMMENT_NODE).child(commentId)
        val userRef = Firebase.database.reference.child(Constant.USER_NODE).child(uid).child(Constant.COMMENT_NODE).child(commentId)

        commentRef.setValue(comment).await()
        userRef.setValue(comment).await()

        if(!validate(commentRef) || !validate(userRef)) {
            throw RuntimeException("CommentRegisterException")
        }

        commentRef.get().await().getValue(Comment::class.java) ?: throw RuntimeException("CommentRegisterException")
    }

    override suspend fun removeComment(comment: Comment) {
        withContext(dispatcher) {
            Firebase.database.reference.child(Constant.POST_NODE).child(comment.postOverview.category).child(comment.postOverview.postId).child(Constant.COMMENT_NODE).child(comment.commentId).setValue(null).await()
            Firebase.database.reference.child(Constant.USER_NODE).child(comment.author).child(Constant.COMMENT_NODE).child(comment.commentId).setValue(null).await()
        }
    }

    override suspend fun removePost(post: Post): Boolean = withContext(dispatcher) {
        val userRef = Firebase.database.reference.child(Constant.USER_NODE).child(post.author).child(Constant.POST_NODE).child(post.postId)
        val postRef = Firebase.database.reference.child(Constant.POST_NODE).child(post.category).child(post.postId)
        userRef.setValue(null).await()
        postRef.setValue(null).await()

        !(validate(userRef) && validate(postRef))
    }

    override suspend fun reportPost(post: Post): Boolean = withContext(dispatcher) {
        val uid = Firebase.auth.currentUser?.uid.toString()
        val reportRef = Firebase.database.reference.child(Constant.REPORT_NODE).child(uid).child(Constant.POST_REPORT_NODE).child(post.postId)

        if(!validate(reportRef)) {
            reportRef.setValue(post).await()
            validate(reportRef)
        } else {
            false
        }
    }

    override suspend fun reportComment(comment: Comment): Boolean = withContext(dispatcher) {
        val uid = Firebase.auth.currentUser?.uid.toString()
        val reportRef = Firebase.database.reference.child(Constant.REPORT_NODE).child(uid).child(Constant.COMMENT_REPORT_NODE).child(comment.commentId)

        if(!validate(reportRef)) {
            reportRef.setValue(comment).await()
            validate(reportRef)
        } else {
            false
        }
    }

    private suspend fun validate(ref: DatabaseReference): Boolean = ref.get().await().exists()


}