package com.naveen.sideeffectscoroutine.data

import com.naveen.sideeffectscoroutine.api.ApiProvider
import com.naveen.sideeffectscoroutine.api.DemoPostRequest
import com.naveen.sideeffectscoroutine.api.DemoPostResponse

class PostRepository {
    private val api = ApiProvider.demoService

    suspend fun createDemoPost(title: String, body: String, userId: Int): DemoPostResponse {
        return api.createPost(DemoPostRequest(title = title, body = body, userId = userId))
    }
}



