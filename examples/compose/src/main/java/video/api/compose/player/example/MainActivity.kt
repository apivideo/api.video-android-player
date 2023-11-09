package video.api.compose.player.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import video.api.compose.player.ApiVideoPlayer
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.views.ApiVideoExoPlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiVideoPlayer(
                videoOptions = VideoOptions("vi77Dgk0F8eLwaFOtC5870yn", VideoType.VOD),
                viewFit = ApiVideoExoPlayerView.ViewFit.FitHeight,
                autoplay = true
            )
        }
    }
}

