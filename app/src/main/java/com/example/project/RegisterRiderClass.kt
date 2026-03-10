package com.example.project

import com.google.gson.annotations.SerializedName

data class RegisterRiderClass(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("role") val role: String = "rider"
)