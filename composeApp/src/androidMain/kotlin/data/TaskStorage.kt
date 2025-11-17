package data



import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object TaskStorage {

    private fun getFile(): File {
        val dir = File(System.getProperty("java.io.tmpdir"))
        val file = File(dir, "tasks.json")
        if (!file.exists()) file.createNewFile()
        return file
    }

    fun saveTask(task: TaskItem) {
        val file = getFile()

        val existing = loadTasks().toMutableList()
        existing.add(task)

        file.writeText(Json.encodeToString(existing))
    }

    fun loadTasks(): List<TaskItem> {
        val file = getFile()
        if (!file.exists() || file.readText().isEmpty()) return emptyList()

        return try {
            Json.decodeFromString(file.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }
}
