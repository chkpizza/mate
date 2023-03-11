package com.antique.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.common.ApiStatus
import com.antique.common.PostOverview
import com.antique.common.SingleEvent
import com.antique.home.data.CommentUiState
import com.antique.home.data.PostDetailsUiState
import com.antique.home.data.PostUiState
import com.antique.home.usecase.*
import kotlinx.coroutines.launch

class PostDetailsViewModel(
    private val getPostDetailsUseCase: GetPostDetailsUseCase,
    private val registerCommentUseCase: RegisterCommentUseCase,
    private val removeCommentUseCase: RemoveCommentUseCase,
    private val removePostUseCase: RemovePostUseCase,
    private val reportPostUseCase: ReportPostUseCase,
    private val reportCommentUseCase: ReportCommentUseCase
) : ViewModel() {
    private val _postDetails = MutableLiveData<ApiStatus<PostDetailsUiState>>()
    val postDetails: MutableLiveData<ApiStatus<PostDetailsUiState>> get() = _postDetails

    private val _comments = MutableLiveData<ApiStatus<List<CommentUiState>>>()
    val comments: LiveData<ApiStatus<List<CommentUiState>>> get() = _comments

    private val _removePostState = MutableLiveData<SingleEvent<Boolean>>()
    val removePostState: LiveData<SingleEvent<Boolean>> get() = _removePostState

    private val _removeCommentState = MutableLiveData<SingleEvent<Boolean>>()
    val removeCommentStatus: LiveData<SingleEvent<Boolean>> get() = _removeCommentState

    private val _reportPostState = MutableLiveData<SingleEvent<Boolean>>()
    val reportPostStatus: LiveData<SingleEvent<Boolean>> get() = _reportPostState

    private val _reportCommentState = MutableLiveData<SingleEvent<Boolean>>()
    val reportCommentState: LiveData<SingleEvent<Boolean>> get() = _reportCommentState

    private lateinit var postOverview: PostOverview

    fun getPostDetails(postId: String, category: String) {
        viewModelScope.launch {
            try {
                val response = getPostDetailsUseCase(postId, category)
                postOverview = PostOverview(response.author.uid, response.postId, response.category)
                _postDetails.value = ApiStatus.Success(response)
                _comments.value = ApiStatus.Success(response.comments)
            } catch (e: Exception) {
                _postDetails.value = ApiStatus.Error(e)
            }
        }
    }

    fun registerComment(details: String) {
        viewModelScope.launch {
            try {
                val response = registerCommentUseCase(postOverview, details)

                comments.value?.let { comments ->
                    when(comments) {
                        is ApiStatus.Success -> {
                            val newList = comments.items.toMutableList()
                            newList.add(response)
                            _comments.value = ApiStatus.Success(newList.toList())
                        }
                        is ApiStatus.Error -> {}
                        is ApiStatus.Loading -> {}
                    }
                }
            } catch (_: Exception) {

            }
        }
    }

    fun removeComment(comment: CommentUiState) {
        viewModelScope.launch {
            try {
                removeCommentUseCase(comment)
                comments.value?.let {
                    when(it) {
                        is ApiStatus.Success -> {
                            val newList = it.items.toMutableList()
                            _comments.value = ApiStatus.Success(
                                newList.filterNot { _comment -> _comment == comment }
                            )
                        }
                        is ApiStatus.Error -> {}
                        is ApiStatus.Loading -> {}
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun removePost() {
        viewModelScope.launch {
            try {
                _postDetails.value?.let {
                    (it as? ApiStatus.Success)?.let { state ->
                        val response = removePostUseCase(state.items)
                        _removePostState.value = SingleEvent(response)
                    } ?: {
                        _removePostState.value = SingleEvent(false)
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun reportPost() {
        viewModelScope.launch {
            try {
                _postDetails.value?.let {
                    (it as? ApiStatus.Success)?.let { state ->
                        val response = reportPostUseCase(state.items)
                        _reportPostState.value = SingleEvent(response)
                    } ?: {
                        _reportPostState.value = SingleEvent((false))
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun reportComment(comment: CommentUiState) {
        viewModelScope.launch {
            try {
                val response = reportCommentUseCase(comment)
                _reportCommentState.value = SingleEvent(response)
            } catch (_: Exception) {}
        }
    }
}