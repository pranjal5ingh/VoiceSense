package com.example.voicesense

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voicesense.data.MainViewModel
import com.example.voicesense.ui.navigation.Routes
import com.example.voicesense.ui.screens.*

class MainActivity : ComponentActivity() {

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            // Optional: check which permissions were granted/denied
            // val cameraGranted = grants[Manifest.permission.CAMERA] == true
            // val audioGranted = grants[Manifest.permission.RECORD_AUDIO] == true
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ensureCameraAndAudioPermissions()

        setContent {
            val navController = rememberNavController()
            val vm: MainViewModel = viewModel()

            NavHost(
                navController = navController,
                startDestination = Routes.START
            ) {
                composable(Routes.START) {
                    StartScreen(onStartClick = { navController.navigate(Routes.NOISE_TEST) })
                }
                composable(Routes.NOISE_TEST) {
                    NoiseTestScreen(onPassed = { navController.navigate(Routes.TASK_SELECTION) })
                }
                composable(Routes.TASK_SELECTION) {
                    TaskSelectionScreen(
                        onTextReading = { navController.navigate(Routes.TEXT_READING) },
                        onImageDescription = { navController.navigate(Routes.IMAGE_DESCRIPTION) },
                        onPhotoCapture = { navController.navigate(Routes.PHOTO_CAPTURE) },
                        onHistory = { navController.navigate(Routes.TASK_HISTORY) }
                    )
                }
                composable(Routes.TEXT_READING) {
                    TextReadingScreen(viewModel = vm, onDone = { navController.popBackStack() })
                }
                composable(Routes.IMAGE_DESCRIPTION) {
                    ImageDescriptionScreen(viewModel = vm, onDone = { navController.popBackStack() })
                }
                composable(Routes.PHOTO_CAPTURE) {
                    PhotoCaptureScreen(viewModel = vm, onDone = { navController.popBackStack() })
                }
                composable(Routes.TASK_HISTORY) {
                    TaskHistoryScreen(tasksFlow = vm.tasks, onBack = { navController.popBackStack() })
                }
            }
        }
    }

    private fun ensureCameraAndAudioPermissions() {
        val needed = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.CAMERA
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            needed += Manifest.permission.RECORD_AUDIO
        }

        if (needed.isNotEmpty()) {
            requestPermissions.launch(needed.toTypedArray())
        }
    }
}