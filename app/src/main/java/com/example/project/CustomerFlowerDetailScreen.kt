package com.example.project

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFlowerDetailScreen(navController: NavHostController, viewModel: ProjectViewModel, flowerName: String) {
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)
    val mainColor = Color(0xFFD2B49C)

    // กรองเฉพาะสีของพันธุ์ที่เลือก
    val flowersByColor = viewModel.flowerList.filter { it.flower_name == flowerName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เลือกสี: $flowerName", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(flowersByColor) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column {
                        Image(
                            painter = rememberAsyncImagePainter(item.flower_image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "สี: ${item.color}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "฿${item.price}", fontSize = 20.sp, color = Color(0xFF9E6B4E), fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "ความหมาย: ${item.meaning}", color = Color.Gray, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (item.quantity > 0) {
                                        viewModel.addToCart(
                                            context = context,
                                            userId = sharedPref.getSavedIdUser(),
                                            productName = "ดอก${item.flower_name} (${item.color})",
                                            price = item.price
                                        )
                                    } else {
                                        Toast.makeText(context, "ขออภัย สินค้าหมด", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if(item.quantity > 0) Color(0xFF9E6B4E) else Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                enabled = item.quantity > 0
                            ) {
                                Text(
                                    text = if(item.quantity > 0) "เพิ่มลงตะกร้า" else "สินค้าหมด",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}