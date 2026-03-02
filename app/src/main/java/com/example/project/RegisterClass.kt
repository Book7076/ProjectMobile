package com.example.project

import com.google.gson.annotations.SerializedName

data class RegisterClass(
    @SerializedName(value = "email") val email: String,
    @SerializedName(value = "password") val password: String,
    @SerializedName(value = "name") val name: String,
    @SerializedName(value = "phone") val phone: String,
    @SerializedName(value = "gender") val gender: String,
    @SerializedName(value = "role") val role: String = "customer"
)

