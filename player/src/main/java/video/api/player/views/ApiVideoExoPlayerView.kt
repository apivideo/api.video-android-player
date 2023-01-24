package video.api.player.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.R
import video.api.player.databinding.ExoPlayerLayoutBinding
import video.api.player.interfaces.IExoPlayerBasedPlayerView


/**
 * The api.video player view class based on an ExoPlayer [StyledPlayerView].
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

    override val styledPlayerView: StyledPlayerView
        get() = binding.playerView

    var listener: Listener? = null

    /**
     * Shows or hides the full screen button
     */
    var showFullScreenButton: Boolean = true
        set(value) {
            if (value) {
                styledPlayerView.setFullscreenButtonClickListener {
                    listener?.onFullScreenModeChanged(it)
                }
            } else {
                styledPlayerView.setControllerOnFullScreenModeChangedListener(null)
            }
            field = value
        }

    /**
     * Shows or hides the control buttons
     */
    var showControls: Boolean
        get() = styledPlayerView.useController
        set(value) {
            styledPlayerView.useController = value
        }

    /**
     * Shows or hides the subtitles
     */
    var showSubtitles: Boolean = true
        set(value) {
            if (value) {
                styledPlayerView.subtitleView?.visibility = VISIBLE
                styledPlayerView.setShowSubtitleButton(true)
            } else {
                styledPlayerView.subtitleView?.visibility = INVISIBLE
                styledPlayerView.setShowSubtitleButton(false)
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