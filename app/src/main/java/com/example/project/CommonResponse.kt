package com.example.project

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName(value = "error") val error: Boolean,
    @SerializedName(value = "message") val message: String
)