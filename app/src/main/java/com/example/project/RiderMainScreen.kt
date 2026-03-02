package com.example.project

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // สร้าง State สำหรับเก็บว่าตอนนี้อยู่ที่เมนูไหน
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            // เพิ่มการกด Profile เพื่อ Logout หรือไปหน้าอื่น
            RiderTopBar(
                contextForToast = context,
                onProfileClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        },
        bottomBar = {
            // ส่งค่าเข้าไปจัดการ Bottom Bar
            RiderBottomBar(
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // แสดงเนื้อหาตามปุ่มที่กด (ตัวอย่างการใช้งาน viewModel)
            when (selectedItem) {
                0 -> Text(text = "ยินดีต้อนรับสู่หน้าหลัก", modifier = Modifier.padding(16.dp))
                1 -> Text(text = "รายการดอกไม้ตามฤดูกาล", modifier = Modifier.padding(16.dp))
                2 -> Text(text = "คำแนะนำช่อดอกไม้พิเศษ", modifier = Modifier.padding(16.dp))
            }

            // แสดงชื่อผู้ใช้จาก viewModel (เพื่อให้ viewModel ถูกใช้งาน)
            val userEmail = viewModel.loginResult?.email ?: "Rider"
            Text(text = "ผู้ใช้งาน: $userEmail", modifier = Modifier.padding(16.dp), color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderTopBar(contextForToast: Context, onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Flower Daily",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFD2B49C) // เปลี่ยนเป็น FF เพื่อให้เห็นสี
        ),
        actions = {
            IconButton(onClick = {
                Toast.makeText(contextForToast, "ตะกร้าสินค้าของคุณ", Toast.LENGTH_SHORT).show()
            }) {
                Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = onProfileClick) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
        }
    )
}

@Composable
fun RiderBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("หน้าหลัก", "ดอกไม้", "แนะนำ")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Filled.LocalFlorist,
        Icons.Filled.QuestionAnswer
    )

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
                    indicatorColor = Color(0xFFD2B49C),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}