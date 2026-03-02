package com.example.project

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

    // Get ID จาก SharedPreferences มาแสดงเป็นค่าเริ่มต้น
    var email by rememberSaveable { mutableStateOf(sharedPref.getSavedEmail()) }
    var password by rememberSaveable { mutableStateOf("") }
    val loginResult = viewModel.loginResult

    // ใน LoginScreen.kt ตรง LaunchedEffect
    LaunchedEffect(key1 = loginResult) {
        loginResult?.let {
            if (!it.error) {
                val role = it.role?.lowercase() ?: ""

                sharedPref.saveLoginStatus(isLoggedIn = true, email = it.email ?: "", role = role)

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
        modifier = Modifier.fillMaxSize().padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Log In", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 8.dp)
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 8.dp)
        )

        Spacer(modifier = Modifier.height(height = 32.dp))

        Button(
            onClick = { viewModel.login( email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5C6BC0)
            )
        ) {
            Text(text = "Login", fontSize = 18.sp)
        }


        Spacer(modifier = Modifier.height(height = 24.dp))

        // Register link
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account? ", color = Color.Black)
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text(text = "Register", color = Color(color = 0xFF3F51B5), fontWeight = FontWeight.Bold)
            }
        }
    }
}