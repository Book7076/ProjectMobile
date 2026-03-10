package com.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPref = SharedPreferencesManager(context)
    val projectViewModel: ProjectViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // --- Authentication ---
        composable(Screen.Login.route) { LoginScreen(navController, projectViewModel) }
        composable(Screen.Register.route) { RegisterScreen(navController, projectViewModel) }

        // --- Admin Roles ---
        composable(Screen.AdminScreen.route) { AdminMainScreen(navController, projectViewModel) }
        composable(Screen.AdminProfileScreen.route) { AdminProfileScreen(navController) }

        composable(Screen.EditProfile.route) {
            CustomerEditProfileScreen(navController, projectViewModel)
        }

        composable(Screen.AdminRiderManagement.route) {
            LaunchedEffect(Unit) { projectViewModel.fetchAllRiders() }
            AdminRiderManagement(navController, projectViewModel)
        }

        composable(Screen.AdminFlower.route) {
            AdminFlowerScreen(navController, projectViewModel)
        }

        composable(Screen.AdminAddFlower.route) {
            AdminAddFlowerScreen(navController, projectViewModel)
        }

        composable(
            route = "admin_flower_detail/{flowerName}",
            arguments = listOf(navArgument("flowerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val flowerName = backStackEntry.arguments?.getString("flowerName") ?: ""
            AdminFlowerDetailScreen(navController, projectViewModel, flowerName)
        }

// หน้าแก้ไขข้อมูลดอกไม้ (กดมาจาก AdminFlowerDetailScreen)
        composable(
            route = "admin_edit_flower/{flowerId}",
            arguments = listOf(navArgument("flowerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val flowerId = backStackEntry.arguments?.getInt("flowerId") ?: 0
            AdminEditFlowerScreen(navController, projectViewModel, flowerId)
        }

        // ✅ เพิ่มหน้า Edit Rider ตรงนี้
        composable(
            route = Screen.EditRider.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EditRiderScreen(navController, projectViewModel, email)
        }

        // --- Customer Roles ---
        composable(Screen.CustomerScreen.route) { CustomerMainScreen(navController, projectViewModel) }
        composable(Screen.Profile.route) { CustomerProfileScreen(navController, projectViewModel) }
        composable(Screen.AddAddress.route) { AddAddressScreen(navController, projectViewModel) }
        composable(Screen.AddressList.route) { AddressListScreen(navController, projectViewModel) }

        composable(
            route = "customer_flower_detail/{flowerName}",
            arguments = listOf(navArgument("flowerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val flowerName = backStackEntry.arguments?.getString("flowerName") ?: ""
            CustomerFlowerDetailScreen(navController, projectViewModel, flowerName)
        }


        composable(Screen.Checkout.route) {
            val userId = sharedPref.getSavedIdUser()
            LaunchedEffect(userId) {
                if (userId != 0) {
                    projectViewModel.fetchCart(userId)
                    projectViewModel.fetchAddresses(userId)
                }
            }
            CheckoutScreen(navController, projectViewModel)
        }

        composable("flower_meaning/{flowerName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("flowerName") ?: ""
            CustomerFlowerMeaningScreen(navController, projectViewModel, name)
        }
        composable("flower_color_grid/{flowerName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("flowerName") ?: ""
            CustomerFlowerColorGridScreen(navController, projectViewModel, name)
        }
        composable(
            route = "flower_final/{flowerId}",
            arguments = listOf(navArgument("flowerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("flowerId") ?: 0
            CustomerFlowerFinalScreen(navController, projectViewModel, id)
        }

        composable(Screen.ThankYou.route) {
            ThankYouStep(onBackHome = {
                navController.navigate(Screen.CustomerScreen.route) {
                    popUpTo(Screen.CustomerScreen.route) { inclusive = true }
                }
            })
        }

        // --- Rider Roles ---
        composable(Screen.RiderScreen.route) { RiderMainScreen(navController, projectViewModel) }
        composable(Screen.RiderProfile.route) { RiderProfileScreen(navController) }

        // --- Order History ---
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController, projectViewModel, initialStatus = "all")
        }

        composable(
            route = "order_history/{status}",
            arguments = listOf(navArgument("status") {
                type = NavType.StringType
                defaultValue = "all"
            })
        ) { backStackEntry ->
            val status = backStackEntry.arguments?.getString("status") ?: "all"
            OrderHistoryScreen(navController, projectViewModel, initialStatus = status)
        }
    }
}