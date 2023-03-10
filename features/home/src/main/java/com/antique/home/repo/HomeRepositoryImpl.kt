package com.antique.home.repo

import com.antique.common.Constant
import com.antique.common.Post
import com.antique.home.data.Block
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val dispatcher: CoroutineDispatcher) : HomeRepository {
    override suspend fun getCategories(): List<String> = withContext(dispatcher) {
        val response = Firebase.database.reference.child(Constant.CATEGORY_NODE).get().await()
        val categories = mutableListOf<String>()

        response.children.forEach {
            it.getValue(String::class.java)?.let { category ->
                categories.add(category)
            }
        }

        categories.toList()
    }

    override suspend fun getInitPosts(category: String): List<Post> = withContext(dispatcher) {
        var index = Constant.UNINITIALIZED_VALUE
        val posts = mutableListOf<Post>()
        var filteredPosts: List<Post>

        do {
            posts.clear()

            val response = if(index == Constant.UNINITIALIZED_VALUE) {
                Firebase.database.reference.child(Constant.POST_NODE).child(category).limitToLast(10).get().await()
            } else {
                Firebase.database.reference.child(Constant.POST_NODE).child(category).orderByKey().endBefore(index).limitToLast(10).get().await()
            }

            if(!response.exists()) {
                filteredPosts = emptyList()
                break
            }

            response.children.forEach {
                it.getValue(Post::class.java)?.let { post ->
                    posts.add(post)
                }
            }

            index = posts.first().postId
            filteredPosts = filter(posts)

        } while(filteredPosts.isEmpty())
        filteredPosts.toList()
    }

    override suspend fun getMorePosts(category: String, _index: String): List<Post> = withContext(dispatcher) {
        var index = _index
        val posts = mutableListOf<Post>()
        var filteredPosts: List<Post>

        do {
            posts.clear()

            val response = Firebase.database.reference.child(Constant.POST_NODE).child(category).orderByKey().endBefore(index).limitToLast(10).get().await()

            if(!response.exists()) {
                filteredPosts = emptyList()
                break
            }

            response.children.forEach {
                it.getValue(Post::class.java)?.let { post ->
                    posts.add(post)
                }
            }

            index = posts.first().postId
            filteredPosts = filter(posts)
        } while(filteredPosts.isEmpty())

        filteredPosts
    }

    private suspend fun filter(posts: MutableList<Post>): List<Post> {
        val filteredPosts: List<Post>
        val blockers = mutableListOf<String>()
        val uid = Firebase.auth.currentUser?.uid.toString()

        val response = Firebase.database.reference.child(Constant.BLOCK_NODE).child(uid).get().await()

        return if(response.exists()) {
            response.children.forEach {
                it.getValue(Block::class.java)?.let { block ->
                    blockers.add(block.blockedUser)
                }
            }

            filteredPosts = posts.filterNot { it.author in blockers }
            filteredPosts
        } else {
            posts.toList()
        }
    }

    override suspend fun getCommentCount(post: Post): Long = withContext(dispatcher) {
        val response = Firebase.database.reference.child(Constant.POST_NODE).child(post.category).child(post.postId).child(Constant.COMMENT_NODE).get().await()
        response.childrenCount
    }
}