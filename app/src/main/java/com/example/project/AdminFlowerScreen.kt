package com.example.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlowerScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val mainColor = Color(0xFFD2B49C)

    // โหลดข้อมูลเมื่อเปิดหน้า
    LaunchedEffect(Unit) {
        viewModel.fetchAllFlowers()
    }

    // กรองเอาเฉพาะชื่อพันธุ์ที่ไม่ซ้ำกันเพื่อแสดงในหน้าแรก
    val flowerCategories = viewModel.flowerList.distinctBy { it.flower_name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("คลังสินค้า (เลือกพันธุ์)", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AdminAddFlower.route) },
                containerColor = mainColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { padding ->
        if (flowerCategories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("ยังไม่มีข้อมูลในคลังสินค้า", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flowerCategories) { flower ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // เมื่อคลิกพันธุ์นี้ จะส่งชื่อพันธุ์ไปหน้าจัดการสี
                                navController.navigate("admin_flower_detail/${flower.flower_name}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocalFlorist, contentDescription = null, tint = mainColor)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ดอก${flower.flower_name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                // นับจำนวนสีที่มีในพันธุ์นี้
                                val colorCount = viewModel.flowerList.count { it.flower_name == flower.flower_name }
                                Text(text = "มีทั้งหมด $colorCount สีในคลัง", fontSize = 14.sp, color = Color.Gray)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}