package navigation


sealed class Screen {
    object Start : Screen()
    object NoiseTest : Screen()
    object TaskSelection : Screen()
    object TextReading : Screen()
    object ImageDescription : Screen()
    object PhotoCapture : Screen()
    object TaskHistory : Screen()
}
