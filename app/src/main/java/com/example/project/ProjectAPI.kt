package com.example.project

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProjectAPI {
    // Register
    @POST(value = "register")
    suspend fun registerCustomer(
        @Body studentData: RegisterClass
    ): Response<RegisterResponse>

    // Login
    @POST(value = "login")
    suspend fun login(
        @Body loginData: Map<String, String>
    ): Response<LoginClass>

    // Search/Profile
    @GET(value = "search/{id}")
    suspend fun getStudentProfile(
        @Path(value = "id") id: String
    ): Response<ProfileClass>
}