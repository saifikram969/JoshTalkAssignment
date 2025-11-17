package org.example.joshtalks

import StartScreen
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import navigation.NavController
import navigation.Screen
import screens.ImageDescriptionScreen
import screens.NoiseTestScreen
import screens.PhotoCaptureScreen
import screens.TaskHistoryScreen
import screens.TaskSelectionScreen
import screens.TextReadingScreen

@Composable
@Preview
fun App() {
    val activity = LocalActivity.current
    val navController = remember { NavController() }

    // Handle Android system back button
    BackHandler {
        when (navController.currentScreen) {

            Screen.NoiseTest,
            Screen.TaskSelection -> {
                // Go back to START screen
                navController.navigate(Screen.Start)
            }

            Screen.TextReading,
            Screen.ImageDescription,
            Screen.PhotoCapture,
            Screen.TaskHistory -> {
                // Go back to task selection
                navController.navigate(Screen.TaskSelection)
            }

            Screen.Start -> {

                activity?.finish()
            }
        }
    }

    MaterialTheme {
        when (navController.currentScreen) {
            Screen.Start -> StartScreen(navController)
            Screen.NoiseTest -> NoiseTestScreen(navController)
            Screen.TaskSelection -> TaskSelectionScreen(navController)
            Screen.TextReading -> TextReadingScreen(navController)
            Screen.ImageDescription -> ImageDescriptionScreen(navController)
            Screen.PhotoCapture -> PhotoCaptureScreen(navController)
            Screen.TaskHistory -> TaskHistoryScreen(navController)
        }

}}