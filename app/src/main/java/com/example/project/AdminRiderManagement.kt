package com.example.project

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRiderManagement(navController: NavHostController, viewModel: ProjectViewModel) {
    var showAddRiderSheet by remember { mutableStateOf(false) }

    // ✅ เพิ่ม State สำหรับ Alert การลบ
    var showDeleteDialog by remember { mutableStateOf(false) }
    var riderToDelete by remember { mutableStateOf<LoginClass?>(null) }

    val mainColor = Color(0xFFD2B49C)
    val context = LocalContext.current

    // ✅ UI สำหรับ Alert ยืนยันการลบ
    if (showDeleteDialog && riderToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ยืนยันการลบ", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการลบ Rider: ${riderToDelete?.name} ใช่หรือไม่?\nการลบอาจล้มเหลวหาก Rider มีประวัติออเดอร์ค้างอยู่") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRider(context, riderToDelete?.email ?: "")
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text("ยืนยันการลบ", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("จัดการข้อมูล Rider", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mainColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddRiderSheet = true },
                containerColor = mainColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Rider")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "รายชื่อ Rider ทั้งหมด",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.riderList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Text("ไม่พบข้อมูล Rider ในระบบ", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.riderList) { rider ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = mainColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = mainColor)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = rider.name ?: "ไม่ระบุชื่อ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF3E2723)
                                    )
                                    Text(
                                        text = "อีเมล: ${rider.email ?: "-"}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "เบอร์โทร: ${rider.phone ?: ""}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row {
                                    // ปุ่มแก้ไข
                                    IconButton(onClick = {
                                        val email = rider.email ?: ""
                                        if (email.isNotEmpty()) {
                                            navController.navigate(Screen.EditRider.createRoute(email))
                                        }
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF42A5F5))
                                    }

                                    // ปุ่มลบ (เรียกใช้ Dialog ยืนยัน)
                                    IconButton(onClick = {
                                        riderToDelete = rider
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddRiderSheet) {
        RegisterRiderDialog(viewModel) { showAddRiderSheet = false }
    }
}

@Composable
fun RegisterRiderDialog(viewModel: ProjectViewModel, onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female")
    var selectedGender by remember { mutableStateOf("Male") }
    val context = LocalContext.current
    val mainColor = Color(0xFFD2B49C)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ลงทะเบียน Rider ใหม่", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ชื่อ-นามสกุล") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("เบอร์โทรศัพท์") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "เพศ:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    genderOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            RadioButton(
                                selected = (selectedGender == option),
                                onClick = { selectedGender = option },
                                colors = RadioButtonDefaults.colors(selectedColor = mainColor)
                            )
                            Text(text = option, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val riderData = RegisterRiderClass(
                        email = email,
                        password = pass,
                        name = name,
                        phone = phone,
                        gender = selectedGender,
                    )
                    viewModel.registerRider(context, riderData) { onDismiss() }
                },
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                shape = RoundedCornerShape(8.dp),
                enabled = email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty()
            ) {
                Text("ยืนยัน", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก", color = Color.Gray)
            }
        }
    )
}