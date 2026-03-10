package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CustomerProfileScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val themeColor = Color(0xFFD2B49C)
    val backgroundColor = Color(0xFFFDFDFD)
    val darkBrown = Color(0xFF5D4037)
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)

    // ✅ 1. ดึงข้อมูลจาก API (ถ้าโหลดเสร็จแล้ว)
    val profileData = viewModel.studentProfile

    // ✅ 2. ดึงข้อมูลสำรองจาก SharedPreferences (ถ้า API ยังโหลดไม่เสร็จ ให้ใช้ค่าที่เซฟไว้ตอน Login)
    // หมายเหตุ: พี่ต้องเช็กว่าใน SharedPreferencesManager มีฟังก์ชัน getSavedName/Email หรือยัง
    val savedName = sharedPref.getSavedName() ?: "ผู้ใช้งาน"
    val savedEmail = sharedPref.getSavedEmail() ?: "-"

    val displayName = profileData?.name ?: savedName
    val displayEmail = profileData?.email ?: savedEmail

    LaunchedEffect(Unit) {
        val userId = sharedPref.getSavedIdUser()
        if (userId != 0) {
            viewModel.getProfile(userId.toString())
        }
    }

    Scaffold(
        topBar = { ProfileTopBar(themeColor, navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ✅ แสดงรูปและข้อมูล (ชื่อ/อีเมล จะไม่เป็นขีดแล้ว เพราะมี savedName สำรอง)
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = darkBrown)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = displayName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = darkBrown)
            Text(text = displayEmail, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Surface(modifier = Modifier.fillMaxWidth(0.9f), color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)) {
                Text(text = "สถานะการสั่งซื้อ", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatusItem(Icons.Default.AssignmentTurnedIn, "รอตรวจสอบ") { navController.navigate("order_history/pending") }
                StatusItem(Icons.Default.Autorenew, "กำลังทำ") { navController.navigate("order_history/processing") }
                StatusItem(Icons.Default.LocalMall, "ที่ต้องได้รับ") { navController.navigate("order_history/shipping") }
                StatusItem(Icons.Default.CheckCircle, "เสร็จสิ้น") { navController.navigate("order_history/completed") }
            }

            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                ProfileMenuItem("โปรไฟล์") { navController.navigate(Screen.EditProfile.route) }
                ProfileMenuItem("ที่อยู่ของฉัน") { navController.navigate(Screen.AddressList.route) }
                ProfileMenuItem("ประวัติการซื้อ") { navController.navigate(Screen.OrderHistory.route) }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().height(50.dp).clickable {
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    },
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.CenterStart) {
                        Text(text = "ออกจากระบบ", modifier = Modifier.padding(start = 16.dp), color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerEditProfileScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)
    val profile = viewModel.studentProfile
    val userId = sharedPref.getSavedIdUser()

    // ✅ ใช้ค่าจาก Profile (API) ถ้าไม่มีให้ใช้จาก SharedPref (Login)
    val initialName = profile?.name ?: sharedPref.getSavedName() ?: ""
    val initialEmail = profile?.email ?: sharedPref.getSavedEmail() ?: "-"
    val initialGender = profile?.std_gender ?: "Male"

    var nameState by remember(profile) { mutableStateOf(initialName) }
    var genderState by remember(profile) { mutableStateOf(initialGender) }
    var isEditing by remember { mutableStateOf(false) }

    val darkBrown = Color(0xFF5D4037)
    val beigeColor = Color(0xFFEADDD0)

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 24.dp)) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = darkBrown)
            }
            Text("ข้อมูลโปรไฟล์", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBrown, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = darkBrown)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("อีเมล", color = Color.Gray, fontSize = 14.sp)
        Text(text = initialEmail, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        HorizontalDivider(thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(20.dp))

        EditField(label = "ชื่อ-นามสกุล", value = nameState, isEditing = isEditing) { nameState = it }
        Spacer(modifier = Modifier.height(20.dp))
        EditField(label = "เพศ", value = genderState, isEditing = isEditing) { genderState = it }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (isEditing) {
                    viewModel.updateProfile(context, userId, nameState, genderState) {
                        isEditing = false
                        viewModel.getProfile(userId.toString()) // โหลดใหม่หลังเซฟ
                    }
                } else {
                    isEditing = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color(0xFF8D6E63) else beigeColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isEditing) "บันทึกข้อมูล" else "แก้ไขข้อมูล", color = if (isEditing) Color.White else darkBrown, fontSize = 16.sp)
        }
    }
}

// ... (StatusItem, ProfileMenuItem, EditField, ProfileTopBar เหมือนเดิม) ...

@Composable
fun EditField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD2B49C))
            )
        } else {
            Text(value.ifEmpty { "-" }, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
fun StatusItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = Color(0xFF8D6E63))
        Text(text = label, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp), color = Color.DarkGray)
    }
}

@Composable
fun ProfileMenuItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = title, fontSize = 16.sp, color = Color.Black)
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(themeColor: Color, navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = { Text("Flower Daily", color = Color.White, fontWeight = FontWeight.Medium) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = { /* ตะกร้า */ }) { Icon(Icons.Default.ShoppingBasket, null, tint = Color.White) }
            IconButton(onClick = { /* โปรไฟล์ */ }) { Icon(Icons.Default.AccountCircle, null, tint = Color.White) }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = themeColor)
    )
}