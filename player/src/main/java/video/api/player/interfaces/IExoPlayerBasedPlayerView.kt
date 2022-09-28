package video.api.player.interfaces

import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.ApiVideoPlayerController

interface IExoPlayerBasedPlayerView : ApiVideoPlayerController.ViewListener {
    val styledPlayerView: StyledPlayerView
}