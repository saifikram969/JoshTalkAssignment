package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import navigation.NavController
import navigation.Screen

@Composable
fun TaskSelectionScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "Choose a task to begin",
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        //cards
        TaskCard(
            title = "Text Reading Task",
            bgColor = Color(0xFFE3F2FD),
            icon = Icons.Default.Mic
        ) {
            navController.navigate(Screen.TextReading)
        }

        Spacer(modifier = Modifier.height(16.dp))


        TaskCard(
            title = "Image Description Task",
            bgColor = Color(0xFFFFF3E0),
            icon = Icons.Default.Image
        ) {
            navController.navigate(Screen.ImageDescription)
        }

        Spacer(modifier = Modifier.height(16.dp))


        TaskCard(
            title = "Photo Capture Task",
            bgColor = Color(0xFFE8F5E9),
            icon = Icons.Default.CameraAlt
        ) {
            navController.navigate(Screen.PhotoCapture)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TaskCard(
            title = "View Task History",
            bgColor = Color(0xFFF3E5F5),
            icon = Icons.Default.History
        ) {
            navController.navigate(Screen.TaskHistory)
        }
    }
}

@Composable
fun TaskCard(title: String, bgColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = Color.Black,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}
