package com.example.project

import com.google.gson.annotations.SerializedName

data class ProfileClass(
    @SerializedName(value = "error") val error: Boolean,
    @SerializedName(value = "message") val message: String?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "name") val name: String?,
    @SerializedName(value = "std_gender") val std_gender: String?,
    @SerializedName(value = "role") val role: String?
)
