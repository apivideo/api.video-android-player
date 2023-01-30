package video.api.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import video.api.analytics.exoplayer.ApiVideoAnalyticsListener
import video.api.player.extensions.currentVideoOptions
import video.api.player.extensions.setMediaSource
import video.api.player.extensions.setMp4MediaSource
import video.api.player.interfaces.IExoPlayerBasedPlayerView
import video.api.player.interfaces.ISurfaceViewBasedPlayerView
import video.api.player.models.ApiVideoMediaSourceFactory
import video.api.player.models.VideoOptions
import java.io.IOException


/**
 * The api.video player controller class.
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param listener a [Player.Listener] to listen to player events
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 */
class ApiVideoPlayerController
internal constructor(
    private val context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: Listener? = null,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    private val notificationController: ApiVideoPlayerNotificationController? = null
) {
    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param playerView the [IExoPlayerBasedPlayerView] interface for ExoPlayer [StyledPlayerView] based player view
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        initialAutoplay: Boolean = false,
        listener: Listener? = null,
        playerView: IExoPlayerBasedPlayerView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        playerView.styledPlayerView,
        looper,
        notificationController
    ) {
        addListener(playerView)
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param playerView the [ISurfaceViewBasedPlayerView] interface for [SurfaceView] based player view
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        initialAutoplay: Boolean = false,
        listener: Listener? = null,
        playerView: ISurfaceViewBasedPlayerView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        playerView.surfaceView,
        looper,
        notificationController
    ) {
        addListener(playerView)
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param styledPlayerView the [StyledPlayerView] to use to display the player
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        initialAutoplay: Boolean = false,
        listener: Listener? = null,
        styledPlayerView: StyledPlayerView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        looper,
        notificationController
    ) {
        styledPlayerView.player = exoplayer
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param surfaceView the [SurfaceView] to use to display the video
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        initialAutoplay: Boolean = false,
        listener: Listener? = null,
        surfaceView: SurfaceView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        looper,
        notificationController
    ) {
        exoplayer.setVideoSurfaceView(surfaceView)
    }

    /**
     * @param context the application context
     * @param initialVideoOptions initial video options
     * @param listener the [Player.Listener] to listen to player events
     * @param surface the [Surface] to use to display the video
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        initialVideoOptions: VideoOptions? = null,
        initialAutoplay: Boolean = false,
        listener: Listener? = null,
        surface: Surface,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        looper,
        notificationController
    ) {
        exoplayer.setVideoSurface(surface)
    }

    private val handler = Handler(looper)
    private var analyticsListener: ApiVideoAnalyticsListener? = null
    private val listeners = mutableListOf<Listener>()

    private var firstPlay = true
    private var isReady = false

    private var mediaSourceFactory: ApiVideoMediaSourceFactory? = null

    /**
     * Set/get the video options.
     */
    var videoOptions: VideoOptions?
        get() = exoplayer.currentVideoOptions
        set(value) {
            value?.let {
                mediaSourceFactory = ApiVideoMediaSourceFactory(it) { error ->
                    listeners.forEach { listener ->
                        listener.onError(error)
                    }
                }.apply {
                    exoplayer.setMediaSource(this)
                }
            } ?: throw IllegalArgumentException("VideoOptions cannot be null")
        }

    private val exoplayerListener: AnalyticsListener = object : AnalyticsListener {
        override fun onPlayerError(eventTime: EventTime, error: PlaybackException) {
            listeners.forEach { listener -> listener.onError(error) }
        }

        override fun onMediaItemTransition(
            eventTime: EventTime,
            mediaItem: MediaItem?,
            reason: Int
        ) {
            // Reload analytics listener when a new video is loaded
            analyticsListener?.let { exoplayer.removeAnalyticsListener(it) }
            mediaItem?.localConfiguration?.uri?.toString()?.let { url ->
                analyticsListener =
                    ApiVideoAnalyticsListener(context, exoplayer, url).apply {
                        exoplayer.addAnalyticsListener(this)
                    }
            }
        }

        override fun onTimelineChanged(eventTime: EventTime, reason: Int) {
            if (reason == TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                firstPlay = true
                isReady = false
            }
        }

        override fun onLoadError(
            eventTime: EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData,
            error: IOException,
            wasCanceled: Boolean
        ) {
            this@ApiVideoPlayerController.videoOptions?.let {
                if (loadEventInfo.uri.toString() != it.mp4Url) {
                    Log.w(TAG, "Failed to load video. Fallback to mp4.")
                    exoplayer.setMp4MediaSource(mediaSourceFactory!!)
                } else {
                    listeners.forEach { listener -> listener.onError(error) }
                }
            } ?: listeners.forEach { listener -> listener.onError(error) }
        }

        override fun onIsPlayingChanged(eventTime: EventTime, isPlaying: Boolean) {
            if (isPlaying) {
                if (firstPlay) {
                    firstPlay = false
                    listeners.forEach { listener -> listener.onFirstPlay() }
                }
                listeners.forEach { listener -> listener.onPlay() }
            } else {
                if (exoplayer.playbackState != STATE_ENDED) {
                    listeners.forEach { listener -> listener.onPause() }
                }
            }
        }

        override fun onPlaybackStateChanged(eventTime: EventTime, state: Int) {
            if (state == STATE_READY) {
                if (!isReady) {
                    isReady = true
                    listeners.forEach { listener -> listener.onReady() }
                }
            } else if (state == STATE_ENDED) {
                listeners.forEach { listener -> listener.onEnd() }
            }
        }

        override fun onPositionDiscontinuity(
            eventTime: EventTime,
            oldPosition: PositionInfo,
            newPosition: PositionInfo,
            reason: Int
        ) {
            if (reason == DISCONTINUITY_REASON_SEEK) {
                listeners.forEach { listener -> listener.onSeek() }
            }
        }

        override fun onVideoSizeChanged(eventTime: EventTime, videoSize: VideoSize) {
            listeners.forEach { listener ->
                listener.onVideoSizeChanged(
                    Size(
                        videoSize.width,
                        videoSize.height
                    )
                )
            }
        }
    }

    private val exoplayer =
        ExoPlayer.Builder(context).setLooper(looper).build().apply {
            addAnalyticsListener(exoplayerListener)
            Handler(context.mainLooper).post {
                notificationController?.showNotification(this)
            }
        }

    /**
     * Check if player is playing
     */
    val isPlaying: Boolean
        get() = exoplayer.isPlaying

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
        get() {
            val duration = exoplayer.duration
            return if (duration == C.TIME_UNSET) {
                0.0f
            } else {
                exoplayer.duration / 1000.0f
            }
        }

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

    val videoSize: Size?
        /**
         * Get the video size
         *
         * @return the video size
         */
        get() = exoplayer.videoFormat?.let { Size(it.width, it.height) }

    var autoplay: Boolean
        /**
         * Get the autoplay state
         *
         * @return true if the video will autoplay, false otherwise
         */
        get() = exoplayer.playWhenReady
        /**
         * Set the autoplay state
         *
         * @param value true if the video will autoplay, false otherwise
         */
        set(value) {
            exoplayer.playWhenReady = value
        }

    var isLooping: Boolean
        /**
         * Get the looping state
         *
         * @return true if the video is looping, false otherwise
         */
        get() = exoplayer.repeatMode == REPEAT_MODE_ALL
        /**
         * Set the looping state
         *
         * @param value true if the video is looping, false otherwise
         */
        set(value) {
            if (value) {
                exoplayer.repeatMode = REPEAT_MODE_ALL
            } else {
                exoplayer.repeatMode = REPEAT_MODE_OFF
            }
        }

    init {
        listener?.let { addListener(it) }

        handler.post {
            initialVideoOptions?.let {
                videoOptions = it
            }
            autoplay = initialAutoplay
            exoplayer.prepare()
        }
    }

    /**
     * Add a listener to the player
     *
     * @param listener the listener to add
     */
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    /**
     * Remove a listener
     *
     * @param listener the listener to remove
     */
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    /**
     * Plays the video
     */
    fun play() {
        notificationController?.isActive = true
        exoplayer.playWhenReady = true
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
        notificationController?.isActive = false
    }

    /**
     * Sets the video position to a time in milliseconds from the current time.
     */
    fun seek(offset: Float) {
        currentTime += offset
    }

    /**
     * Releases the player
     */
    fun release() {
        notificationController?.hideNotification()
        exoplayer.removeAnalyticsListener(exoplayerListener)
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

    companion object {
        private const val TAG = "ApiVideoPlayer"
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
}