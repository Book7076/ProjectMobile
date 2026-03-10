package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentMethodStep(onMethodSelected: (String) -> Unit) {
    val darkBrown = Color(0xFF6D4C41)
    var selectedType by remember { mutableStateOf("") }
    // ตัด isLoading ออกได้เลยเพราะ QR ปกติจะแค่เปลี่ยนหน้า ไม่ต้องรอยิง API ทันทีแบบ COD

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "เลือกวิธีชำระเงิน",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ เหลือแค่รายการเดียวคือ QR Code
        listOf("QR" to "โอนผ่าน QR Code").forEach { (type, label) ->
            OutlinedButton(
                onClick = {
                    selectedType = type
                    onMethodSelected(type)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = darkBrown,
                    containerColor = if (selectedType == type) darkBrown.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}