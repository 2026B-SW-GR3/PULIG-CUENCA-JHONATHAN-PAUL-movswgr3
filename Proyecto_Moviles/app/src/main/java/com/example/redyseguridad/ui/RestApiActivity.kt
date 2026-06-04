package com.example.redyseguridad.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redyseguridad.R
import com.example.redyseguridad.databinding.ActivityRestApiBinding
import com.example.redyseguridad.viewmodel.RestApiViewModel

class RestApiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestApiBinding
    private val viewModel: RestApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rest_api)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnGetPost.setOnClickListener {
            val postId = binding.etPostId.text.toString().toIntOrNull() ?: 1
            viewModel.getPostById(postId)
        }

        binding.btnGetAllPosts.setOnClickListener {
            viewModel.getAllPosts()
        }

        binding.btnCreatePost.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val body = binding.etBody.text.toString()
            if (title.isNotEmpty() && body.isNotEmpty()) {
                viewModel.createPost(title, body, 1)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdatePost.setOnClickListener {
            val postId = binding.etPostId.text.toString().toIntOrNull() ?: 1
            val title = binding.etTitle.text.toString()
            val body = binding.etBody.text.toString()
            if (title.isNotEmpty() && body.isNotEmpty()) {
                viewModel.updatePost(postId, title, body)
            }
        }

        binding.btnDeletePost.setOnClickListener {
            val postId = binding.etPostId.text.toString().toIntOrNull() ?: 1
            viewModel.deletePost(postId)
        }
    }

    private fun observeViewModel() {
        viewModel.post.observe(this) { post ->
            if (post != null) {
                binding.tvPostContent.text = "ID: ${post.id}\nTítulo: ${post.title}\n\nCuerpo: ${post.body}"
            }
        }

        viewModel.posts.observe(this) { posts ->
            binding.tvPostContent.text = "Posts cargados: ${posts.size} posts\n\nPrimero:\n${posts.firstOrNull()?.title ?: "Sin posts"}"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = android.view.View.VISIBLE
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            } else {
                binding.tvError.visibility = android.view.View.GONE
            }
        }
    }
}
