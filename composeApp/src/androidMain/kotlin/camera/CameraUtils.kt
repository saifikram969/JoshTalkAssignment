package camera



import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.File

object CameraUtils {

    fun createImageFile(activity: Activity): File {
        val file = File(
            activity.cacheDir,
            "captured_photo.jpg"
        )
        file.createNewFile()
        return file
    }

    fun startCamera(
        activity: Activity,
        launcher: ActivityResultLauncher<Intent>,
        photoFile: File
    ) {
        val uri = FileProvider.getUriForFile(
            activity,
            activity.packageName + ".provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        launcher.launch(intent)
    }
}
