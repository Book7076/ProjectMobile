package com.example.project

// มองหาคลาสที่ใช้รับค่าจาก API placeOrder
data class OrderResponse(
    val error: Boolean,
    val message: String,
    val id_order: Int? = null
)
