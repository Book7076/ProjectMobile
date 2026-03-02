package com.example.project

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object AdminScreen : Screen("admin")
    object CustomerScreen : Screen("customer")
    object RiderScreen : Screen("customer")
}
