package video.api.player.extensions

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Hides the system UI: status bar, navigation bar and system bars.
 */
fun Window.hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    WindowInsetsControllerCompat(this, this.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}