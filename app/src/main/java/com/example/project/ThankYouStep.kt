package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThankYouStep(onBackHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("สั่งซื้อสำเร็จ!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))
        Text("ขอบคุณที่ใช้บริการ Flower Daily", color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBackHome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD2B49C)),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.width(200.dp)
        ) { Text("กลับหน้าหลัก", color = Color.White) }
    }
}