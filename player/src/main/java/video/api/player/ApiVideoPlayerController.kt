package video.api.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Size
import android.view.SurfaceView
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.video.VideoSize
import video.api.analytics.exoplayer.ApiVideoAnalyticsListener
import video.api.player.interfaces.IExoPlayerBasedPlayerView
import video.api.player.interfaces.ISurfaceViewBasedPlayerView
import video.api.player.models.*
import java.io.IOException


/**
 * The api.video player controller class.
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param listener a [Player.Listener] to listen to player events
 */
class ApiVideoPlayerController
internal constructor(
    private val context: Context,
    initialVideoOptions: VideoOptions? = null,
    private val listener: Listener,
) {
    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param playerView the [IExoPlayerBasedPlayerView] interface for ExoPlayer [StyledPlayerView] based player view
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        listener: Listener,
        playerView: IExoPlayerBasedPlayerView
    ) : this(context, initialVideoOptions, listener, playerView.styledPlayerView) {
        viewListener = playerView
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param playerView the [ISurfaceViewBasedPlayerView] interface for [SurfaceView] based player view
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        listener: Listener,
        playerView: ISurfaceViewBasedPlayerView
    ) : this(context, initialVideoOptions, listener, playerView.surfaceView) {
        viewListener = playerView
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param styledPlayerView the [StyledPlayerView] to use to display the player
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        listener: Listener,
        styledPlayerView: StyledPlayerView
    ) : this(context, initialVideoOptions, listener) {
        styledPlayerView.player = exoplayer
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param surfaceView the [SurfaceView] to use to display the video
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        listener: Listener,
        surfaceView: SurfaceView
    ) : this(context, initialVideoOptions, listener) {
        exoplayer.setVideoSurfaceView(surfaceView)
    }

    private val queue = Volley.newRequestQueue(context).apply {
        start()
    }
    private lateinit var playerManifest: PlayerManifest
    private var analyticsListener: ApiVideoAnalyticsListener? = null
    private var xTokenSession: String? = null
    private var firstPlay = true
    private var isReady = false

    /**
     * Set/get the video options.
     */
    var videoOptions: VideoOptions? = null
        /**
         * Play a new video from the given [VideoOptions].
         *
         * @param value the video options
         */
        set(value) {
            field = value
            value?.let {
                require(it.videoType == VideoType.VOD) { "Only VOD videos are supported" }

                firstPlay = true
                isReady = false
                loadPlayer(it)
            }
        }

    private val exoPlayerAnalyticsListener: AnalyticsListener = object : AnalyticsListener {
        override fun onPlayerError(eventTime: EventTime, error: PlaybackException) {
            listener.onError(error)
        }

        override fun onLoadError(
            eventTime: EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData,
            error: IOException,
            wasCanceled: Boolean
        ) {
            this@ApiVideoPlayerController.playerManifest.video.mp4?.let {
                if (loadEventInfo.uri.toString() != getPlayerFileUrl(it, videoOptions?.token)) {
                    Log.w(TAG, "Failed to load video. Fallback to mp4")
                    setPlayerUri(it)
                } else {
                    listener.onError(error)
                }
            } ?: listener.onError(error)
        }

        override fun onIsPlayingChanged(eventTime: EventTime, isPlaying: Boolean) {
            if (isPlaying) {
                if (firstPlay) {
                    firstPlay = false
                    listener.onFirstPlay()
                }
                listener.onPlay()
            } else {
                if (exoplayer.playbackState != STATE_ENDED) {
                    listener.onPause()
                }
            }
        }

        override fun onPlaybackStateChanged(eventTime: EventTime, state: Int) {
            if (state == STATE_READY) {
                if (!isReady) {
                    isReady = true
                    listener.onReady()
                }
            } else if (state == STATE_ENDED) {
                listener.onEnd()
            }
        }

        override fun onPositionDiscontinuity(
            eventTime: EventTime,
            oldPosition: PositionInfo,
            newPosition: PositionInfo,
            reason: Int
        ) {
            if (reason == DISCONTINUITY_REASON_SEEK) {
                listener.onSeek()
            }
        }

        override fun onVideoSizeChanged(eventTime: EventTime, videoSize: VideoSize) {
            listener.onVideoSizeChanged(Size(videoSize.width, videoSize.height))
        }
    }

    private val exoplayer = ExoPlayer.Builder(context).build().apply {
        addAnalyticsListener(exoPlayerAnalyticsListener)
    }

    init {
        initialVideoOptions?.let {
            videoOptions = it
        }
    }

    var currentTime: Float
        /**
         * Get current video position in seconds
         *
         * @return current video position in seconds
         */
        get() = exoplayer.currentPosition.toFloat() / 1000.0f
        /**
         * Set current video position in seconds
         *
         * @param value video position in seconds
         */
        set(value) {
            exoplayer.seekTo((value * 1000.0).toLong())
        }

    val duration: Float
        /**
         * Get video duration in seconds
         *
         * @return video duration in seconds
         */
        get() = exoplayer.duration / 1000.0f

    var isMuted: Boolean
        /**
         * Get the device mute states
         *
         * @return true if the device is muted, false otherwise
         */
        get() = exoplayer.isDeviceMuted
        /**
         * Set the device mute states
         *
         * @param value true if the device is muted, false otherwise
         */
        set(value) {
            exoplayer.isDeviceMuted = value
        }

    private var viewListener: ViewListener? = null

    private fun loadPlayer(videoOptions: VideoOptions) {
        getPlayerManifest(videoOptions, { request ->
            playerManifest = request.playerManifest
            viewListener?.onNewVideoManifest(playerManifest)
            xTokenSession = request.headers?.get("X-Token-Session")
            analyticsListener?.let { exoplayer.removeAnalyticsListener(it) }
            analyticsListener =
                ApiVideoAnalyticsListener(context, exoplayer, playerManifest.video.src).apply {
                    exoplayer.addAnalyticsListener(this)
                }
            setPlayerUri(playerManifest.video.src, videoOptions.token)
            preparePlayer(playerManifest)
        }, { error ->
            listener.onError(error)
        })
    }

    /**
     * Plays the video
     */
    fun play() {
        exoplayer.play()
    }

    /**
     * Pauses the video
     */
    fun pause() {
        exoplayer.pause()
    }

    /**
     * Stops the video
     */
    fun stop() {
        exoplayer.stop()
    }

    /**
     * Releases the player
     */
    fun release() {
        exoplayer.removeAnalyticsListener(exoPlayerAnalyticsListener)
        analyticsListener?.let { exoplayer.removeAnalyticsListener(it) }
        exoplayer.release()
    }

    var volume: Float
        /**
         * Get audio volume
         *
         * @return volume between 0 and 1.0
         */
        get() = exoplayer.deviceVolume.toFloat() / (exoplayer.deviceInfo.maxVolume - exoplayer.deviceInfo.minVolume) - exoplayer.deviceInfo.minVolume
        /**
         * Set audio volume
         *
         * @param value volume between 0 and 1.0
         */
        set(value) {
            exoplayer.deviceVolume =
                (value * (exoplayer.deviceInfo.maxVolume - exoplayer.deviceInfo.minVolume) + exoplayer.deviceInfo.minVolume).toInt()
        }

    private fun setPlayerUri(uri: String, token: String? = null) {
        val mediaItem =
            MediaItem.fromUri(getPlayerFileUrl(uri, token))
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        xTokenSession?.let { dataSourceFactory.setDefaultRequestProperties(mapOf("X-Token-Session" to it)) }

        val videoSource =
            DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(mediaItem)

        exoplayer.setMediaSource(videoSource)
    }

    private fun preparePlayer(playerManifest: PlayerManifest) {
        if (playerManifest.loop) {
            exoplayer.repeatMode = REPEAT_MODE_ALL
        }
        exoplayer.playWhenReady = playerManifest.autoplay
        exoplayer.prepare()
    }

    private fun loadPoster(posterUrl: String, callback: (Drawable) -> Unit) {
        val imageRequest = ImageRequest(
            posterUrl,
            { bitmap ->
                try {
                    callback(BitmapDrawable(context.resources, bitmap))
                } catch (e: Exception) {
                    Log.e(TAG, e.message ?: "Failed to transform poster to drawable")
                }
            },
            0,
            0,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.ARGB_8888,
            { error ->
                Log.e(TAG, error.message ?: "Failed to get poster")
            }
        )

        queue.add(imageRequest)
    }

    private fun getPlayerManifest(
        videoOptions: VideoOptions,
        onSuccess: (PlayerJsonRequestResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val stringRequest = PlayerJsonRequest(
            getPlayerJsonUrl(videoOptions.videoId, videoOptions.videoType, videoOptions.token),
            { response ->
                try {
                    onSuccess(response)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                onError(error)
            }
        )

        queue.add(stringRequest)
    }

    companion object {
        private val TAG = "ApiVideoPlayer"

        private fun getPlayerJsonUrl(
            videoId: String,
            videoType: VideoType,
            privateToken: String? = null
        ) =
            videoType.baseUrl + videoId + (privateToken?.let { "/token/$privateToken" }
                ?: "") + "/player.json"

        private fun getPlayerFileUrl(
            uri: String,
            token: String? = null,
        ) = token?.let { uri.replace(":token", it) } ?: uri
    }

    /**
     * Listener for player events
     */
    interface Listener {
        /**
         * An error occurred
         */
        fun onError(error: Exception) {}

        /**
         * The video is ready to play
         */
        fun onReady() {}

        /**
         * The video started to play for the first time
         */
        fun onFirstPlay() {}

        /**
         * The video started to play (for the first time or after having been paused)
         */
        fun onPlay() {}

        /**
         * The video has been paused
         */
        fun onPause() {}

        /**
         * The player is seeking
         */
        fun onSeek() {}

        /**
         * The playback as reached the ended of the video
         */
        fun onEnd() {}

        /**
         * Called when the video resolution has changed
         *
         * @param resolution the new video resolution
         */
        fun onVideoSizeChanged(resolution: Size) {}
    }

    interface ViewListener {
        /**
         * Called when a new video manifest has been loaded.
         *
         * Use it to adapt your view according to api.video player settings.
         */
        fun onNewVideoManifest(playerManifest: PlayerManifest)
    }
}