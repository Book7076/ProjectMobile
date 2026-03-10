package com.example.project

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminOrderScreen(viewModel: ProjectViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("รอตรวจสอบ", "ดำเนินการ", "กำลังจัดส่ง", "เสร็จสิ้น")
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchAdminOrders()
    }

    val statusFiltered = when (selectedTabIndex) {
        0 -> viewModel.adminOrderList.filter { it.status == "pending" || it.status == "pending_verification" }
        1 -> viewModel.adminOrderList.filter { it.status == "paid" || it.status == "preparing" }
        2 -> viewModel.adminOrderList.filter { it.status == "shipping" }
        else -> viewModel.adminOrderList.filter { it.status == "completed" }
    }

    val finalFiltered = statusFiltered.filter {
        it.customer_name.contains(searchQuery, ignoreCase = true) ||
                it.product_name.contains(searchQuery, ignoreCase = true)
    }

    val groupedOrders = finalFiltered.groupBy { it.order_date?.split("T")?.get(0) ?: "ไม่ระบุวันที่" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "การจัดการคำสั่งซื้อ",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D4C41),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            placeholder = { Text("ค้นหาชื่อลูกค้า หรือสินค้า...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFFF5F5F5),
            contentColor = Color(0xFF6D4C41),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFF6D4C41)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (groupedOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("ไม่พบรายการที่ตรงเงื่อนไข", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                groupedOrders.forEach { (date, ordersInDate) ->
                    item {
                        Text(
                            text = "วันที่: $date",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFFD2B49C), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    items(ordersInDate) { order ->
                        AdminOrderCard(order = order) { nextStatus ->
                            viewModel.updateOrderStatus(order.id_order, nextStatus) {
                                Toast.makeText(context, "อัปเดตเรียบร้อย", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
} // 👈 ปีกกาปิด AdminOrderScreen

@Composable
fun AdminOrderCard(order: AdminOrderClass, onUpdateStatus: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7CCC8).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("💐", fontSize = 32.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = order.product_name, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Text(
                        text = "ผู้สั่งซื้อ: ${order.customer_name}",
                        fontSize = 13.sp,
                        color = Color(0xFF6D4C41),
                        fontWeight = FontWeight.Medium
                    )

                    // ✅ แสดงชื่อ Rider เฉพาะตอนสถานะ "completed" (ส่งงานเสร็จสิ้น)
                    if (order.status == "completed") {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ผู้ส่งสำเร็จ: ${order.rider_name ?: "ไม่ระบุชื่อ"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(text = "ผู้รับ: ${order.receiver_name}", fontSize = 14.sp)
                    Text(text = "โทร: ${order.phone}", fontSize = 13.sp)
                    Text(
                        text = order.address_detail,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val (buttonText, nextStatus) = when(order.status) {
                    "pending_verification" -> "ยืนยันการโอนเงิน" to "paid"
                    "paid" -> "เริ่มเตรียมสินค้า" to "preparing"
                    "preparing" -> "ส่งต่อให้ Rider" to "shipping"
                    "shipping" -> "จัดส่งสำเร็จ" to "completed"
                    else -> "" to ""
                }

                if (buttonText.isNotEmpty()) {
                    Button(
                        onClick = { onUpdateStatus(nextStatus) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(buttonText, fontSize = 12.sp, color = Color.White)
                    }
                } else {
                    Text("รายการสำเร็จแล้ว ✅", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}