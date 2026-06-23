package com.example.fittrackpro

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.fittrackpro.theme.FitTrackProTheme
import com.example.fittrackpro.ui.auth.ForgotPasswordScreen
import com.example.fittrackpro.ui.auth.LoginScreen
import com.example.fittrackpro.ui.auth.RegisterScreen
import com.example.fittrackpro.ui.main.MainScreen
import com.example.fittrackpro.ui.settings.SettingsScreen

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("fittrackpro_prefs", Context.MODE_PRIVATE) }
    val authPrefs = remember { context.getSharedPreferences("fittrackpro_auth_prefs", Context.MODE_PRIVATE) }
    var darkThemePref by remember { mutableStateOf(prefs.getString("theme_preference", "system") ?: "system") }

    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (darkThemePref) {
        "light" -> false
        "dark" -> true
        else -> isSystemDark
    }

    FitTrackProTheme(darkTheme = darkTheme) {
        val initialKey = if (authPrefs.getString("current_user_email", null) != null) Main else Login
        val backStack = rememberNavBackStack(initialKey)

        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Login> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.add(Main)
                            backStack.remove(Login)
                        },
                        onRegisterClick = { backStack.add(Register) },
                        onForgotPasswordClick = { backStack.add(ForgotPassword) }
                    )
                }
                entry<Register> {
                    RegisterScreen(
                        onRegisterSuccess = {
                            backStack.add(Main)
                            backStack.remove(Register)
                            backStack.remove(Login)
                        },
                        onBackToLogin = { backStack.removeLastOrNull() }
                    )
                }
                entry<ForgotPassword> {
                    ForgotPasswordScreen(
                        onBackToLogin = { backStack.removeLastOrNull() }
                    )
                }
                entry<Main> {
                    MainScreen(
                        onItemClick = { navKey -> backStack.add(navKey) },
                        onLogout = {
                            backStack.add(Login)
                            backStack.remove(Main)
                        }
                    )
                }
                entry<Settings> {
                    SettingsScreen(
                        onThemeChanged = { newTheme ->
                            darkThemePref = newTheme
                        },
                        onBackClick = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
