package com.example.project

data class AdminOrderClass(
    val id_order: Int,
    val product_name: String,
    val price: Double,
    val status: String,
    val order_date: String,
    val customer_name: String,
    val receiver_name: String,
    val address_detail: String,
    val phone: String,
    val rider_name: String? = null,
    val slip_image: String? = null
)
