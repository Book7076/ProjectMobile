package com.example.project

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRiderScreen(navController: NavHostController, viewModel: ProjectViewModel, email: String) {
    // ดึงข้อมูล Rider จาก List ใน ViewModel โดยหาจาก Email
    val rider = viewModel.riderList.find { it.email == email }
    val context = LocalContext.current
    val mainColor = Color(0xFFD2B49C)

    // ตรวจสอบว่าถ้าหาข้อมูลไม่เจอ ให้กลับไปหน้าเดิม
    if (rider == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "ไม่พบข้อมูล Rider: $email", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    // State สำหรับเก็บค่าที่แก้ไข
    var name by remember { mutableStateOf(rider.name ?: "") }
    var phone by remember { mutableStateOf(rider.phone ?: "") }

    // ✅ แก้ปัญหา Error: ถ้าใน LoginClass ไม่มี gender ให้ใส่ค่าเริ่มต้นเป็น "Male" ไว้ก่อน
    // หรือถ้ามีแต่ชื่อต่างกัน ให้เปลี่ยน rider.gender เป็นชื่อตามใน Model ครับ
    var selectedGender by remember { mutableStateOf("Male") }

    var password by remember { mutableStateOf("") } // รหัสผ่านใหม่ (ถ้าจะเปลี่ยน)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("แก้ไขข้อมูล Rider", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("ข้อมูลพื้นฐาน (Email: $email)", fontWeight = FontWeight.Bold, color = Color.Gray)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("ชื่อ-นามสกุล") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("เบอร์โทรศัพท์") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("รหัสผ่านใหม่ (ว่างไว้ถ้าไม่เปลี่ยน)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("เพศ:", fontWeight = FontWeight.Bold)
            Row {
                listOf("Male", "Female").forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (selectedGender == gender),
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = mainColor)
                        )
                        Text(gender)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // ✅ เรียกใช้งานฟังก์ชัน updateRider ใน ViewModel ที่เราสร้างไว้
                    viewModel.updateRider(context, email, name, phone, selectedGender, password) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotEmpty() && phone.isNotEmpty()
            ) {
                Text("บันทึกการแก้ไข", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}