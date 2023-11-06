package video.api.compose.player

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import video.api.player.ApiVideoPlayerController
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.notifications.ApiVideoPlayerNotificationController
import video.api.player.views.ApiVideoExoPlayerView

/**
 * [ApiVideoPlayer] is a composable that displays an api.video video.
 *
 * @param videoOptions The video options
 * @param modifier The modifier to be applied to the view
 * @param viewFit Sets how the video is fitted in its parent view
 * @param showControls Shows or hides the control buttons
 * @param showSubtitles Shows or hides the subtitles and the subtitle button
 * @param autoplay True to play the video immediately, false otherwise
 * @param notificationController The notification controller. Set to null to disable the notification.
 */
@Composable
fun ApiVideoPlayer(
    videoOptions: VideoOptions,
    modifier: Modifier = Modifier,
    viewFit: ApiVideoExoPlayerView.ViewFit = ApiVideoExoPlayerView.ViewFit.Contains,
    showControls: Boolean = true,
    showSubtitles: Boolean = true,
    autoplay: Boolean = true,
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        LocalContext.current
    )
) {
    val context = LocalContext.current
    val controller = remember {
        ApiVideoPlayerController(
            context,
            videoOptions,
            initialAutoplay = autoplay,
            notificationController = notificationController
        )
    }

    ApiVideoPlayer(
        controller = controller,
        modifier = modifier,
        viewFit = viewFit,
        showControls = showControls,
        showSubtitles = showSubtitles
    )
}


/**
 * [ApiVideoPlayer] is a composable that displays an api.video video from the player controller.
 *
 * @param controller The player controller
 * @param modifier The modifier to be applied to the view
 * @param viewFit Sets how the video is fitted in its parent view
 * @param showControls Shows or hides the control buttons
 * @param showSubtitles Shows or hides the subtitles and the subtitle button
 */
@Composable
fun ApiVideoPlayer(
    controller: ApiVideoPlayerController,
    modifier: Modifier = Modifier,
    viewFit: ApiVideoExoPlayerView.ViewFit = ApiVideoExoPlayerView.ViewFit.Contains,
    showControls: Boolean = true,
    showSubtitles: Boolean = true
) {
    val context = LocalContext.current

    // player view
    DisposableEffect(
        AndroidView(
            modifier = modifier,
            factory = {
                ApiVideoExoPlayerView(context).apply {
                    this.fullScreenListener = null
                    this.viewFit = viewFit
                    this.showControls = showControls
                    this.showSubtitles = showSubtitles

                    controller.setPlayerView(this)
                }
            }
        )
    ) {
        onDispose {
            controller.release()
        }
    }
}

@Preview
@Composable
private fun ApiVideoPlayerPreview() {
    MaterialTheme {
        ApiVideoPlayer(VideoOptions("vi77Dgk0F8eLwaFOtC5870yn", VideoType.VOD))
    }
}
