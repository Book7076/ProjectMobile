package com.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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

@Composable
fun RiderProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val riderName = sharedPref.getSavedName()
    val riderRole = "Rider Partner" // หรือดึงจาก sharedPref ถ้ามี

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Profile Image Icon
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFFD2B49C).copy(alpha = 0.2f)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp).padding(20.dp),
                tint = Color(0xFF6D4C41)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (riderName.isNotEmpty()) riderName else "Rider Name",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
        )

        Text(
            text = riderRole,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Logout Button
        Button(
            onClick = {
                // เคลียร์ค่าใน SharedPref (ถ้ามีฟังก์ชัน logout)
                // sharedPref.clearData()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) // ล้าง stack ทั้งหมดเพื่อไม่ให้กดย้อนกลับมาได้
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ออกจากระบบ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}