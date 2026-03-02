package com.example.project

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ProjectViewModel : ViewModel() {
    private var _loginResult by mutableStateOf<LoginClass?>(null)
    val loginResult get() = _loginResult

    private var _studentProfile by mutableStateOf<ProfileClass?>(null)
    val studentProfile get() = _studentProfile

    private var _errorMessage by mutableStateOf("")
    val errorMessage get() = _errorMessage

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
                    // ดึง Error Message จริงๆ จาก Server (เช่น "User not found" หรือ "Invalid password")
                    val errorBody = response.errorBody()?.string()
                    val errorObj = Gson().fromJson(errorBody, LoginClass::class.java)
                    _loginResult = errorObj // ส่ง Object ที่มี message จริงๆ ไปให้ UI
                }
            } catch (e: Exception) {
                _loginResult = LoginClass(error = true, message = "Connection Error", email = null, role = null)
            }
        }
    }

    // Get Profile Function
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

    // Register Function
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
                            val errorData = Gson().fromJson(errorRawString, RegisterResponse::class.java)
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
}