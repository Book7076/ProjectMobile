package com.example.project

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun AdminFlowerDetailScreen(navController: NavHostController, viewModel: ProjectViewModel, flowerName: String) {
    val context = LocalContext.current
    val mainColor = Color(0xFFD2B49C)

    // สถานะสำหรับเปิด/ปิด Dialog เพิ่มสีใหม่
    var showAddColorDialog by remember { mutableStateOf(false) }

    // กรองรายการดอกไม้เฉพาะพันธุ์ที่เลือก
    val flowersByColor = viewModel.flowerList.filter { it.flower_name == flowerName }

    // ดึงข้อมูลพื้นฐานจากตัวแรกมาใช้เป็นค่าเริ่มต้นสำหรับสีใหม่
    val defaultData = flowersByColor.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("จัดการสี: $flowerName", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor)
            )
        },
        // --- เพิ่มปุ่ม FloatingActionButton ตรงนี้ ---
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddColorDialog = true },
                containerColor = mainColor,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("เพิ่มสีใหม่") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(flowersByColor) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // แสดงรูปจิ๋วของสีนั้นๆ
                        Image(
                            painter = rememberAsyncImagePainter(item.flower_image),
                            contentDescription = null,
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(text = "สี: ${item.color}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "คลัง: ${item.quantity} | ฿${item.price}", fontSize = 14.sp, color = Color.Gray)
                        }

                        // ปุ่มแก้ไขสีนี้
                        IconButton(onClick = {
                            navController.navigate("admin_edit_flower/${item.id_flower}")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Blue)
                        }

                        // ปุ่มลบสีนี้
                        IconButton(onClick = {
                            viewModel.deleteFlower(context, item.id_flower)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // --- ส่วนของ Dialog สำหรับเพิ่มสีใหม่ (พร้อมรูปภาพ) ---
    if (showAddColorDialog) {
        QuickAddColorWithImageDialog(
            flowerName = flowerName,
            defaultType = defaultData?.type ?: "single",
            defaultMeaning = defaultData?.meaning ?: "",
            defaultPrice = defaultData?.price?.toString() ?: "",
            onDismiss = { showAddColorDialog = false },
            onConfirm = { newColorData ->
                // ✅ เรียกฟังก์ชันเพิ่มดอกไม้ใน ViewModel
                viewModel.addFlower(context, newColorData) {
                    showAddColorDialog = false
                }
            }
        )
    }
}

@Composable
fun QuickAddColorWithImageDialog(
    flowerName: String,
    defaultType: String,
    defaultMeaning: String, // รับความหมายเริ่มต้นมา
    defaultPrice: String,
    onDismiss: () -> Unit,
    onConfirm: (Map<String, Any>) -> Unit
) {
    var newColor by remember { mutableStateOf("") }
    var newQty by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf(defaultPrice) }
    var newMeaning by remember { mutableStateOf(defaultMeaning) } // เพิ่ม State สำหรับความหมาย
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val mainColor = Color(0xFFD2B49C)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("เพิ่มสีใหม่ให้ $flowerName", fontWeight = FontWeight.Bold) },
        text = {
            // ใช้ Column พร้อม scroll เพราะฟิลด์เริ่มเยอะขึ้น เผื่อหน้าจอเล็ก
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // --- ส่วนเลือกรูปภาพ ---
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, mainColor, CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(28.dp), tint = mainColor)
                            Text("รูปสีใหม่", color = mainColor, fontSize = 10.sp)
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

                // --- ฟิลด์ข้อมูล ---
                OutlinedTextField(
                    value = newColor,
                    onValueChange = { newColor = it },
                    label = { Text("ระบุสีใหม่ (เช่น ขาว, ชมพู)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newQty,
                        onValueChange = { newQty = it },
                        label = { Text("จำนวน") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it },
                        label = { Text("ราคา (บาท)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // --- เพิ่มช่องความหมายตรงนี้ ---
                OutlinedTextField(
                    value = newMeaning,
                    onValueChange = { newMeaning = it },
                    label = { Text("ความหมายของสีนี้") },
                    modifier = Modifier.fillMaxWidth().height(90.dp), // ปรับให้สูงขึ้นหน่อยเพราะเป็น Text ยาว
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newColor.isNotEmpty() && newQty.isNotEmpty() && newPrice.isNotEmpty() && imageUri != null) {
                        val data = mapOf(
                            "flower_name" to flowerName,
                            "color" to newColor,
                            "quantity" to (newQty.toIntOrNull() ?: 0),
                            "price" to (newPrice.toDoubleOrNull() ?: 0.0),
                            "type" to defaultType,
                            "meaning" to newMeaning, // ส่งความหมายที่กรอกใหม่ไป
                            "flower_image" to imageUri.toString()
                        )
                        onConfirm(data)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
            ) { Text("บันทึกสีใหม่") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ยกเลิก") }
        }
    )
}