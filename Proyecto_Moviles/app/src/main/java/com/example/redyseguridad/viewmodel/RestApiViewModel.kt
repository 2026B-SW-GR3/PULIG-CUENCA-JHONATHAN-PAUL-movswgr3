package com.example.redyseguridad.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redyseguridad.api.RetrofitClient
import com.example.redyseguridad.model.Post
import kotlinx.coroutines.launch

class RestApiViewModel : ViewModel() {

    private val apiService = RetrofitClient.jsonPlaceholderService

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getPostById(postId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = apiService.getPost(postId)
                _post.value = result
            } catch (e: Exception) {
                _error.value = "Error al obtener el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllPosts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = apiService.getAllPosts()
                _posts.value = result
            } catch (e: Exception) {
                _error.value = "Error al obtener posts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPost(title: String, body: String, userId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val newPost = Post(userId = userId, id = 0, title = title, body = body)
                val result = apiService.createPost(newPost)
                _post.value = result
            } catch (e: Exception) {
                _error.value = "Error al crear el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePost(postId: Int, title: String, body: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val updatedPost = Post(userId = 1, id = postId, title = title, body = body)
                val result = apiService.updatePost(postId, updatedPost)
                _post.value = result
            } catch (e: Exception) {
                _error.value = "Error al actualizar el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                apiService.deletePost(postId)
                _post.value = null
                _error.value = "Post eliminado correctamente"
            } catch (e: Exception) {
                _error.value = "Error al eliminar el post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
