package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CartStep(viewModel: ProjectViewModel, onNext: () -> Unit) {
    val cartItems = viewModel.cartList

    // --- State สำหรับคุมการเปิด/ปิด Dialog ---
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartClass?>(null) }

    val totalPrice by remember {
        derivedStateOf {
            cartItems.filter { it.isSelected }.sumOf { it.price * it.quantity.toDouble() }
        }
    }

    val darkBrown = Color(0xFF6D4C41)
    val lightBrown = Color(0xFFD2B49C)

    // --- Dialog ยืนยันการลบ ---
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("ยืนยันการลบ") },
            text = { Text("คุณต้องการลบ '${itemToDelete?.product_name}' ออกจากตะกร้าใช่หรือไม่?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val index = cartItems.indexOf(itemToDelete!!)
                        if (index != -1) {
                            val id = itemToDelete!!.id_cart
                            cartItems.removeAt(index)
                            viewModel.removeFromCart(id)
                        }
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("ลบ", color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    itemToDelete = null
                }) {
                    Text("ยกเลิก")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = darkBrown)
            Spacer(modifier = Modifier.width(8.dp))
            Text("เลือกสินค้าในตะกร้า", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBrown)
        }

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("ตะกร้าของคุณว่างเปล่า", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(items = cartItems, key = { _, item -> item.id_cart }) { _, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7CCC8).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Checkbox(
                                checked = item.isSelected,
                                onCheckedChange = { isChecked ->
                                    val currentIndex = cartItems.indexOf(item)
                                    if (currentIndex != -1) {
                                        cartItems[currentIndex] = item.copy(isSelected = isChecked)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = darkBrown)
                            )

                            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                                Text(item.product_name, fontWeight = FontWeight.Bold, color = darkBrown)
                                Text("${item.price} บาท", fontSize = 14.sp, color = Color.Gray)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    //ปุ่มลดจำนวน (-)
                                    IconButton(
                                        onClick = {
                                            val currentIndex = cartItems.indexOf(item)
                                            if (currentIndex != -1 && item.quantity > 1) {
                                                val newQty = item.quantity - 1
                                                cartItems[currentIndex] = item.copy(quantity = newQty)
                                                // ✅ อัปเดตไปยัง Database ทันที
                                                viewModel.updateCartQuantity(item.id_cart, newQty)
                                            }
                                        },
                                        modifier = Modifier.size(30.dp).background(lightBrown.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    ) {
                                        Icon(Icons.Default.Remove, null, tint = darkBrown, modifier = Modifier.size(16.dp))
                                    }

                                    Text(text = "${item.quantity}", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)

                                    //ปุ่มเพิ่มจำนวน (+)
                                    IconButton(
                                        onClick = {
                                            val currentIndex = cartItems.indexOf(item)
                                            if (currentIndex != -1) {
                                                val newQty = item.quantity + 1
                                                cartItems[currentIndex] = item.copy(quantity = newQty)
                                                // ✅ อัปเดตไปยัง Database ทันที
                                                viewModel.updateCartQuantity(item.id_cart, newQty)
                                            }
                                        },
                                        modifier = Modifier.size(30.dp).background(lightBrown.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = darkBrown, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            IconButton(onClick = {
                                itemToDelete = item
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, "ลบ", tint = Color(0xFFE57373))
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("ราคารวมทั้งหมด:", fontWeight = FontWeight.Medium)
            Text("$totalPrice บาท", color = Color(0xFFE57373), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = cartItems.any { it.isSelected },
            colors = ButtonDefaults.buttonColors(containerColor = darkBrown),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("ขั้นตอนถัดไป", fontWeight = FontWeight.Bold)
        }
    }
}