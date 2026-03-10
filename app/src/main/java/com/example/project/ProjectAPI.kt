package com.example.project

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProjectAPI {
    // Register สำหรับ Customer (ตัวเดิม)
    @POST("register")
    suspend fun registerCustomer(
        @Body studentData: RegisterClass
    ): Response<CommonResponse>

    // ✅ เพิ่ม: Register สำหรับ Rider (ใช้ Data Class ตัวใหม่ที่คุณสร้าง)
    @POST("register")
    suspend fun registerRider(
        @Body riderData: RegisterRiderClass
    ): Response<CommonResponse>

    // ✅ เพิ่ม: ดึงรายชื่อ User ทั้งหมด (เพื่อเอามากรองแสดง Rider ในหน้า Admin)
    @GET("users")
    suspend fun getAllUsers(): List<LoginClass>

    // Login
    @POST("login")
    suspend fun login(
        @Body loginData: Map<String, String>
    ): Response<LoginClass>

    // Search/Profile
    @GET("search/{id}")
    suspend fun getStudentProfile(
        @Path("id") id: String
    ): Response<ProfileClass>

    @POST("add-address")
    suspend fun saveAddress(
        @Body addressData: Map<String, @JvmSuppressWildcards Any>
    ): Response<CommonResponse>

    @GET("addresses/{userId}")
    suspend fun getAddresses(@Path("userId") userId: Int): List<AddressClass>

    @GET("admin/orders")
    suspend fun getAdminOrders(): List<AdminOrderClass>

    @POST("place-order")
    suspend fun placeOrder(@Body orderRequest: OrderRequest): OrderResponse

    @PUT("update-order-status")
    suspend fun updateOrderStatus(@Body data: Map<String, @JvmSuppressWildcards Any>): CommonResponse

    // เพิ่มสินค้าลงตะกร้า
    @POST("add-to-cart")
    suspend fun addToCart(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<CommonResponse>

    // ดึงสินค้าในตะกร้า
    @GET("cart/{userId}")
    suspend fun getCartItems(@Path("userId") userId: Int): List<CartClass>

    @Multipart
    @POST("upload-payment")
    suspend fun uploadPayment(
        @Part("order_id") orderId: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part slipImage: MultipartBody.Part
    ): Response<CommonResponse>

    @DELETE("deleteCart/{id_cart}")
    suspend fun deleteCartItem(@Path("id_cart") id: Int): Response<CommonResponse>

    @Multipart
    @POST("rider/complete-job")
    suspend fun completeRiderJob(
        @Part("id_order") idOrder: RequestBody,
        @Part("id_rider") idRider: RequestBody,
        @Part delivery_image: MultipartBody.Part?
    ): Response<CommonResponse>

    @GET("orders/customer/{userId}")
    suspend fun getCustomerOrders(@Path("userId") userId: Int): List<AdminOrderClass>

    // เพิ่มไว้ใน interface ProjectAPI
    @PUT("update-rider") // ตรวจสอบว่ามี / หรือไม่มี ตามฐาน URL ของคุณ
    suspend fun updateRider(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<CommonResponse>

    @DELETE("delete-rider/{email}")
    suspend fun deleteRider(@Path("email") email: String): Response<CommonResponse>

    // เพิ่มใน ProjectAPI.kt
    @GET("flowers")
    suspend fun getAllFlowers(): List<FlowerClass>

    @POST("add-flower")
    suspend fun addFlower(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<CommonResponse>

    @DELETE("delete-flower/{id}")
    suspend fun deleteFlower(@Path("id") id: Int): Response<CommonResponse>

    // เพิ่มเข้าไปใน interface ProjectAPI
    @PUT("update-flower")
    suspend fun updateFlower(
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Response<CommonResponse>

    // เพิ่มไว้ใน interface ProjectAPI.kt
    @PUT("update-cart-quantity")
    suspend fun updateCartQuantity(
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Response<CommonResponse>

    @FormUrlEncoded
    @PUT("updateProfile")
    suspend fun updateStudentProfile(
        @Field("id_user") id_user: Int,
        @Field("name") name: String,
        @Field("gender") gender: String
    ): Response<LoginClass>
}