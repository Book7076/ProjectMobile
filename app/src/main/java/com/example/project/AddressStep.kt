package com.example.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddressStep(viewModel: ProjectViewModel, onAddressSelected: (Int) -> Unit) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val userId = sharedPref.getSavedIdUser()

    // ดึงข้อมูลที่อยู่จาก ViewModel
    val addresses = viewModel.addressList
    var selectedId by remember { mutableIntStateOf(-1) }

    // ✅ เพิ่มการโหลดข้อมูลอัตโนมัติเมื่อเข้าสู่ Step นี้
    // สาเหตุที่ก่อนหน้านี้ไม่ขึ้น เพราะไม่มีคำสั่ง fetch ข้อมูลในหน้านี้นั่นเอง
    LaunchedEffect(Unit) {
        if (userId != 0) {
            viewModel.fetchAddresses(userId)
        }
    }

    val darkBrown = Color(0xFF6D4C41)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "เลือกที่อยู่จัดส่ง",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (addresses.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ยังไม่มีข้อมูลที่อยู่", color = Color.Gray)
                    Text("กรุณาเพิ่มที่อยู่ที่หน้าโปรไฟล์", fontSize = 14.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = addresses, key = { it.id ?: 0 }) { address ->
                    val addrId = address.id ?: -1
                    val isSelected = selectedId == addrId

                    OutlinedCard(
                        onClick = { if (addrId != -1) selectedId = addrId },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) darkBrown else Color.LightGray.copy(alpha = 0.5f)
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) darkBrown.copy(alpha = 0.05f) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (addrId != -1) selectedId = addrId },
                                colors = RadioButtonDefaults.colors(selectedColor = darkBrown)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = address.receiver_name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = darkBrown
                                )
                                Text(
                                    text = "โทร: ${address.phone}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${address.address_detail} ${address.post_code}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ปุ่มยืนยัน
        Button(
            onClick = { if (selectedId != -1) onAddressSelected(selectedId) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = selectedId != -1, // กดได้เฉพาะเมื่อเลือกที่อยู่แล้ว
            colors = ButtonDefaults.buttonColors(
                containerColor = darkBrown,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = if (selectedId != -1) "ใช้ที่อยู่นี้สำหรับคำสั่งซื้อ" else "กรุณาเลือกที่อยู่",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}