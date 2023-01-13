package video.api.player.interfaces

import android.view.SurfaceView
import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.ApiVideoPlayerController

interface ISurfaceViewBasedPlayerView : ApiVideoPlayerController.Listener {
    val surfaceView: SurfaceView
}