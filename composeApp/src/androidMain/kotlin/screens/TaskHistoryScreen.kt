package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import data.TaskItem
import data.TaskStorage
import navigation.NavController
import navigation.Screen

@Composable
fun TaskHistoryScreen(navController: NavController) {

    val tasks = remember { TaskStorage.loadTasks() }
    val totalDuration = tasks.sumOf { it.duration_sec }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        BackTopBar(
            title = "Task History",
            onBack = { navController.navigate(Screen.TaskSelection) }
        )

        Spacer(Modifier.height(16.dp))

        // Header Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Tasks", color = Color.White, fontSize = 14.sp)
                    Text("${tasks.size}", color = Color.White, fontSize = 22.sp)
                }
                Column {
                    Text("Total Duration", color = Color.White, fontSize = 14.sp)
                    Text("${totalDuration} sec", color = Color.White, fontSize = 22.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            items(tasks) { task ->
                TaskHistoryCard(task)
            }
        }
    }
}

@Composable
fun TaskHistoryCard(task: TaskItem) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {

                val icon = when (task.task_type) {
                    "text_reading" -> Icons.Default.TextSnippet
                    "image_description" -> Icons.Default.Image
                    "photo_capture" -> Icons.Default.Image
                    else -> Icons.Default.Mic
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = task.task_type.replace("_", " ").uppercase(),
                    fontSize = 18.sp,
                    color = Color(0xFF1E88E5)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Task ID: ${task.id}", fontSize = 12.sp, color = Color.Gray)
            Text("Duration: ${task.duration_sec} sec", fontSize = 14.sp)
            Text("Timestamp: ${task.timestamp}", fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(10.dp))

            // TEXT PREVIEW
            task.text?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Text(
                        it.take(60) + "...",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            // IMAGE PREVIEW
            task.image_url?.let {
                Spacer(Modifier.height(10.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // LOCAL IMAGE PREVIEW
            task.image_path?.let {
                Spacer(Modifier.height(10.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
