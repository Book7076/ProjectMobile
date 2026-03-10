package com.example.project

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun AdminEditFlowerScreen(navController: NavHostController, viewModel: ProjectViewModel, flowerId: Int) {
    val context = LocalContext.current
    val mainColor = Color(0xFFD2B49C)

    // 1. ดึงข้อมูลเดิมจากลิสต์ที่มีอยู่ใน ViewModel
    val existingFlower = viewModel.flowerList.find { it.id_flower == flowerId }

    // ข้อมูลเริ่มต้นสำหรับ Dropdown
    val flowerTypes = listOf("กุหลาบ", "ทานตะวัน", "ทิวลิป", "ลิลลี่", "คาร์เนชั่น", "แบบช่อ (Bouquet)")
    var expanded by remember { mutableStateOf(false) }

    // 2. ตั้งค่า State โดยใช้ข้อมูลเดิม (Initial Values)
    var selectedFlowerName by remember { mutableStateOf(existingFlower?.flower_name ?: "") }
    var color by remember { mutableStateOf(existingFlower?.color ?: "") }
    var qty by remember { mutableStateOf(existingFlower?.quantity?.toString() ?: "") }
    var price by remember { mutableStateOf(existingFlower?.price?.toString() ?: "") }
    var meaning by remember { mutableStateOf(existingFlower?.meaning ?: "") }
    var selectedType by remember { mutableStateOf(existingFlower?.type ?: "single") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val existingImageUrl = existingFlower?.flower_image

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("แก้ไขข้อมูลดอกไม้", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- ส่วนรูปภาพ ---
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, mainColor, RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                // ถ้าเลือกรูปใหม่โชว์รูปใหม่ ถ้าไม่เลือกโชว์รูปเดิมจาก DB
                val painter = if (imageUri != null) {
                    rememberAsyncImagePainter(imageUri)
                } else {
                    rememberAsyncImagePainter(existingImageUrl)
                }

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                ) {
                    Text("เปลี่ยนรูปภาพ", color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp)
                }
            }

            // --- ประเภท ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedType == "single", onClick = { selectedType = "single" })
                Text("ดอกเดี่ยว")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = selectedType == "bouquet", onClick = { selectedType = "bouquet" })
                Text("แบบช่อ")
            }

            // --- ชื่อพันธุ์ (Editable Dropdown) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedFlowerName,
                    onValueChange = { selectedFlowerName = it },
                    label = { Text("ชื่อพันธุ์ดอกไม้") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    flowerTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedFlowerName = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // --- สี ---
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("สี") }, modifier = Modifier.fillMaxWidth())

            // --- จำนวน และ ราคา ---
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("จำนวนคงเหลือ") }, modifier = Modifier.weight(1f).padding(end = 4.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("ราคา (บาท)") }, modifier = Modifier.weight(1f).padding(start = 4.dp))
            }

            // --- ความหมาย ---
            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("ความหมาย") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- ปุ่มบันทึกการแก้ไข ---
            Button(
                onClick = {
                    if (selectedFlowerName.isNotEmpty() && color.isNotEmpty()) {
                        val data = mapOf(
                            "id_flower" to flowerId, // ต้องส่ง ID ไปด้วยเพื่อบอกว่าจะแก้ตัวไหน
                            "flower_name" to selectedFlowerName,
                            "color" to color,
                            "quantity" to (qty.toIntOrNull() ?: 0),
                            "price" to (price.toDoubleOrNull() ?: 0.0),
                            "type" to selectedType,
                            "meaning" to meaning,
                            "flower_image" to (imageUri?.toString() ?: existingImageUrl ?: "")
                        )
                        // ✅ เรียกฟังก์ชันอัปเดตใน ViewModel
                        viewModel.updateFlower(context, data) {
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("บันทึกการแก้ไข", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}