package com.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    // สถานะสำหรับเก็บข้อมูลที่ผู้ใช้กรอก
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female", "Other")
    var cusGender by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )



        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Text(text = "Select Gender:", modifier = Modifier.align(Alignment.Start))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            genderOptions.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (cusGender == option),
                        onClick = { cusGender = option }
                    )
                    Text(text = option, modifier = Modifier.padding(end = 16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ปุ่ม Register
        Button(
            onClick = {
                // สร้าง Object จากข้อมูลที่กรอก
                val newStudent = RegisterClass(
                    email = email,
                    password = password,
                    name = name,
                    phone = phone,
                    gender = cusGender
                )
                // เรียกใช้ ViewModel เพื่อสมัครสมาชิก
                viewModel.register(context, newStudent) {
                    // เมื่อสำเร็จ ให้กลับไปหน้า Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            // ปุ่มจะกดได้ก็ต่อเมื่อกรอกข้อมูลครบทุกช่อง
            enabled = name.isNotEmpty() && password.isNotEmpty() &&
                    email.isNotEmpty() && phone.isNotEmpty()
        ) {
            Text(text = "Register", fontSize = 18.sp)
        }

        // ปุ่มย้อนกลับไปหน้า Login
        TextButton(onClick = { navController.popBackStack() }) {
            Text(text = "Already have an account? Log In")
        }
    }
}