package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun CheckoutScreen(navController: NavHostController, viewModel: ProjectViewModel) {
    // --- 1. ย้ายการประกาศค่า Composable มาไว้ด้านบนสุดตรงนี้ ---
    val context = LocalContext.current
    val sharedPref = remember { SharedPreferencesManager(context) }
    val userId = sharedPref.getSavedIdUser()

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedAddressId by remember { mutableIntStateOf(-1) }
    var selectedPaymentMethod by remember { mutableStateOf("") }

    val primaryColor = Color(0xFFD2B49C)
    val darkBrown = Color(0xFF6D4C41)

    when (currentStep) {
        4 -> {
            QRCodeStep(
                viewModel = viewModel,
                selectedAddressId = selectedAddressId,
                onBack = { currentStep = 3 },
                onComplete = { currentStep = 5 }
            )
        }
        5 -> {
            ThankYouStep {
                navController.navigate(Screen.CustomerScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        else -> {
            Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (currentStep == 0) navController.popBackStack()
                            else currentStep--
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = darkBrown)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ยืนยันการสั่งซื้อ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrown,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "ขั้นตอนที่ ${currentStep + 1}/4",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 44.dp)
                )

                LinearProgressIndicator(
                    progress = (currentStep + 1) / 4f,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(8.dp),
                    color = primaryColor,
                    trackColor = primaryColor.copy(alpha = 0.2f)
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (currentStep) {
                        0 -> CartStep(viewModel) { currentStep = 1 }
                        1 -> AddressStep(viewModel) { id ->
                            selectedAddressId = id
                            currentStep = 2
                        }
                        2 -> SummaryStep(viewModel, selectedAddressId) {
                            currentStep = 3
                        }
                        3 -> PaymentMethodStep { method ->
                            selectedPaymentMethod = method
                            if (method == "QR") {
                                currentStep = 4
                            } else {
                                // ✅ ใช้ค่า userId และ context ที่ประกาศไว้ด้านบนได้เลย ไม่ Error แล้ว
                                viewModel.placeOrderCOD(
                                    idUser = userId,
                                    idAddress = selectedAddressId,
                                    onSuccess = {
                                        currentStep = 5
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}