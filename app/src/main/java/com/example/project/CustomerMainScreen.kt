package com.example.project

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun CustomerMainScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    var selectedItem by remember { mutableIntStateOf(0) }
    val userName = sharedPref.getSavedName()
    val mainColor = Color(0xFFD2B49C)

    LaunchedEffect(Unit) {
        viewModel.fetchAllFlowers()
    }

    Scaffold(
        topBar = {
            CustomerTopBar(
                contextForToast = context,
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onCartClick = { navController.navigate(Screen.Checkout.route) }
            )
        },
        bottomBar = {
            CustomerBottomBar(
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ยินดีต้อนรับคุณ: ${if (userName.isNotEmpty()) userName else "Customer"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5C6BC0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "ยินดีต้อนรับสู่ร้าน Flower Daily ของเรา", color = Color.Gray)
                    }
                }

                1 -> {
                    // --- หน้าเลือกพันธุ์ดอกไม้ (ปรับเป็น Grid 2 คอลัมน์ตามรูป) ---
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "แคตตาล็อกดอกไม้",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color(0xFF5D4037)
                        )

                        val flowerCategories = viewModel.flowerList.distinctBy { it.flower_name }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(flowerCategories) { flower ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clickable {
                                            // ✅ แก้ไข: คลิกแล้วไปหน้า "ความหมาย" (รูปที่ 2 ใน Flow)
                                            navController.navigate("flower_meaning/${flower.flower_name}")
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(flower.flower_image),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "ดอก${flower.flower_name}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "คำแนะนำ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = "เลือกดอกไม้ให้ถูกใจผู้รับด้วยคำแนะนำของเรา", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTopBar(contextForToast: Context, onProfileClick: () -> Unit, onCartClick: () -> Unit) {
    TopAppBar(
        title = { Text("Flower Daily", fontWeight = FontWeight.Bold, color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD2B49C)),
        actions = {
            IconButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingBasket, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
        }
    )
}

@Composable
fun CustomerBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("หน้าหลัก", "ดอกไม้", "แนะนำ")
    val icons = listOf(Icons.Default.Home, Icons.Filled.LocalFlorist, Icons.Filled.QuestionAnswer)

    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { index, item ->
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