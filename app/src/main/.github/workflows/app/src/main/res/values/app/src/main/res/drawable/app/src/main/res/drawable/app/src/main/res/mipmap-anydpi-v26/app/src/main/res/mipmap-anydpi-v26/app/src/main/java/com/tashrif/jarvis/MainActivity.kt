package com.tashrif.jarvis

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tashrif.jarvis.service.JarvisForegroundService
import com.tashrif.jarvis.ui.ChatScreen
import com.tashrif.jarvis.ui.ChatViewModel
import com.tashrif.jarvis.ui.HomeScreen
import com.tashrif.jarvis.ui.theme.JarvisTheme

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
}

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()
        startKahuguService()

        setContent {
            JarvisTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                when (currentScreen) {
                    is Screen.Home -> HomeScreen(
                        onOpenChat = { currentScreen = Screen.Chat }
                    )
                    is Screen.Chat -> ChatScreen(viewModel = chatViewModel)
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun startKahuguService() {
        val serviceIntent = Intent(this, JarvisForegroundService::class.java)
        startForegroundService(serviceIntent)
    }
}
