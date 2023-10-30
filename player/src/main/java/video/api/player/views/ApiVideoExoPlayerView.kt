package video.api.player.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import androidx.media3.ui.PlayerView
import video.api.player.R
import video.api.player.databinding.ExoPlayerLayoutBinding
import video.api.player.interfaces.IExoPlayerBasedPlayerView

/**
 * The api.video player view class based on an ExoPlayer [PlayerView].
 *
 * @param context the application context
 * @param attrs the attributes of the XML tag that is inflating the view.
 * @param defStyleAttr an attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
 */
class ApiVideoExoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IExoPlayerBasedPlayerView {
    private val binding = ExoPlayerLayoutBinding.inflate(LayoutInflater.from(context), this)

    override val playerView: PlayerView
        get() = binding.playerView

    /**
     * Sets or gets the full screen listener.
     * If set to null, the full screen button is hidden.
     */
    var fullScreenListener: FullScreenListener? = null
        @SuppressLint("UnsafeOptInUsageError")
        set(value) {
            if (value != null) {
                playerView.setFullscreenButtonClickListener {
                    value.onFullScreenModeChanged(it)
                }
            } else {
                playerView.setControllerOnFullScreenModeChangedListener(null)
            }
            field = value
        }

    /**
     * Shows or hides the control buttons
     */
    var showControls: Boolean
        get() = playerView.useController
        set(value) {
            playerView.useController = value
        }

    /**
     * Shows or hides the subtitles
     */
    var showSubtitles: Boolean = true
        @SuppressLint("UnsafeOptInUsageError")
        set(value) {
            if (value) {
                playerView.subtitleView?.visibility = VISIBLE
                playerView.setShowSubtitleButton(true)
            } else {
                playerView.subtitleView?.visibility = INVISIBLE
                playerView.setShowSubtitleButton(false)
            }
            field = value
        }

    /**
     * Sets or gets how the video is fitted in its parent view
     */
    var viewFit: ViewFit
        @SuppressLint("UnsafeOptInUsageError")
        get() = ViewFit.fromValue(playerView.resizeMode)
        @SuppressLint("UnsafeOptInUsageError")
        set(value) {
            playerView.resizeMode = value.value
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ApiVideoExoPlayerView)
        try {
            showControls = a.getBoolean(R.styleable.ApiVideoExoPlayerView_show_controls, true)
            showSubtitles = a.getBoolean(R.styleable.ApiVideoExoPlayerView_show_subtitles, true)
        } finally {
            a.recycle()
        }
    }

    interface FullScreenListener {
        /**
         * Called when the full screen button has been clicked
         *
         * @param isFullScreen true if the player is in fullscreen mode
         */
        fun onFullScreenModeChanged(isFullScreen: Boolean) {}
    }

    /**
     * The different ways to fit the video in its parent view
     */
    @SuppressLint("UnsafeOptInUsageError")
    enum class ViewFit(val value: Int) {
        /**
         * The video is resized to be contained in its parent view
         */
        Contains(RESIZE_MODE_FIT),

        /**
         * The video is resized to fit its parent view width. The height is adjusted to keep the aspect ratio.
         */
        FitWidth(RESIZE_MODE_FIXED_WIDTH),

        /**
         * The video is resized to fit its parent view height. The width is adjusted to keep the aspect ratio.
         */
        FitHeight(RESIZE_MODE_FIXED_HEIGHT),

        /**
         * The video is resized to fill its parent view. Aspect ratio is not preserved.
         */
        Fill(RESIZE_MODE_FILL),

        /**
         * The video is resized to fill its parent view. Aspect ratio is preserved.
         */
        Zoom(RESIZE_MODE_ZOOM);

        companion object {
            fun fromValue(value: Int) = entries.first { it.value == value }
        }
    }
}