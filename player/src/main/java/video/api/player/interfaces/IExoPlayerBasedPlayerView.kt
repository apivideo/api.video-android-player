package video.api.player.interfaces

import androidx.media3.ui.PlayerView
import video.api.player.ApiVideoPlayerController

interface IExoPlayerBasedPlayerView : ApiVideoPlayerController.Listener {
    val playerView: PlayerView
}