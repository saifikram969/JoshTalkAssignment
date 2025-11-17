package screens

import android.app.Application
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import data.TaskItem
import data.TaskStorage
import navigation.NavController
import navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ImageDescriptionScreen(navController: NavController) {

    val context = LocalContext.current.applicationContext as Application
    val vm = remember { TextReadingViewModel(context) }

    var showResult by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }

    val hasPermission = rememberAudioPermission()

    val imageUrl =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-WOJ4GT_08RQoJfeYrv9lORW0gUcHJD1W-A&s"

    // Image loading states
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            elapsedTime = 0
            while (isRecording) {
                delay(1000)
                elapsedTime++
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        BackTopBar(
            title = "Image Description Task",
            onBack = { navController.navigate(Screen.TaskSelection) }
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {

            if (isLoading) Text("Loading image...")

            AsyncImage(
                model = imageUrl,
                contentDescription = "Task Image",
                modifier = Modifier.fillMaxSize(),
                onLoading = { isLoading = true },
                onSuccess = {
                    isLoading = false
                    hasError = false
                },
                onError = {
                    hasError = true
                    errorMessage = it.result.throwable.message
                    isLoading = false
                }
            )

            if (hasError) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load image", color = Color.Red)
                    Text(errorMessage ?: "Unknown error", color = Color.Red, fontSize = 12.sp)
                    Button(onClick = { isLoading = true; hasError = false }) {
                        Text("Retry")
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Describe what you see in your native language.")

        Spacer(Modifier.height(25.dp))

        val infinite = rememberInfiniteTransition()

        val pulseScale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = if (isRecording) 1.18f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val pulseGlow by infinite.animateFloat(
            initialValue = 0.3f,
            targetValue = if (isRecording) 0.9f else 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(700),
                repeatMode = RepeatMode.Reverse
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
                        .background(Color.Red.copy(alpha = 0.15f), CircleShape)
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

        //error handling
        Spacer(Modifier.height(15.dp))

        if (vm.tooShortError) Text("Recording too short (min 10s)", color = Color.Red)
        if (vm.tooLongError) Text("Recording too long (max 20s)", color = Color.Red)

        if (showResult && vm.audioPath != null && !vm.tooShortError && !vm.tooLongError) {

            Spacer(Modifier.height(20.dp))

            Text("Playback Preview", fontSize = 18.sp, fontWeight = FontWeight.Bold)

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

                Spacer(Modifier.width(15.dp))

                Button(
                    onClick = {
                        vm.stopAudio()

                        TaskStorage.saveTask(
                            TaskItem(
                                id = System.currentTimeMillis(),
                                task_type = "image_description",
                                image_url = imageUrl,
                                audio_path = vm.audioPath,
                                duration_sec = vm.durationSec,
                                timestamp = "2025-11-12T10:10:00"
                            )
                        )
                        navController.navigate(Screen.TaskSelection)
                    }
                ) {
                    Text("Submit")
                }
            }
        }
    }
}
