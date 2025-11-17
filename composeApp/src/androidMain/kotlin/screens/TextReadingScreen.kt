package screens

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import data.TaskItem
import data.TaskStorage
import navigation.NavController
import navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun TextReadingScreen(navController: NavController) {

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

    LaunchedEffect(Unit) { vm.fetchText() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        BackTopBar(
            title = "Text Reading Task",
            onBack = { navController.navigate(Screen.TaskSelection) }
        )

        Spacer(Modifier.height(20.dp))

        if (vm.isLoading) {
            Text("Loading text...")
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Gray.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            ) {
                Text(
                    vm.textToRead,
                    fontSize = 16.sp
                )
            }
        }


        Spacer(Modifier.height(20.dp))

        Text("Read the passage aloud in your native language.")

        Spacer(Modifier.height(20.dp))

        val hasPermission = rememberAudioPermission()

        val infiniteTransition = rememberInfiniteTransition()

        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isRecording) 1.15f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val pulseGlow by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = if (isRecording) 0.9f else 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
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
                        shape = CircleShape
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
                        .size(130.dp)
                        .graphicsLayer {
                            alpha = pulseGlow
                        }
                        .background(Color.Red.copy(alpha = 0.15f), CircleShape)
                )

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic",
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

        Spacer(Modifier.height(16.dp))

        // error handling
        if (vm.tooShortError) Text("Recording too short (min 10s)", color = Color.Red)
        if (vm.tooLongError) Text("Recording too long (max 20s)", color = Color.Red)

        Spacer(Modifier.height(20.dp))

        if (showResult && !vm.tooShortError && !vm.tooLongError && vm.audioPath != null) {
            Spacer(Modifier.height(20.dp))
            Text("Playback Preview", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            PlaybackCardPreview(
                duration = vm.durationSec,
                onPlay = { vm.playAudio() },
                onStop = { vm.stopAudio() }
            )

        }

        // CHECKBOXES + SUBMIT
        if (showResult && vm.audioPath != null && !vm.tooShortError && !vm.tooLongError) {
            CheckboxRow("No background noise", vm.checkbox1) { vm.checkbox1 = it }
            CheckboxRow("No mistakes while reading", vm.checkbox2) { vm.checkbox2 = it }
            CheckboxRow("Beech me koi galti nahi hai", vm.checkbox3) { vm.checkbox3 = it }

            Spacer(Modifier.height(20.dp))

            Row {
                Button(onClick = {
                    vm.audioPath = null
                    showResult = false
                    vm.stopAudio()
                }) { Text("Record Again") }

                Spacer(Modifier.width(16.dp))

                Button(
                    enabled = vm.checkbox1 && vm.checkbox2 && vm.checkbox3,
                    onClick = {
                        vm.stopAudio()

                        TaskStorage.saveTask(
                            TaskItem(
                                id = System.currentTimeMillis(),
                                task_type = "text_reading",
                                text = vm.textToRead,
                                audio_path = vm.audioPath,
                                duration_sec = vm.durationSec,
                                timestamp = "2025-11-12T10:00:00"
                            )
                        )
                        navController.navigate(Screen.TaskSelection)
                    }
                ) { Text("Submit") }
            }
        }
    }
}

@Composable
fun rememberAudioPermission(): Boolean {
    val context = LocalContext.current
    val permission = android.Manifest.permission.RECORD_AUDIO

    var granted by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }

    if (!granted) {
        LaunchedEffect(Unit) { launcher.launch(permission) }
    }

    return granted
}

@Composable
fun CheckboxRow(label: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheck)
        Text(label)
    }
}

@Composable
fun PlaybackCardPreview(
    duration: Int,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    // Fake animated progress when playing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            for (i in 1..100) {
                delay((duration * 10).toLong())
                progress = i / 100f
            }
            isPlaying = false
            progress = 0f
            onStop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE7ECF5), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // PLAY / STOP CIRCLE BUTTON
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1976D2), CircleShape)
                    .clickable {
                        if (!isPlaying) {
                            isPlaying = true
                            onPlay()
                        } else {
                            isPlaying = false
                            progress = 0f
                            onStop()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(16.dp))

            // PROGRESS BAR LINE
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .height(6.dp)
                    .weight(1f),
                color = Color(0xFF1976D2),
                trackColor = Color.LightGray
            )
        }
    }
}
