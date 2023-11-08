package video.api.compose.player.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import video.api.compose.player.ApiVideoPlayer
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.views.ApiVideoExoPlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /**
             * Use a [Surface] to set the background color of the player in fullscreen mode.
             */
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black,
            ) {
                ApiVideoPlayer(
                    videoOptions = VideoOptions("vi77Dgk0F8eLwaFOtC5870yn", VideoType.VOD),
                    viewFit = ApiVideoExoPlayerView.ViewFit.FitHeight,
                    autoplay = true
                )
            }
        }
    }
}

