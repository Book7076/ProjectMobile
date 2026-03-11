package com.example.project

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFlowerFinalScreen(navController: NavHostController, viewModel: ProjectViewModel, flowerId: Int) {
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)
    val item = viewModel.flowerList.find { it.id_flower == flowerId }
    val mainColor = Color(0xFFD2B49C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("รายละเอียดสินค้า", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Checkout.route) }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (item == null) {
            // กรณีโหลดข้อมูลไม่ทันหรือหา id ไม่เจอ
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = mainColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ รูปภาพสินค้าแบบชัดๆ ดึงจาก Server
                AsyncImage(
                    model = "http://10.0.2.2:3000${item.flower_image}",
                    contentDescription = item.flower_name,
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, mainColor, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ดอก${item.flower_name} (สี${item.color})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.meaning ?: "ไม่มีข้อมูลความหมาย",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ราคา : ฿${item.price} / ดอก",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF9E6B4E)
                )

                Spacer(modifier = Modifier.weight(1f))

                // ✅ ปุ่มเพิ่มลงตะกร้า
                Button(
                    onClick = {
                        viewModel.addToCart(
                            context,
                            sharedPref.getSavedIdUser(),
                            "ดอก${item.flower_name} (${item.color})",
                            item.price
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E6B4E)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = item.quantity > 0
                ) {
                    Text(
                        text = if (item.quantity > 0) "เพิ่มลงตะกร้า" else "สินค้าหมด",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}