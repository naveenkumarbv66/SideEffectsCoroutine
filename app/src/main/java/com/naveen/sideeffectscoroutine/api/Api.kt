package com.naveen.sideeffectscoroutine.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class DemoPostRequest(
    val title: String,
    val body: String,
    val userId: Int
)

data class DemoPostResponse(
    val id: Int?,
    val title: String?,
    val body: String?,
    val userId: Int?
)

interface DemoApiService {
    @POST("posts")
    suspend fun createPost(@Body payload: DemoPostRequest): DemoPostResponse
}

object ApiProvider {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val demoService: DemoApiService = retrofit.create(DemoApiService::class.java)
}



