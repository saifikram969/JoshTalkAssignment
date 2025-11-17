package screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import navigation.NavController
import navigation.Screen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NoiseTestScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    var isTesting by remember { mutableStateOf(false) }
    var dbValue by remember { mutableStateOf(0f) }
    var testFinished by remember { mutableStateOf(false) }

    val animatedDb by animateFloatAsState(
        targetValue = dbValue,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Text("Sample Task", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        Text("Test Ambient Noise Level", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(
            "Before you can start the call we will have to check your ambient noise level.",
            fontSize = 15.sp
        )

        Spacer(Modifier.height(30.dp))

        NoiseGauge(db = animatedDb)

        Spacer(Modifier.height(30.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (isTesting) return@Button
                isTesting = true
                testFinished = false

                scope.launch {
                    repeat(20) {
                        dbValue = (0..60).random().toFloat()
                        delay(120)
                    }
                    isTesting = false
                    testFinished = true
                }
            }
        ) {
            Text(if (isTesting) "Testing..." else "Start Test")
        }

        Spacer(Modifier.height(20.dp))

        if (testFinished) {
            val isGood = dbValue < 40

            Text(
                if (isGood) "Good to proceed" else "Please move to a quieter place",
                color = if (isGood) Color(0xFF2E7D32) else Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(20.dp))

            if (isGood) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(Screen.TaskSelection) }
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun NoiseGauge(db: Float) {
    val angle = (db / 60f) * 180f

    Canvas(
        modifier = Modifier
            .size(250.dp)
            .padding(8.dp)
    ) {

        val width = size.width
        val height = size.height
        val radius = width / 2.2f
        val center = Offset(width / 2, height)

        drawArc(
            color = Color(0xFF1976D2),
            startAngle = 180f,
            sweepAngle = 150f,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )

        drawArc(
            color = Color(0xFFD32F2F),
            startAngle = 330f,
            sweepAngle = 30f,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )

        val pointerAngle = Math.toRadians((180 + angle).toDouble())
        val pointerX = center.x + radius * cos(pointerAngle).toFloat()
        val pointerY = center.y + radius * sin(pointerAngle).toFloat()

        drawLine(
            color = Color.LightGray,
            strokeWidth = 12f,
            cap = StrokeCap.Round,
            start = center,
            end = Offset(pointerX, pointerY)
        )

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "${db.toInt()} dB",
                center.x,
                center.y - 60,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 50f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
