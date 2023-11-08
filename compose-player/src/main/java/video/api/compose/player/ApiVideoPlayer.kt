package video.api.compose.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.SecureFlagPolicy
import video.api.compose.player.extensions.findActivity
import video.api.player.ApiVideoPlayerController
import video.api.player.extensions.showSystemUI
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.notifications.ApiVideoPlayerNotificationController
import video.api.player.views.ApiVideoExoPlayerView

/**
 * [ApiVideoPlayer] is a composable that displays an api.video video from the video options.
 *
 * @param videoOptions The video options
 * @param modifier The modifier to be applied to the view
 * @param viewFit Sets how the video is fitted in its parent view
 * @param showControls Shows or hides the control buttons
 * @param showFullScreenButton Shows or hides the full screen button
 * @param showSubtitleButton Shows or hides the subtitle button
 * @param autoplay True to play the video immediately, false otherwise
 * @param notificationController The notification controller. Set to null to disable the notification.
 */
@Composable
fun ApiVideoPlayer(
    videoOptions: VideoOptions,
    modifier: Modifier = Modifier,
    viewFit: ApiVideoExoPlayerView.ViewFit = ApiVideoExoPlayerView.ViewFit.Contains,
    showControls: Boolean = true,
    showFullScreenButton: Boolean = true,
    showSubtitleButton: Boolean = true,
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
        showFullScreenButton = showFullScreenButton,
        showSubtitleButton = showSubtitleButton
    )
}


/**
 * [ApiVideoPlayer] is a composable that displays an api.video video from the player controller.
 *
 * @param controller The player controller
 * @param modifier The modifier to be applied to the view
 * @param viewFit Sets how the video is fitted in its parent view
 * @param showControls Shows or hides the control buttons
 * @param showFullScreenButton Shows or hides the full screen button
 * @param showSubtitleButton Shows or hides the subtitle button
 */
@SuppressLint("OpaqueUnitKey")
@Composable
fun ApiVideoPlayer(
    controller: ApiVideoPlayerController,
    modifier: Modifier = Modifier,
    viewFit: ApiVideoExoPlayerView.ViewFit = ApiVideoExoPlayerView.ViewFit.Contains,
    showControls: Boolean = true,
    showFullScreenButton: Boolean = true,
    showSubtitleButton: Boolean = true
) {
    val context = LocalContext.current

    var isFullScreenModeEntered by remember { mutableStateOf(false) }

    // player view
    val playerView = remember {
        ApiVideoExoPlayerView(context).apply {
            this.viewFit = viewFit
            this.showControls = showControls
            this.showSubtitles = showSubtitleButton

            if (showFullScreenButton) {
                this.fullScreenListener = object : ApiVideoExoPlayerView.FullScreenListener {
                    override fun onFullScreenModeChanged(isFullScreen: Boolean) {
                        isFullScreenModeEntered = isFullScreen
                    }
                }
            }

            controller.setPlayerView(this)
        }
    }

    DisposableEffect(
        if (isFullScreenModeEntered) {
            FullScreenPlayer(
                originalView = playerView,
                playerController = controller,
                securePolicy = SecureFlagPolicy.Inherit,
                onDismissRequest = {
                    isFullScreenModeEntered = false
                },
            )
        } else {
            ApiVideoPlayer(
                view = playerView,
                modifier = modifier
            )
        },

        ) {
        onDispose {
            controller.release()
        }
    }
}

/**
 * [ApiVideoPlayer] is a composable that displays the api.video player view.
 *
 * @param view The Android player view
 * @param modifier The modifier to be applied to the view
 */
@SuppressLint("OpaqueUnitKey")
@Composable
private fun ApiVideoPlayer(
    view: ApiVideoExoPlayerView,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = {
            view.apply {
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        }
    )
}

@Preview
@Composable
private fun ApiVideoPlayerPreview() {
    MaterialTheme {
        ApiVideoPlayer(VideoOptions("vi77Dgk0F8eLwaFOtC5870yn", VideoType.VOD))
    }
}

@Composable
private fun FullScreenPlayer(
    originalView: ApiVideoExoPlayerView,
    playerController: ApiVideoPlayerController,
    securePolicy: SecureFlagPolicy,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val currentActivity = context.findActivity()

    val originalOrientation = remember {
        currentActivity!!.requestedOrientation
    }
    val fullScreenPlayerView = remember {
        originalView.duplicate()
    }

    val internalOnDismissRequest = {
        // Going back to normal screen
        playerController.switchTargetView(fullScreenPlayerView, originalView)
        originalView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
            .performClick()

        currentActivity?.requestedOrientation = originalOrientation
        currentActivity?.window?.showSystemUI()

        onDismissRequest()
    }

    SideEffect {
        fullScreenPlayerView.fullScreenListener =
            object : ApiVideoExoPlayerView.FullScreenListener {
                override fun onFullScreenModeChanged(isFullScreen: Boolean) {
                    if (!isFullScreen) {
                        internalOnDismissRequest()
                    }
                }
            }
    }
    SideEffect {
        // Going to full screen
        currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        playerController.switchTargetView(originalView, fullScreenPlayerView)
        fullScreenPlayerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
            .performClick()
    }

    FullScreenDialog({
        internalOnDismissRequest()
    }, securePolicy) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            ApiVideoPlayer(
                view = fullScreenPlayerView,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
