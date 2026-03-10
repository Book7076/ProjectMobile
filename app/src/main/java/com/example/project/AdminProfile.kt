package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun AdminProfileScreen(navController: NavHostController) {
    val themeColor = Color(0xFFD2B49C)
    val backgroundColor = Color(0xFFFDFDFD)
    val darkBrown = Color(0xFF5D4037)

    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val adminName = sharedPref.getSavedName().ifEmpty { "ผู้ดูแลระบบ" }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Flower Daily", color = Color.White, fontWeight = FontWeight.Medium) },
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
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = themeColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "คุณ $adminName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown
            )
            Text(
                text = "สถานะ: ผู้ดูแลระบบ (Admin)",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(56.dp))

            // ส่วนปุ่ม Logout
            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clickable {
                            // เคลียร์ข้อมูล Login และกลับไปหน้า Login
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // ปรับข้อความให้อยู่กลางปุ่ม
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFE57373)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "ออกจากระบบ",
                            color = Color(0xFFE57373),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}