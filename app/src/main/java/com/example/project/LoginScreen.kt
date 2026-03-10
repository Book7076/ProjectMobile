package com.example.project

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)

    // 定义สีตามตัวอย่าง
    val primaryColor = Color(0xFFC4A792) // สีเบจ-น้ำตาลอ่อน ของปุ่ม
    val inputBgColor = Color(0xFFE0E0E0) // สีเทาอ่อน ของช่องกรอกข้อมูล
    val textColor = Color(0xFF424242) // สีเทาเข้ม ของตัวหนังสือ

    var email by rememberSaveable { mutableStateOf(sharedPref.getSavedEmail()) }
    var password by rememberSaveable { mutableStateOf("") }
    val loginResult = viewModel.loginResult

    LaunchedEffect(key1 = loginResult) {
        loginResult?.let {
            if (!it.error) {
                val role = it.role?.lowercase() ?: ""
                sharedPref.saveLoginStatus(
                    isLoggedIn = true,
                    email = it.email ?: "",
                    role = role,
                    name = it.name ?: "Unknown",
                    id_user = it.id_user ?: 0
                )
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                val destination = when (role) {
                    "admin" -> Screen.AdminScreen.route
                    "rider" -> Screen.RiderScreen.route
                    else -> Screen.CustomerScreen.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                viewModel.resetLoginResult()
            } else {
                Toast.makeText(context, it.message ?: "Login Failed", Toast.LENGTH_SHORT).show()
                viewModel.resetLoginResult()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- ส่วนที่ 1: รูปดอกลิลลี่ (แก้ไขชื่อไฟล์ ic_lily_flower ให้ตรงใน Drawable) ---
        Image(
            painter = painterResource(id = R.drawable.flowerlogin),
            contentDescription = "Lily Flower",
            modifier = Modifier
                .size(width = 280.dp, height = 280.dp) // ปรับขนาดตามความเหมาะสม
                .padding(bottom = 64.dp) // เว้นระยะห่างด้านล่าง
        )

        // --- ส่วนที่ 2: ช่องกรอกอีเมล ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "อีเมล", color = textColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent, // ซ่อนเส้นขอบเมื่อกด
                    unfocusedBorderColor = Color.Transparent, // ซ่อนเส้นขอบปกติ
                    focusedContainerColor = inputBgColor, // สีพื้นหลังเมื่อกด
                    unfocusedContainerColor = inputBgColor, // สีพื้นหลังปกติ
                    cursorColor = textColor
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ส่วนที่ 3: ช่องกรอกรหัสผ่าน ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "รหัสผ่าน", color = textColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    cursorColor = textColor
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- ส่วนที่ 4: ปุ่มเข้าสู่ระบบ สีเบจ ---
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .size(width = 240.dp, height = 56.dp), // ปรับขนาดปุ่มตามตัวอย่าง
            shape = RoundedCornerShape(16.dp), // ขอบมนน้อยลงตามตัวอย่าง
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor, // สีเบจ
                contentColor = Color.White, // สีตัวหนังสือขาว
                disabledContainerColor = primaryColor.copy(alpha = 0.5f) // สีเมื่อปุ่มปิดใช้งาน
            )
        ) {
            Text(text = "เข้าสู่ระบบ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- ส่วนที่ 5: ลิงก์สมัครสมาชิก ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ยังไม่มีบัญชี? ", color = textColor, fontSize = 14.sp)
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.padding(start = 0.dp)
            ) {
                Text(text = "สมัครสมาชิก", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}