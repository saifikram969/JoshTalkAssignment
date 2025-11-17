package data



import kotlinx.serialization.Serializable

@Serializable
data class TaskItem(
    val id: Long,
    val task_type: String,
    val text: String? = null,
    val image_url: String? = null,
    val image_path: String? = null,
    val audio_path: String? = null,
    val duration_sec: Int,
    val timestamp: String
)
