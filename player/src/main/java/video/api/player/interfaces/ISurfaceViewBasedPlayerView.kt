package video.api.player.interfaces

import android.view.SurfaceView
import video.api.player.ApiVideoPlayerController

interface ISurfaceViewBasedPlayerView : ApiVideoPlayerController.Listener {
    val surfaceView: SurfaceView
}