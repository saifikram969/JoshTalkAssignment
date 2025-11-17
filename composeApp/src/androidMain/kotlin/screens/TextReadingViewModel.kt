package screens

import android.app.Application
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import network.ApiService
import network.ConnectivityObserver
import java.io.File

class TextReadingViewModel(
    private val app: Application
) {

    private val connectivityObserver = ConnectivityObserver(app)

    var textToRead by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var audioPath by mutableStateOf<String?>(null)
    var durationSec by mutableStateOf(0)
    var tooShortError by mutableStateOf(false)
    var tooLongError by mutableStateOf(false)
    var checkbox1 by mutableStateOf(false)
    var checkbox2 by mutableStateOf(false)
    var checkbox3 by mutableStateOf(false)

    private var autoRetryStarted = false
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var startTime = 0L

    init {
        observeInternet()
    }

    private fun observeInternet() {
        if (autoRetryStarted) return
        autoRetryStarted = true

        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observe().collectLatest { connected ->
                if (connected && textToRead.contains("No internet")) {
                    fetchText()
                }
            }
        }
    }

    fun fetchText() {
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val description = ApiService.getProductDescription()
                textToRead = description
            } catch (e: Exception) {
                textToRead = "No internet connection. Please try again."
            } finally {
                isLoading = false
            }
        }
    }

    fun startRecording() {
        try {
            val file = File(app.filesDir, "audio_${System.currentTimeMillis()}.m4a")
            audioPath = file.absolutePath

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioPath)
                prepare()
                start()
            }

            startTime = System.currentTimeMillis()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (_: Exception) {
        }

        recorder = null

        durationSec = ((System.currentTimeMillis() - startTime) / 1000).toInt()

        tooShortError = durationSec < 10
        tooLongError = durationSec > 20
    }

    fun playAudio() {
        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAudio() {
        try {
            player?.stop()
            player?.release()
        } catch (_: Exception) {
        }

        player = null
    }
}
