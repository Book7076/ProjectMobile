package com.example.project

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeStep(
    viewModel: ProjectViewModel,
    selectedAddressId: Int,
    onBack: () -> Unit, // ✅ รับ callback สำหรับปุ่มย้อนกลับ
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val userId = sharedPref.getSavedIdUser()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    val themeColor = Color(0xFFD2B49C)
    val darkBrown = Color(0xFF6D4C41)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ชำระเงิน", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { // ✅ เรียกใช้ฟังก์ชันย้อนกลับ
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                // ✅ ใช้ topAppBarColors เพื่อลด Warning (Deprecated)
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "สแกน QR Code เพื่อชำระเงิน",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ส่วนแสดง QR Code
            Card(
                modifier = Modifier.size(250.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("📷 [ รูป QR ร้าน ]", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ปุ่มเลือกรูปสลิป
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = if (imageUri == null) Icons.Default.FileUpload else Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (imageUri == null) "เลือกรูปภาพสลิป" else "เลือกสลิปสำเร็จแล้ว",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ปุ่มยืนยันการแจ้งโอน
            Button(
                enabled = imageUri != null,
                onClick = {
                    imageUri?.let { uri ->
                        viewModel.confirmPaymentAndPlaceOrder(
                            context = context,
                            idUser = userId,
                            idAddress = selectedAddressId,
                            imageUri = uri,
                            onSuccess = onComplete
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkBrown,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "ยืนยันการแจ้งโอน",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}