package com.example.project

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProjectViewModel : ViewModel() {
    private var _loginResult by mutableStateOf<LoginClass?>(null)
    val loginResult get() = _loginResult

    private var _studentProfile by mutableStateOf<ProfileClass?>(null)
    val studentProfile get() = _studentProfile

    private var _errorMessage by mutableStateOf("")
    val errorMessage get() = _errorMessage

    var cartList = androidx.compose.runtime.mutableStateListOf<CartClass>()
        private set

    var addressList by mutableStateOf<List<AddressClass>>(emptyList())
        private set

    var adminOrderList by mutableStateOf<List<AdminOrderClass>>(emptyList())
        private set

    var customerOrderList by mutableStateOf<List<AdminOrderClass>>(emptyList())
        private set
    var riderList = mutableStateListOf<LoginClass>()

    var flowerList = mutableStateListOf<FlowerClass>()


    fun resetLoginResult() { _loginResult = null }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult = null
                val loginData = mapOf("email" to email, "password" to password)
                val response = ProjectClient.projectAPI.login(loginData)

                if (response.isSuccessful) {
                    _loginResult = response.body()
                    _errorMessage = ""
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorObj = Gson().fromJson(errorBody, LoginClass::class.java)
                    _loginResult = errorObj
                }
            } catch (e: Exception) {
                _loginResult = LoginClass(error = true, message = "Connection Error", email = null, role = null, name = null, id_user = null, phone = null)
            }
        }
    }

    fun getProfile(id: String) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getStudentProfile(id)
                if (response.isSuccessful) {
                    _studentProfile = response.body()
                } else {
                    _errorMessage = "Student data not found"
                }
            } catch (e: Exception) {
                _errorMessage = "Error: ${e.message}"
            }
        }
    }

    fun register(context: Context, customer: RegisterClass, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.registerCustomer(customer)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorRawString = response.errorBody()?.string()
                    val finalMessage = if (!errorRawString.isNullOrEmpty()) {
                        try {
                            val errorData = Gson().fromJson(errorRawString, CommonResponse::class.java)
                            errorData.message
                        } catch (e: Exception) { errorRawString }
                    } else { response.message() }
                    _errorMessage = finalMessage
                    Toast.makeText(context, _errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                _errorMessage = "Network Error: ${e.message}"
                Toast.makeText(context, _errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addToCart(context: Context, userId: Int, productName: String, price: Double) {
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "id_user" to userId,
                    "product_name" to productName,
                    "price" to price
                )
                val response = ProjectClient.projectAPI.addToCart(data)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error) {
                        Toast.makeText(context, "เพิ่มลงตะกร้าเรียบร้อย!", Toast.LENGTH_SHORT).show()
                        fetchCart(userId)
                    } else {
                        val msg = body?.message ?: "ไม่สามารถเพิ่มสินค้าได้"
                        Toast.makeText(context, "เกิดข้อผิดพลาด: $msg", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CART_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun fetchCart(userId: Int) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getCartItems(userId)
                cartList.clear()
                cartList.addAll(response)
            } catch (e: Exception) {
                Log.e("CART_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun saveAddress(id_user: Int, receiver_name: String, phone: String, address_detail: String, post_code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val addressMap = mapOf(
                    "id" to id_user,
                    "name" to receiver_name,
                    "phone" to phone,
                    "addressDetail" to address_detail,
                    "postCode" to post_code
                )
                val response = ProjectClient.projectAPI.saveAddress(addressMap)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage = "ไม่สามารถบันทึกได้: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage = "Connection Error: ${e.message}"
            }
        }
    }

    fun fetchAddresses(userId: Int) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getAddresses(userId)
                addressList = response
            } catch (e: Exception) {
                _errorMessage = "โหลดข้อมูลล้มเหลว: ${e.message}"
            }
        }
    }

    fun placeOrder(id_user: Int, id_address: Int, product_name: String, price: Double, onSuccess: (OrderResponse) -> Unit) {
        viewModelScope.launch {
            try {
                val orderRequest = OrderRequest(id_user, id_address, product_name, price)
                val response = ProjectClient.projectAPI.placeOrder(orderRequest)
                if (response.error == false) {
                    onSuccess(response)
                }
            } catch (e: Exception) {
                Log.e("ORDER_DEBUG", "Error: ${e.message}")
            }
        }
    }

    fun fetchAdminOrders() {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getAdminOrders()
                adminOrderList = response
            } catch (e: Exception) {
                Log.e("ADMIN_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun updateOrderStatus(idOrder: Int, newStatus: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val data = mapOf("id_order" to idOrder, "status" to newStatus)
                val response = ProjectClient.projectAPI.updateOrderStatus(data)
                if (response.error == false) {
                    fetchAdminOrders() // สำคัญ: เพื่อให้หน้า Admin และ Rider เห็นข้อมูลล่าสุด
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("UPDATE_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun uploadPayment(orderId: Int, amount: Double, imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val orderIdBody = RequestBody.create(MultipartBody.FORM, orderId.toString())
                val amountBody = RequestBody.create(MultipartBody.FORM, amount.toString())
                val response = ProjectClient.projectAPI.uploadPayment(orderIdBody, amountBody, imagePart)
                if (response.isSuccessful && response.body()?.error == false) {
                    Log.d("PAYMENT", "Upload Slip Success")
                }
            } catch (e: Exception) {
                Log.e("PAYMENT_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun removeFromCart(idCart: Int) {
        viewModelScope.launch {
            try {
                ProjectClient.projectAPI.deleteCartItem(idCart)
            } catch (e: Exception) {
                Log.e("API", "ลบไม่สำเร็จ: ${e.message}")
            }
        }
    }

    // --- ฟังก์ชันหลักสำหรับบันทึกออเดอร์และแจ้งโอนเงิน ---
    fun confirmPaymentAndPlaceOrder(
        context: Context,
        idUser: Int,
        idAddress: Int,
        imageUri: Uri,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. ตรวจสอบสินค้าในตะกร้า
                var selectedItems = cartList.filter { it.isSelected }
                if (selectedItems.isEmpty()) {
                    selectedItems = cartList
                }

                if (selectedItems.isEmpty()) {
                    Log.e("ORDER_DEBUG", "ตะกร้าว่างเปล่า สั่งซื้อไม่ได้")
                    return@launch
                }

                val productName = selectedItems.joinToString(", ") { it.product_name }
                val totalPrice = selectedItems.sumOf { it.price * it.quantity }

                Log.d("ORDER_DEBUG", "กำลังสร้าง Order: User $idUser, Address $idAddress, Total $totalPrice")

                // 2. บันทึก Order ลง DB
                val orderRequest = OrderRequest(idUser, idAddress, productName, totalPrice)
                val orderResponse = ProjectClient.projectAPI.placeOrder(orderRequest)

                if (orderResponse.error == false && orderResponse.id_order != null) {
                    val newOrderId = orderResponse.id_order
                    Log.d("ORDER_DEBUG", "สร้าง Order สำเร็จ ID: $newOrderId กำลังอัปโหลดสลิป...")

                    // 4. เตรียมรูปสลิป
                    val imagePart = prepareImagePart(context, imageUri, "slip_image")

                    if (imagePart != null) {
                        val orderIdBody = RequestBody.create(MultipartBody.FORM, newOrderId.toString())
                        val amountBody = RequestBody.create(MultipartBody.FORM, totalPrice.toString())

                        val uploadResponse = ProjectClient.projectAPI.uploadPayment(orderIdBody, amountBody, imagePart)

                        if (uploadResponse.isSuccessful) {
                            Log.d("ORDER_DEBUG", "แจ้งโอนสำเร็จ! กำลังลบตะกร้า...")
                            selectedItems.forEach {
                                ProjectClient.projectAPI.deleteCartItem(it.id_cart)
                            }
                            fetchCart(idUser)
                            onSuccess()
                        } else {
                            Log.e("ORDER_DEBUG", "Upload Fail: ${uploadResponse.message()}")
                        }
                    }
                } else {
                    Log.e("ORDER_DEBUG", "Server error: ${orderResponse.message}")
                }
            } catch (e: Exception) {
                Log.e("ORDER_DEBUG", "Exception: ${e.message}")
            }
        }
    }

    private fun prepareImagePart(context: Context, imageUri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes() ?: return null

            val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())

            MultipartBody.Part.createFormData(partName, "image_${System.currentTimeMillis()}.jpg", requestFile)
        } catch (e: Exception) {
            Log.e("IMAGE_PART_ERROR", "Error: ${e.message}")
            null
        }
    }

    fun completeRiderJob(
        context: Context,
        idOrder: Int,
        idRider: Int,
        imageUri: Uri,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val imagePart = prepareImagePart(context, imageUri, "delivery_image")
                val orderIdBody = RequestBody.create(MultipartBody.FORM, idOrder.toString())
                val riderIdBody = RequestBody.create(MultipartBody.FORM, idRider.toString())

                val response = ProjectClient.projectAPI.completeRiderJob(
                    orderIdBody,
                    riderIdBody,
                    imagePart
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Toast.makeText(context, "ไม่สามารถบันทึกข้อมูลได้", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RIDER_DEBUG", "Error: ${e.message}")
                Toast.makeText(context, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchCustomerOrders(userId: Int) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getCustomerOrders(userId)
                customerOrderList = response
            } catch (e: Exception) {
                Log.e("HISTORY_ERROR", "Error: ${e.message}")
                customerOrderList = emptyList()
            }
        }
    }

    fun fetchAllRiders() {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getAllUsers()
                riderList.clear()
                val riders = response.filter { it.role == "rider" }
                riderList.addAll(riders)
            } catch (e: Exception) {
                Log.e("FETCH_RIDER", "Error: ${e.message}")
            }
        }
    }

    fun registerRider(context: Context, riderData: RegisterRiderClass, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.registerRider(riderData)

                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "ลงทะเบียน Rider สำเร็จ", Toast.LENGTH_SHORT).show()
                    fetchAllRiders()
                    onSuccess()
                } else {
                    val msg = response.body()?.message ?: "เกิดข้อผิดพลาด"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "เชื่อมต่อล้มเหลว: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- ✅ แก้ไขฟังก์ชัน Update Rider ---
    fun updateRider(context: Context, email: String, name: String, phone: String, gender: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val data = mutableMapOf<String, Any>(
                    "email" to email,
                    "name" to name,
                    "phone" to phone,
                    "gender" to gender
                )
                if (pass.isNotEmpty()) data["password"] = pass

                val response = ProjectClient.projectAPI.updateRider(data)
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                    fetchAllRiders()
                    onSuccess()
                } else {
                    val errorMsg = response.body()?.message ?: "แก้ไขล้มเหลว"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UPDATE_ERROR", "Error: ${e.message}")
                Toast.makeText(context, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- ✅ แก้ไขฟังก์ชัน Delete Rider ---
    fun deleteRider(context: Context, email: String) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.deleteRider(email)
                if (response.isSuccessful && response.body()?.error == false) {
                    Toast.makeText(context, "ลบ Rider เรียบร้อย", Toast.LENGTH_SHORT).show()
                    fetchAllRiders()
                } else {
                    // ถ้าลบไม่ได้ อาจเพราะติด Foreign Key (มีออเดอร์ค้าง)
                    val errorMsg = response.body()?.message ?: "ไม่สามารถลบได้ (อาจมีข้อมูลออเดอร์ผูกอยู่)"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("DELETE_ERROR", "Error: ${e.message}")
                Toast.makeText(context, "เชื่อมต่อ Server ล้มเหลว", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchAllFlowers() {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.getAllFlowers()
                flowerList.clear()
                flowerList.addAll(response)
            } catch (e: Exception) { Log.e("API", "Error: ${e.message}") }
        }
    }

    fun addFlower(context: Context, data: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.addFlower(data)
                if (response.isSuccessful) {
                    fetchAllFlowers()
                    onSuccess()
                }
            } catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
        }
    }

    fun deleteFlower(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val response = ProjectClient.projectAPI.deleteFlower(id)
                if (response.isSuccessful) {
                    fetchAllFlowers()
                    Toast.makeText(context, "ลบสำเร็จ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) { Log.e("API", "Error: ${e.message}") }
        }
    }

    fun updateFlower(context: Context, data: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // เรียก API updateFlower ที่เราเพิ่มใน interface ไว้
                val response = ProjectClient.projectAPI.updateFlower(data)
                if (response.isSuccessful) {
                    Toast.makeText(context, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                    fetchAllFlowers() // โหลดข้อมูลใหม่เพื่อให้หน้าลิสต์อัปเดต
                    onSuccess()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // เพิ่มไว้ใน ProjectViewModel.kt
    fun updateCartQuantity(idCart: Int, newQuantity: Int) {
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "id_cart" to idCart,
                    "quantity" to newQuantity
                )
                val response = ProjectClient.projectAPI.updateCartQuantity(data)
                if (response.isSuccessful && response.body()?.error == false) {
                    Log.d("CART_DEBUG", "Update Quantity Success: ID $idCart to $newQuantity")
                } else {
                    Log.e("CART_DEBUG", "Update Fail: ${response.body()?.message}")
                }
            } catch (e: Exception) {
                Log.e("CART_DEBUG", "Error: ${e.message}")
            }
        }
    }

    fun placeOrderCOD(
        idUser: Int,
        idAddress: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. เตรียมข้อมูลสินค้า
                val selectedItems = cartList.filter { it.isSelected }.ifEmpty { cartList }
                val productName = selectedItems.joinToString(", ") { it.product_name }
                val totalPrice = selectedItems.sumOf { it.price * it.quantity }

                // 2. ส่งคำสั่งซื้อไปยัง API (สถานะจะเป็น 'pending' โดยอัตโนมัติตาม Node.js)
                val orderRequest = OrderRequest(idUser, idAddress, productName, totalPrice)
                val response = ProjectClient.projectAPI.placeOrder(orderRequest)

                if (response.error == false) {
                    // 3. ลบสินค้าที่สั่งซื้อออกจากตะกร้าใน DB
                    selectedItems.forEach {
                        ProjectClient.projectAPI.deleteCartItem(it.id_cart)
                    }
                    // 4. รีเฟรชตะกร้าในแอป
                    fetchCart(idUser)
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("ORDER_COD_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun updateProfile(context: Context, id: Int, name: String, gender: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // ✅ ส่งค่าแยกกัน 3 ตัว: id_user (Int), name (String), gender (String)
                // เช็กใน ProjectAPI.kt ว่าตั้งชื่อฟังก์ชันและเรียงลำดับแบบนี้ไหม
                val response = ProjectClient.projectAPI.updateStudentProfile(id, name, gender)

                if (response.isSuccessful) {
                    val body = response.body()
                    // ✅ ตรวจสอบ error จากฝั่ง Server (ต้องส่งกลับมาเป็น false ถึงจะแปลว่าสำเร็จ)
                    if (body != null && body.error == false) {
                        // ✅ อัปเดตข้อมูลใน ViewModel โดยการดึงใหม่จาก Database
                        getProfile(id.toString())
                        Toast.makeText(context, "อัปเดตข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        val msg = body?.message ?: "อัปเดตไม่สำเร็จ"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // ถ้า Code 500 คือ SQL ฝั่ง Node.js เขียนผิด
                    // ถ้า Code 404 คือ ลืมใส่ Path ใน ProjectAPI หรือใส่ชื่อผิด
                    Log.e("API_ERROR", "Code: ${response.code()} Body: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "เซิร์ฟเวอร์ตอบสนองผิดพลาด: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UPDATE_ERROR", "Error: ${e.message}")
                Toast.makeText(context, "เชื่อมต่อฐานข้อมูลล้มเหลว: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}