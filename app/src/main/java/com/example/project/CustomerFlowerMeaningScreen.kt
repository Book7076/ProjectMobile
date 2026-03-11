package com.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFlowerMeaningScreen(navController: NavHostController, viewModel: ProjectViewModel, flowerName: String) {
    val flower = viewModel.flowerList.find { it.flower_name == flowerName }
    val mainColor = Color(0xFFD2B49C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("รายละเอียดสินค้า", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("แคตตาล็อกดอกไม้", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
            Spacer(modifier = Modifier.height(20.dp))

            // ✅ ดึงรูปจาก Server
            AsyncImage(
                model = "http://10.0.2.2:3000${flower?.flower_image}",
                contentDescription = null,
                modifier = Modifier.size(250.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )

            Text("ดอก$flowerName", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = flower?.meaning ?: "ไม่มีข้อมูลความหมาย", modifier = Modifier.padding(16.dp))

            Button(
                onClick = { navController.navigate("flower_color_grid/$flowerName") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E6B4E))
            ) {
                Text("เลือกสีดอกไม้")
            }
        }
    }
}