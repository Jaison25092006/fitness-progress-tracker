package com.example.fittrackpro

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey
@Serializable data object Login : NavKey
@Serializable data object Register : NavKey
@Serializable data object ForgotPassword : NavKey
@Serializable data object Settings : NavKey
