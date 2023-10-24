package video.api.player.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
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

    var listener: Listener? = null

    /**
     * Shows or hides the full screen button
     */
    var showFullScreenButton: Boolean = true
        @OptIn(UnstableApi::class)
        set(value) {
            if (value) {
                playerView.setFullscreenButtonClickListener {
                    listener?.onFullScreenModeChanged(it)
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
        @OptIn(UnstableApi::class)
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

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ApiVideoExoPlayerView)
        try {
            showFullScreenButton =
                a.getBoolean(R.styleable.ApiVideoExoPlayerView_show_fullscreen_button, true)
            showControls = a.getBoolean(R.styleable.ApiVideoExoPlayerView_show_controls, true)
            showSubtitles = a.getBoolean(R.styleable.ApiVideoExoPlayerView_show_subtitles, true)
        } finally {
            a.recycle()
        }
    }

    interface Listener {
        /**
         * Called when the full screen button has been clicked
         *
         * @param isFullScreen true if the player is in fullscreen mode
         */
        fun onFullScreenModeChanged(isFullScreen: Boolean) {}
    }
}