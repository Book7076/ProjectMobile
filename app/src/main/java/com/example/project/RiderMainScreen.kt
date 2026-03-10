package com.example.project

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun RiderMainScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    var selectedItem by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.fetchAdminOrders()
    }

    Scaffold(
        topBar = {
            // เอาปุ่ม Profile ออกจาก TopBar เพราะย้ายไป BottomBar แล้ว
            RiderSimpleTopBar()
        },
        bottomBar = { RiderBottomBar(selectedItem) { selectedItem = it } }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> {
                    val deliveryOrders = viewModel.adminOrderList.filter { it.status == "shipping" }
                    RiderOrderList(deliveryOrders, viewModel)
                }
                1 -> {
                    // เรียกหน้า Profile ที่สร้างใหม่
                    RiderProfileScreen(navController)
                }
            }
        }
    }
}

@Composable
fun RiderOrderList(orders: List<AdminOrderClass>, viewModel: ProjectViewModel) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ไม่มีงานส่งดอกไม้ในขณะนี้", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                RiderJobCard(order = order, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RiderJobCard(order: AdminOrderClass, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val riderId = sharedPref.getSavedIdUser()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📦 ออเดอร์: ${order.product_name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("📍 ที่อยู่: ${order.address_detail}", fontSize = 14.sp, color = Color.DarkGray)
            Text("📞 ผู้รับ: ${order.receiver_name} (${order.phone})", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = if(imageUri == null) Color.Gray else Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (imageUri == null) "📷 เลือกรูปหลักฐานการส่ง" else "✅ เลือกรูปสำเร็จ")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                enabled = imageUri != null,
                onClick = {
                    imageUri?.let { uri ->
                        viewModel.completeRiderJob(
                            context = context,
                            idOrder = order.id_order,
                            idRider = riderId,
                            imageUri = uri,
                            onSuccess = {
                                Toast.makeText(context, "ส่งงานสำเร็จ!", Toast.LENGTH_SHORT).show()
                                viewModel.fetchAdminOrders()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
            ) {
                Text("ยืนยันการจัดส่งถึงมือผู้รับ", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderSimpleTopBar() {
    TopAppBar(
        title = {
            Text("Flower Daily - Rider", fontWeight = FontWeight.Bold, color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD2B49C))
    )
}

@Composable
fun RiderBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    // ปรับให้เหลือแค่ 2 เมนู
    val items = listOf("งานส่ง", "โปรไฟล์")
    val icons = listOf(Icons.Default.Home, Icons.Default.Person)

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFFD2B49C)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color(0xFFD2B49C),
                    indicatorColor = Color(0xFFD2B49C)
                )
            )
        }
    }
}