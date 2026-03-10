package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavHostController,
    viewModel: ProjectViewModel,
    initialStatus: String? = "all" // ✅ รับค่าสถานะเริ่มต้น (pending, processing, shipping, completed, all)
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val userId = sharedPref.getSavedIdUser()
    val themeColor = Color(0xFFD2B49C)

    // โหลดข้อมูลเมื่อเปิดหน้าจอ
    LaunchedEffect(Unit) {
        if (userId != 0) {
            viewModel.fetchCustomerOrders(userId)
        }
    }

    // ✅ กรองข้อมูลตามสถานะที่กดมาจากหน้า Profile (เพิ่ม completed)
    val filteredOrders = when (initialStatus) {
        "pending" -> viewModel.customerOrderList.filter {
            it.status == "pending" || it.status == "pending_verification"
        }
        "processing" -> viewModel.customerOrderList.filter {
            it.status == "paid" || it.status == "preparing"
        }
        "shipping" -> viewModel.customerOrderList.filter {
            it.status == "shipping"
        }
        "completed" -> viewModel.customerOrderList.filter {
            it.status == "completed"
        }
        else -> viewModel.customerOrderList // กรณี "all" หรืออื่นๆ แสดงทั้งหมด
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText = when(initialStatus) {
                        "pending" -> "รอการตรวจสอบ"
                        "processing" -> "กำลังดำเนินการ"
                        "shipping" -> "ที่ต้องได้รับ"
                        "completed" -> "รายการที่เสร็จสิ้น"
                        else -> "ประวัติการสั่งซื้อ"
                    }
                    Text(titleText, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = themeColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDFDFD))
        ) {
            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (viewModel.customerOrderList.isEmpty()) "ยังไม่มีประวัติการสั่งซื้อ"
                        else "ไม่มีรายการในหมวดหมู่นี้",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        HistoryCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(order: AdminOrderClass) {
    val statusColor = when (order.status) {
        "completed" -> Color(0xFF4CAF50)
        "pending", "pending_verification" -> Color(0xFFFF9800)
        "shipping" -> Color(0xFF2196F3)
        "paid", "preparing" -> Color(0xFF8D6E63)
        else -> Color.Gray
    }

    val statusText = when (order.status) {
        "pending" -> "รอชำระเงิน"
        "pending_verification" -> "รอตรวจสอบ"
        "paid", "preparing" -> "กำลังเตรียมสินค้า"
        "shipping" -> "อยู่ระหว่างจัดส่ง"
        "completed" -> "สำเร็จแล้ว ✅"
        else -> order.status
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // จัดการเรื่องวันที่เพื่อป้องกัน error
                val dateText = order.order_date?.split("T")?.get(0) ?: "ไม่ระบุวันที่"
                Text(text = "วันที่: $dateText", fontSize = 12.sp, color = Color.Gray)
                Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = order.product_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ราคารวม:", fontSize = 14.sp)
                Text(text = "${order.price} บาท", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
            }
        }
    }
}