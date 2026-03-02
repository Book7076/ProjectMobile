package com.example.project

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProjectClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val projectAPI: ProjectAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProjectAPI::class.java)
    }
}