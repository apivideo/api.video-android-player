package video.api.player.interfaces

import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.ApiVideoPlayerController

interface IExoPlayerBasedPlayerView : ApiVideoPlayerController.Listener {
    val styledPlayerView: StyledPlayerView
}