package com.naveen.sideeffectscoroutine.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.naveen.sideeffectscoroutine.data.PostRepository

class PostCoroutineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val UNIQUE_WORK_NAME = "periodic_post_work"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d("PostCoroutineWorker", "Starting periodic post creation")
            
            val repository = PostRepository()
            val response = repository.createDemoPost(
                title = "Periodic Post ${System.currentTimeMillis()}",
                body = "This is a periodic post created at ${System.currentTimeMillis()}",
                userId = 1
            )
            
            Log.d("PostCoroutineWorker", "Periodic post created with ID: ${response.id}")
            Result.success()
        } catch (e: Exception) {
            Log.e("PostCoroutineWorker", "Failed to create periodic post", e)
            Result.failure()
        }
    }
}
