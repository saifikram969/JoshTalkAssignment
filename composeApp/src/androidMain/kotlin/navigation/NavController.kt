package navigation



import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class NavController {
    var currentScreen by mutableStateOf<Screen>(Screen.Start)

    fun navigate(screen: Screen) {
        currentScreen = screen
    }
}
