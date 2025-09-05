package com.naveen.sideeffectscoroutine.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.naveen.sideeffectscoroutine.data.PostRepository
import com.naveen.sideeffectscoroutine.worker.PostCoroutineWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed interface PostUiState {
    data object Idle : PostUiState
    data object Loading : PostUiState
    data class Success(val id: Int) : PostUiState
    data class Error(val message: String) : PostUiState
}

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PostRepository()
    private val _state = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val state: StateFlow<PostUiState> = _state.asStateFlow()

    fun createOnce(title: String, body: String, userId: Int) {
        _state.value = PostUiState.Loading
        viewModelScope.launch {
            try {
                val response = repo.createDemoPost(title, body, userId)
                // Ensure we handle the nullable ID properly
                val postId = response.id ?: -1 // Use -1 as fallback for null ID
                _state.value = PostUiState.Success(postId)
                Log.d("PostViewModel", "Post created with ID: $postId")
                Log.d("PostViewModel", "Full response: $response")
            } catch (t: Throwable) {
                _state.value = PostUiState.Error(t.message ?: "Unknown error")
                Log.e("PostViewModel", "Error creating post", t)
            }
        }
    }

    fun schedulePeriodicPost(intervalMinutes: Long = 15) {
        val work = PeriodicWorkRequestBuilder<PostCoroutineWorker>(intervalMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            PostCoroutineWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}



