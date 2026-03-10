    package com.example.project


    import android.content.Context
    import android.content.SharedPreferences

    class SharedPreferencesManager(context: Context) {
        private val preferences: SharedPreferences = context.getSharedPreferences("flower_daily_prefs", Context.MODE_PRIVATE)

        companion object {
            private const val KEY_IS_LOGGED_IN = "is_logged_in"
            private const val KEY_EMAIL = "email"
            private const val KEY_ROLE = "role"
            private const val KEY_NAME = "name"
            private const val KEY_USER_ID = "id_user"
        }

        fun saveLoginStatus(isLoggedIn: Boolean, email: String, role: String, name: String, id_user: Int) {
            preferences.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
                putString(KEY_EMAIL, email)
                putString(KEY_ROLE, role)
                putString(KEY_NAME, name)
                putInt(KEY_USER_ID, id_user)
                apply()
            }
        }



        fun getSavedEmail(): String = preferences.getString(KEY_EMAIL, "") ?: ""
        fun getSavedName(): String = preferences.getString(KEY_NAME, "") ?: ""
        fun getSavedIdUser(): Int {
            return preferences.getInt(KEY_USER_ID, 0)
        }

        fun logout(rememberId: Boolean) {
            preferences.edit().apply {
                remove(KEY_IS_LOGGED_IN)
                remove(KEY_ROLE)
                if (!rememberId) remove(KEY_EMAIL)
                apply()
            }
        }
    }