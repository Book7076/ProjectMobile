package com.example.project

data class OrderRequest(
    val id_user: Int,
    val id_address: Int,
    val product_name: String,
    val price: Double
)