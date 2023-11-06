package video.api.player.models

import android.util.Log
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import video.api.player.ApiVideoPlayerController
import video.api.player.R
import video.api.player.views.ApiVideoExoPlayerView
import video.api.player.views.FullScreenDialogFragment

/**
 * A class that handles the player full screen.
 *
 * Internally, it creates another player view in a full screen dialog fragment.
 *
 * @param fragmentManager The fragment manager.
 * @param originalPlayerView The player view.
 * @param playerController The player controller.
 * @param fullScreenListener The full screen listener if you want to lock the orientation in full screen.
 */
class ApiVideoPlayerFullScreenController(
    private val fragmentManager: FragmentManager,
    private val originalPlayerView: ApiVideoExoPlayerView,
    private val playerController: ApiVideoPlayerController,
    private val fullScreenListener: ApiVideoExoPlayerView.FullScreenListener? = null
) : ApiVideoExoPlayerView.FullScreenListener {
    /**
     * Full screen listener for the full screen player view.
     */
    private val internalFullScreenListener = object : ApiVideoExoPlayerView.FullScreenListener {
        override fun onFullScreenModeChanged(isFullScreen: Boolean) {
            if (dialogFragment.isVisible) {
                fullScreenPlayerView.playerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
                    .setImageResource(R.drawable.exo_styled_controls_fullscreen_exit)
                playerController.switchTargetView(fullScreenPlayerView, originalPlayerView)
                dialogFragment.dismiss()
                fullScreenListener?.onFullScreenModeChanged(false)
            } else {
                Log.e(TAG, "onFullScreenModeChanged: not expected when dialog is already visible")
            }
        }
    }
    private val fullScreenPlayerView: ApiVideoExoPlayerView = originalPlayerView.duplicate().apply {
        this.fullScreenListener = this@ApiVideoPlayerFullScreenController.internalFullScreenListener
        this.playerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
            .setImageResource(R.drawable.exo_styled_controls_fullscreen_exit)
    }
    private val dialogFragment: FullScreenDialogFragment =
        FullScreenDialogFragment(fullScreenPlayerView)

    /**
     * Original view full screen listener.
     */
    override fun onFullScreenModeChanged(isFullScreen: Boolean) {
        if (!dialogFragment.isVisible) {
            originalPlayerView.playerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
                .setImageResource(R.drawable.exo_styled_controls_fullscreen_enter)
            playerController.switchTargetView(originalPlayerView, fullScreenPlayerView)
            dialogFragment.show(fragmentManager, TAG)
            fullScreenListener?.onFullScreenModeChanged(true)
        } else {
            Log.e(TAG, "onFullScreenModeChanged: not expected")
        }
    }

    companion object {
        private const val TAG = "FullScreenDialogCtrl"
    }
}