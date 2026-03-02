package com.example.project

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    val projectViewModel: ProjectViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController, projectViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, projectViewModel)
        }

        composable(Screen.AdminScreen.route) {
            AdminMainScreen(navController, projectViewModel)
        }

        composable(Screen.CustomerScreen.route) {
            CustomerMainScreen(navController, projectViewModel)
        }

        composable(Screen.RiderScreen.route) {
            RiderMainScreen(navController, projectViewModel)
        }
    }
}
