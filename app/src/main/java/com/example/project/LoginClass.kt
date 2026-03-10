package com.example.project

import com.google.gson.annotations.SerializedName

data class LoginClass(
    @SerializedName(value = "error") val error: Boolean,
    @SerializedName(value = "message") val message: String?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "role") val role: String?,
    @SerializedName(value = "name") val name: String?,
    @SerializedName(value = "id_user") val id_user: Int?,
    @SerializedName(value = "phone") val phone: String?
)