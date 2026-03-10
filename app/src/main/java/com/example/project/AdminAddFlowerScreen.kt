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
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
fun AdminAddFlowerScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val mainColor = Color(0xFFD2B49C)

    // ข้อมูลเริ่มต้นสำหรับแนะนำพันธุ์ดอกไม้ (สามารถพิมพ์ใหม่นอกเหนือจากนี้ได้)
    val flowerTypes = listOf("กุหลาบ", "ทานตะวัน", "ทิวลิป", "ลิลลี่", "คาร์เนชั่น", "ไฮเดรนเยีย", "แบบช่อ (Bouquet)")
    var expanded by remember { mutableStateOf(false) }

    // States สำหรับเก็บข้อมูลฟอร์ม
    var selectedFlowerName by remember { mutableStateOf("") } // ชื่อพันธุ์ (พิมพ์เองได้)
    var color by remember { mutableStateOf("") }             // สี
    var qty by remember { mutableStateOf("") }               // จำนวน
    var price by remember { mutableStateOf("") }             // ราคา
    var meaning by remember { mutableStateOf("") }           // ความหมาย
    var selectedType by remember { mutableStateOf("single") } // ประเภท (ดอกเดี่ยว/ช่อ)
    var imageUri by remember { mutableStateOf<Uri?>(null) }  // รูปภาพ

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เพิ่มข้อมูลใหม่", color = Color.White) },
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
            // --- 1. ส่วนเลือกรูปภาพ ---
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, mainColor, RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = mainColor
                        )
                        Text("เลือกรูปภาพ", color = mainColor, fontSize = 14.sp)
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // --- 2. เลือกประเภทหลัก ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(selected = selectedType == "single", onClick = { selectedType = "single" })
                Text("ดอกเดี่ยว")
                Spacer(modifier = Modifier.width(20.dp))
                RadioButton(selected = selectedType == "bouquet", onClick = { selectedType = "bouquet" })
                Text("แบบช่อ")
            }

            // --- 3. ชื่อพันธุ์ดอกไม้ (Dropdown ที่พิมพ์เองได้) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedFlowerName,
                    onValueChange = {
                        selectedFlowerName = it
                        // ถ้าพิมพ์เองแล้วมีคำว่าช่อ ให้ติ๊ก RadioButton อัตโนมัติ
                        if (it.contains("ช่อ")) selectedType = "bouquet"
                    },
                    label = { Text("ชื่อพันธุ์ดอกไม้ (พิมพ์ใหม่หรือเลือกจากรายการ)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    singleLine = true
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    flowerTypes.forEach { flowerType ->
                        DropdownMenuItem(
                            text = { Text(flowerType) },
                            onClick = {
                                selectedFlowerName = flowerType
                                expanded = false
                                if (flowerType.contains("ช่อ") || flowerType.contains("Bouquet")) {
                                    selectedType = "bouquet"
                                } else {
                                    selectedType = "single"
                                }
                            }
                        )
                    }
                }
            }

            // --- 4. ระบุสี ---
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("สี (เช่น แดง, ชมพู, พาสเทล)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- 5. จำนวน และ ราคา ---
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("จำนวนสินค้า") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("ราคา (บาท)") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    singleLine = true
                )
            }

            // --- 6. ความหมาย ---
            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("ความหมายของดอกไม้") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("ระบุความหมายเพื่อให้ลูกค้าตัดสินใจง่ายขึ้น...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 7. ปุ่มบันทึก ---
            Button(
                onClick = {
                    if (selectedFlowerName.isNotEmpty() && color.isNotEmpty() && price.isNotEmpty()) {
                        val data = mapOf(
                            "flower_name" to selectedFlowerName,
                            "color" to color,
                            "quantity" to (qty.toIntOrNull() ?: 0),
                            "price" to (price.toDoubleOrNull() ?: 0.0),
                            "type" to selectedType,
                            "meaning" to meaning,
                            "flower_image" to (imageUri?.toString() ?: "")
                        )
                        viewModel.addFlower(context, data) {
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "กรุณากรอกข้อมูล ชื่อพันธุ์ สี และราคา", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("บันทึกเข้าคลังสินค้า", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}