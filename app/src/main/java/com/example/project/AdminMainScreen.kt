package com.example.project

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun AdminMainScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current

    val adminEmail = viewModel.loginResult?.email ?: "Admin"

    Scaffold(
        topBar = {
            AdminTopBar(
                contextForToast = context,
                onProfileClick = {
                    // ใช้งาน navController: กดแล้วให้กลับไปหน้า Login (Logout)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // ล้างหน้าทั้งหมดใน Stack
                    }
                }
            )
        },
        bottomBar = {
            AdminBottomBar()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ยินดีต้อนรับคุณ: $adminEmail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "นี่คือเนื้อหาของหน้าหลักสำหรับผู้ดูแลระบบ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(contextForToast: Context, onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Flower Daily (Admin)",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // แก้ไขจาก 0x00 เป็น 0xFF เพื่อให้สีน้ำตาลแสดงผล
            containerColor = Color(0xFFD2B49C)
        ),
        actions = {
            IconButton(onClick = {
                Toast.makeText(contextForToast, "จัดการออเดอร์", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    imageVector = Icons.Default.ShoppingBasket,
                    contentDescription = "Orders",
                    tint = Color.White
                )
            }
            IconButton(onClick = onProfileClick) { // เรียกใช้ฟังก์ชันที่ส่งมาจากด้านบน
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun AdminBottomBar() {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("หน้าหลัก", "จัดการดอกไม้", "แชทลูกค้า")
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
                selected = selectedItem == index,
                onClick = { selectedItem = index },
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