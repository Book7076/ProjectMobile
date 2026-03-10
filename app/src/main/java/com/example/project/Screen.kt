package com.example.project

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object AdminScreen : Screen("admin")
    data object AdminProfileScreen : Screen("admin_profile")
    data object EditProfile : Screen("edit_profile") // ✅ เพิ่มสำหรับหน้าแก้ไขโปรไฟล์
    data object CustomerScreen : Screen("customer")
    data object CustomerFlowerDetail : Screen("customer_flower_detail/{flowerName}") {
        fun createRoute(name: String) = "customer_flower_detail/$name"
    }

    object CustomerFlowerMeaning : Screen("flower_meaning/{flowerName}")
    object CustomerFlowerColorGrid : Screen("flower_color_grid/{flowerName}")
    object CustomerFlowerFinal : Screen("flower_final/{flowerId}")

    data object RiderScreen : Screen("rider")
    data object AdminRiderManagement : Screen("admin_rider_management")

    data object AdminFlower : Screen("admin_flower")

    data object AdminAddFlower : Screen("admin_add_flower")

    // เพิ่มเข้าไปใน sealed class Screen
    data object AdminFlowerDetail : Screen("admin_flower_detail/{flowerName}") {
        fun createRoute(name: String) = "admin_flower_detail/$name"
    }
    data object AdminEditFlower : Screen("admin_edit_flower/{flowerId}") {
        fun createRoute(id: Int) = "admin_edit_flower/$id"
    }

    // ✅ เพิ่มเส้นทางสำหรับหน้าแก้ไข Rider (รับ parameter email)
    object EditRider : Screen("edit_rider/{email}") {
        fun createRoute(email: String) = "edit_rider/$email"
    }

    data object RiderProfile : Screen("rider_profile")
    data object Profile : Screen("profile")
    data object OrderHistory : Screen("order_history")
    data object AddAddress : Screen("add_address")
    data object AddressList : Screen("address_list")

    object CheckoutSummary : Screen("checkout_summary/{productName}/{price}") {
        fun createRoute(productName: String, price: Double) = "checkout_summary/$productName/$price"
    }

    data object Checkout : Screen("checkout")
    data object ThankYou : Screen("thank_you")
}