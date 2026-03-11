package com.example.project

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(navController: NavHostController, viewModel: ProjectViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }

    // ตัวแปร State สำหรับเก็บค่าจาก TextField
    var receiverName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressDetail by remember { mutableStateOf("") }
    var postCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AddAddressTopBar(onBack = { navController.popBackStack() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "ข้อมูลที่อยู่จัดส่ง",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8D6E63),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AddressInputField(label = "ชื่อ-นามสกุล ผู้รับ", value = receiverName, onValueChange = { receiverName = it }, placeholder = "เช่น นายสมชาย ใจดี")
            AddressInputField(label = "เบอร์โทรศัพท์", value = phone, onValueChange = { phone = it }, placeholder = "08x-xxx-xxxx")
            AddressInputField(label = "บ้านเลขที่ / ตึก / ชั้น / แขวง/ตำบล, เขต/อำเภอ, จังหวัด", value = addressDetail, onValueChange = { addressDetail = it }, placeholder = "กรอกที่อยู่โดยละเอียด")
            AddressInputField(label = "รหัสไปรษณีย์", value = postCode, onValueChange = { postCode = it }, placeholder = "xxxxx")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val userId = sharedPref.getSavedIdUser()

                    if (userId != 0) {
                        if (receiverName.isEmpty() || phone.isEmpty() || addressDetail.isEmpty() || postCode.isEmpty()) {
                            Toast.makeText(context, "กรุณากรอกข้อมูลที่จำเป็นให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                        } else {
                            // ส่งข้อมูลโดยใช้ชื่อตัวแปรที่ตรงกับ AddressClass
                            viewModel.saveAddress(
                                id_user = userId,
                                receiver_name = receiverName,
                                phone = phone,
                                address_detail = addressDetail,
                                post_code = postCode
                            ) {
                                Toast.makeText(context, "เพิ่มที่อยู่สำเร็จแล้ว", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "เซสชั่นหมดอายุ กรุณาล็อกอินใหม่", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD2B49C)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("บันทึกที่อยู่", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("จัดการที่อยู่", color = Color.White, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFD2B49C))
    )
}

@Composable
fun AddressInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.LightGray) },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD2B49C),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFBFBFB),
                unfocusedContainerColor = Color(0xFFFBFBFB)
            )
        )
    }
}