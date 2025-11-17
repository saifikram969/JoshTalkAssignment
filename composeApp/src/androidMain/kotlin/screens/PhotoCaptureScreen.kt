package screens

import android.app.Application
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import camera.CameraUtils
import data.TaskItem
import data.TaskStorage
import navigation.NavController
import navigation.Screen
import java.io.File
import kotlinx.coroutines.delay

@Composable
fun PhotoCaptureScreen(navController: NavController) {

    val activity = LocalActivity.current
    val context = LocalContext.current.applicationContext as Application
    val vm = remember { TextReadingViewModel(context) }
    var showResult by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            elapsedTime = 0
            while (isRecording) {
                delay(1000)
                elapsedTime++
            }
        }
    }

    var photoFile by remember { mutableStateOf<File?>(null) }
    var photoUri by remember { mutableStateOf<String?>(null) }

    var description by remember { mutableStateOf(TextFieldValue("")) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        photoUri = photoFile?.absolutePath
    }

    val hasPermission = rememberAudioPermission()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {

        BackTopBar(
            title = "Photo Capture Task",
            onBack = { navController.navigate(Screen.TaskSelection) }
        )

        Spacer(Modifier.height(20.dp))

        // photo capture btn
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF000000), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val act = activity ?: return@detectTapGestures
                                val f = CameraUtils.createImageFile(act)
                                photoFile = f
                                CameraUtils.startCamera(act, cameraLauncher, f)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // PHOTO PREVIEW
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Captured Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // DESCRIPTION
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the photo in your language") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(25.dp))

        val infinite = rememberInfiniteTransition()

        val pulseScale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = if (isRecording) 1.18f else 1f,
            animationSpec = infiniteRepeatable(
                tween(600, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            )
        )

        val pulseGlow by infinite.animateFloat(
            initialValue = 0.3f,
            targetValue = if (isRecording) 0.9f else 0.3f,
            animationSpec = infiniteRepeatable(
                tween(700),
                RepeatMode.Reverse
            )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .background(
                        if (isRecording) Color(0xFF4CAF50) else Color(0xFF2196F3),
                        CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {

                                if (!hasPermission) return@detectTapGestures

                                showResult = false
                                isRecording = true
                                vm.startRecording()

                                tryAwaitRelease()

                                isRecording = false
                                vm.stopRecording()
                                showResult = true
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .graphicsLayer { alpha = pulseGlow }
                        .background(Color.Red.copy(alpha = 0.18f), CircleShape)
                )

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            if (isRecording) {
                Text(
                    text = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60),
                    fontSize = 20.sp,
                    color = Color.Red
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        //error handling
        if (vm.tooShortError) Text("Recording too short (min 10s)", color = Color.Red)
        if (vm.tooLongError) Text("Recording too long (max 20s)", color = Color.Red)

        if (
            showResult &&
            vm.audioPath != null &&
            !vm.tooShortError &&
            !vm.tooLongError
        ) {

            Spacer(Modifier.height(20.dp))

            Text("Playback Preview", fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))

            PlaybackCardPreview(
                duration = vm.durationSec,
                onPlay = { vm.playAudio() },
                onStop = { vm.stopAudio() }
            )


            Spacer(Modifier.height(20.dp))

            Row {

                Button(onClick = {
                    vm.audioPath = null
                    showResult = false
                    vm.stopAudio()
                }) {
                    Text("Record Again")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    enabled = photoUri != null && description.text.isNotEmpty(),
                    onClick = {
                        vm.stopAudio()

                        TaskStorage.saveTask(
                            TaskItem(
                                id = System.currentTimeMillis(),
                                task_type = "photo_capture",
                                image_path = photoUri,
                                audio_path = vm.audioPath,
                                duration_sec = vm.durationSec,
                                timestamp = "2025-11-12T10:15:00"
                            )
                        )

                        navController.navigate(Screen.TaskSelection)
                    }
                ) {
                    Text("Submit")
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
