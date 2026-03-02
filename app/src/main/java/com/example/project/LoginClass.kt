package com.example.project

import com.google.gson.annotations.SerializedName

data class LoginClass(
    @SerializedName(value = "error") val error: Boolean,
    @SerializedName(value = "message") val message: String?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "role") val role: String?
)

