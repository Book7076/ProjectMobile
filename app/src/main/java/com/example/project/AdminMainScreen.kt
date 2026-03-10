package com.example.project

import android.content.Context
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

@Composable
fun AdminMainScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val mainColor = Color(0xFFD2B49C)

    // ✅ ปรับเริ่มต้นที่ 0 แต่ให้ 0 เป็นหน้าจัดการดอกไม้แทน
    var selectedItem by remember { mutableIntStateOf(0) }

    // โหลดข้อมูลดอกไม้ทันทีเมื่อเข้าหน้า
    LaunchedEffect(Unit) {
        viewModel.fetchAllFlowers()
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                contextForToast = context,
                onProfileClick = {
                    navController.navigate(Screen.AdminProfileScreen.route)
                }
            )
        },
        bottomBar = {
            AdminBottomBar(selectedItem) { selectedItem = it }
        },
        floatingActionButton = {
            // ✅ แสดงปุ่มเพิ่มดอกไม้เฉพาะเมื่ออยู่ที่หน้าจัดการดอกไม้ (Index 0)
            if (selectedItem == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AdminAddFlower.route) },
                    containerColor = mainColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> {
                    // ✅ หน้าจัดการดอกไม้ (เดิมคือเคส 1)
                    AdminFlowerListContent(navController, viewModel)
                }
                1 -> {
                    // ✅ หน้าสถานะออเดอร์ (เดิมคือเคส 2)
                    AdminOrderScreen(viewModel)
                }
                2 -> {
                    // ✅ หน้าจัดการระบบ
                    val lowStockFlowers = viewModel.flowerList
                        .distinctBy { it.flower_name }
                        .filter { flower ->
                            val totalQuantity = viewModel.flowerList
                                .filter { it.flower_name == flower.flower_name }
                                .sumOf { it.quantity }
                            totalQuantity < 10
                        }

                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Text(text = "จัดการระบบ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))

                        // ปุ่มจัดการ Rider เดิม
                        Button(
                            onClick = { navController.navigate(Screen.AdminRiderManagement.route) },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.DirectionsBike, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("จัดการข้อมูล Rider")
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- ส่วนแจ้งเตือนสินค้าใกล้หมด (ย้ายมาไว้ตรงนี้) ---
                        Text(text = "การแจ้งเตือนสต็อก", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (lowStockFlowers.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFFFEBEE), // สีแดงอ่อน
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "สินค้าใกล้หมดคลัง (ต่ำกว่า 10)",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    lowStockFlowers.forEach { flower ->
                                        val total = viewModel.flowerList
                                            .filter { it.flower_name == flower.flower_name }
                                            .sumOf { it.quantity }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "• ดอก${flower.flower_name}", color = Color(0xFFB71C1C))
                                            Text(
                                                text = "คงเหลือ $total ดอก",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // กรณีสินค้าเพียงพอทั้งหมด
                            Surface(
                                color = Color(0xFFE8F5E9), // สีเขียวอ่อน
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("สินค้าทุกรายการมีสต็อกเพียงพอ", color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFlowerListContent(navController: NavHostController, viewModel: ProjectViewModel) {
    val flowerCategories = viewModel.flowerList.distinctBy { it.flower_name }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "คลังสินค้าดอกไม้", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (flowerCategories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("ยังไม่มีข้อมูลในคลังสินค้า", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(flowerCategories) { flower ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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
                            Icon(Icons.Default.LocalFlorist, contentDescription = null, tint = Color(0xFFD2B49C))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ดอก${flower.flower_name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
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

@Composable
fun AdminBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    // ✅ ปรับลำดับปุ่มใหม่
    val itemsList = listOf("ดอกไม้", "ออเดอร์", "จัดการ")
    val icons = listOf(Icons.Default.LocalFlorist, Icons.Filled.Assignment, Icons.Default.Settings)

    NavigationBar(containerColor = Color.White) {
        itemsList.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color(0xFFD2B49C),
                    selectedTextColor = Color(0xFFD2B49C)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(contextForToast: Context, onProfileClick: () -> Unit) {
    TopAppBar(
        title = { Text("Flower Daily (Admin)", fontWeight = FontWeight.Bold, color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD2B49C)),
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
        }
    )
}