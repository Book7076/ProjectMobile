package com.example.project

data class CartClass(
    val id_cart: Int,
    val id_user: Int,
    val product_name: String,
    val price: Double,
    var quantity: Int,
    val image_url: String?,
    var isSelected: Boolean = true
)