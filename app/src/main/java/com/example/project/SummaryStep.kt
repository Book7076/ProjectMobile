package com.example.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryStep(viewModel: ProjectViewModel, selectedAddressId: Int, onConfirm: () -> Unit) {
    // ดึงสินค้าที่เลือกจากตะกร้า
    val cartItems = viewModel.cartList.filter { it.isSelected }
    // ค้นหาที่อยู่ที่ผู้ใช้เลือก
    val selectedAddress = viewModel.addressList.find { it.id == selectedAddressId }
    // คำนวณราคาทั้งหมด
    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    val darkBrown = Color(0xFF6D4C41)

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("สรุปรายการสั่งซื้อ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBrown)

        Spacer(modifier = Modifier.height(12.dp))

        // --- ส่วนรายการสินค้า ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF8F5))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                cartItems.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.product_name} x${item.quantity}")
                        Text("${item.price * item.quantity} บาท")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ยอดรวมสุทธิ:", fontWeight = FontWeight.Bold)
                    Text("$totalPrice บาท", fontWeight = FontWeight.Bold, color = Color(0xFFE57373))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ส่วนที่อยู่จัดส่ง ---
        Text("ที่อยู่จัดส่ง", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = darkBrown)
        if (selectedAddress != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = darkBrown, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedAddress.receiver_name, fontWeight = FontWeight.Bold)
                    }
                    Text("โทร: ${selectedAddress.phone}", fontSize = 14.sp, color = Color.Gray)
                    Text("${selectedAddress.address_detail} ${selectedAddress.post_code}", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = cartItems.isNotEmpty() && selectedAddress != null,
            colors = ButtonDefaults.buttonColors(containerColor = darkBrown),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("ยืนยันรายการและไปชำระเงิน", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}