package com.example.project

data class FlowerClass(
    val id_flower: Int,
    val flower_name: String,
    val flower_image: String?,
    val color: String,
    val quantity: Int,
    val price: Double,
    val type: String,    // ✅ ต้องมีบรรทัดนี้
    val meaning: String? // ✅ ต้องมีบรรทัดนี้
)