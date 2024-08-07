package video.api.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.DISCONTINUITY_REASON_SEEK
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.ui.PlayerView
import video.api.player.analytics.exoplayer.extensions.addApiVideoAnalyticsListener
import video.api.player.extensions.currentVideoOptions
import video.api.player.extensions.setMediaSource
import video.api.player.extensions.setMp4MediaSource
import video.api.player.models.ApiVideoExoPlayerMediaFactory
import video.api.player.models.VideoOptions
import video.api.player.notifications.ApiVideoPlayerNotificationController
import video.api.player.views.ApiVideoExoPlayerView
import java.io.IOException


/**
 * Creates a new controller without a view.
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param initialAutoplay initial autoplay: true to play the video immediately, false otherwise
 * @param listener a [ApiVideoPlayerController.Listener] to listen to player events
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 */
fun ApiVideoPlayerController(
    context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: ApiVideoPlayerController.Listener? = null,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
): ApiVideoPlayerController {
    return ApiVideoPlayerController(
        context,
        looper,
        notificationController
    ).apply {
        Handler(looper).post {
            autoplay = initialAutoplay
            listener?.let { addListener(it) }
            initialVideoOptions?.let { videoOptions = it }
        }
    }
}

/**
 * Creates a new controller with an [ApiVideoExoPlayerView].
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param initialAutoplay initial autoplay: true to play the video immediately, false otherwise
 * @param listener the [ApiVideoPlayerController.Listener] to listen to player events
 * @param playerView the player view
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 */
fun ApiVideoPlayerController(
    context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: ApiVideoPlayerController.Listener? = null,
    playerView: ApiVideoExoPlayerView,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
): ApiVideoPlayerController {
    return ApiVideoPlayerController(
        context,
        initialVideoOptions,
        initialAutoplay,
        listener,
        playerView.playerView,
        looper,
        notificationController
    )
}

/**
 * Creates a new controller with a `media3` [PlayerView].
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param initialAutoplay initial autoplay: true to play the video immediately, false otherwise
 * @param listener the [ApiVideoPlayerController.Listener] to listen to player events
 * @param playerView the [PlayerView] to use to display the player
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 */
fun ApiVideoPlayerController(
    context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: ApiVideoPlayerController.Listener? = null,
    playerView: PlayerView,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
): ApiVideoPlayerController {
    return ApiVideoPlayerController(
        context,
        playerView,
        looper,
        notificationController
    ).apply {
        Handler(looper).post {
            autoplay = initialAutoplay
            listener?.let { addListener(it) }
            initialVideoOptions?.let { videoOptions = it }
        }
    }
}

/**
 * Creates a new controller with a [SurfaceView].
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param initialAutoplay initial autoplay: true to play the video immediately, false otherwise
 * @param listener the [ApiVideoPlayerController.Listener] to listen to player events
 * @param surfaceView the [SurfaceView] to use to display the video
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 */
fun ApiVideoPlayerController(
    context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: ApiVideoPlayerController.Listener? = null,
    surfaceView: SurfaceView,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
): ApiVideoPlayerController {
    return ApiVideoPlayerController(
        context,
        surfaceView,
        looper,
        notificationController
    ).apply {
        Handler(looper).post {
            autoplay = initialAutoplay
            listener?.let { addListener(it) }
            initialVideoOptions?.let { videoOptions = it }
        }
    }
}

/**
 * Creates a new controller with a [Surface].
 *
 * @param context the application context
 * @param initialVideoOptions initial video options
 * @param initialAutoplay initial autoplay: true to play the video immediately, false otherwise
 * @param listener the [ApiVideoPlayerController.Listener] to listen to player events
 * @param surface the [Surface] to use to display the video
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 */
fun ApiVideoPlayerController(
    context: Context,
    initialVideoOptions: VideoOptions? = null,
    initialAutoplay: Boolean = false,
    listener: ApiVideoPlayerController.Listener? = null,
    surface: Surface,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
): ApiVideoPlayerController {
    return ApiVideoPlayerController(
        context,
        surface,
        looper,
        notificationController
    ).apply {
        Handler(looper).post {
            autoplay = initialAutoplay
            listener?.let { addListener(it) }
            initialVideoOptions?.let { videoOptions = it }
        }
    }
}

/**
 * The api.video player controller class.
 *
 * @param context the application context
 * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
 * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
 * @constructor Creates a new controller without a view.
 */
class ApiVideoPlayerController(
    private val context: Context,
    looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    private val notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
        context
    )
) {
    /**
     * Creates a new controller with a `media3` [PlayerView].
     *
     * @param context the application context
     * @param playerView the [PlayerView] to use to display the player
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     * @param notificationController the [ApiVideoPlayerNotificationController] to use. It will be released when calling [release].
     */
    constructor(
        context: Context,
        playerView: PlayerView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        looper,
        notificationController
    ) {
        playerView.player = exoplayer
    }

    /**
     * Creates a new controller with a [SurfaceView].
     *
     * @param context the application context
     * @param surfaceView the [SurfaceView] to use to display the video
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        surfaceView: SurfaceView,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        looper,
        notificationController
    ) {
        exoplayer.setVideoSurfaceView(surfaceView)
    }

    /**
     * Creates a new controller with a [Surface].
     *
     * @param context the application context
     * @param surface the [Surface] to use to display the video
     * @param looper the looper where call to the player are executed. By default, it is the current looper or the main looper.
     */
    constructor(
        context: Context,
        surface: Surface,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
        notificationController: ApiVideoPlayerNotificationController? = ApiVideoPlayerNotificationController(
            context
        )
    ) : this(
        context,
        looper,
        notificationController
    ) {
        exoplayer.setVideoSurface(surface)
    }

    private val handler = Handler(looper)
    private val listeners = mutableListOf<Listener>()

    private var firstPlay = true
    private var isReady = false

    private var mediaSourceFactory: ApiVideoExoPlayerMediaFactory? = null

    /**
     * Set/get the video options.
     */
    var videoOptions: VideoOptions?
        get() = exoplayer.currentVideoOptions
        set(value) {
            if (value != null) {
                mediaSourceFactory = ApiVideoExoPlayerMediaFactory(value) { error ->
                    listeners.forEach { listener ->
                        listener.onError(error)
                    }
                }.apply {
                    exoplayer.setMediaSource(this)
                }
            } else {
                exoplayer.clearMediaItems()
            }
        }

    private val exoplayerListener: AnalyticsListener = object : AnalyticsListener {
        @OptIn(UnstableApi::class)
        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException
        ) {
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                // Re-initialize player at the current live window default position.
                exoplayer.seekToDefaultPosition()
                exoplayer.prepare()
            }
            listeners.forEach { listener -> listener.onError(error) }
        }

        @OptIn(UnstableApi::class)
        override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
            if (reason == TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                firstPlay = true
                isReady = false
            }
        }

        @OptIn(UnstableApi::class)
        override fun onLoadError(
            eventTime: AnalyticsListener.EventTime,
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

        @OptIn(UnstableApi::class)
        override fun onIsPlayingChanged(
            eventTime: AnalyticsListener.EventTime,
            isPlaying: Boolean
        ) {
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

        @OptIn(UnstableApi::class)
        override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
            if (state == STATE_READY) {
                if (!isReady) {
                    isReady = true
                    listeners.forEach { listener -> listener.onReady() }
                }
            } else if (state == STATE_ENDED) {
                listeners.forEach { listener -> listener.onEnd() }
            }
        }

        @OptIn(UnstableApi::class)
        override fun onPositionDiscontinuity(
            eventTime: AnalyticsListener.EventTime,
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            if (reason == DISCONTINUITY_REASON_SEEK) {
                listeners.forEach { listener -> listener.onSeek() }
            }
        }

        @OptIn(UnstableApi::class)
        override fun onVideoSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            videoSize: VideoSize
        ) {
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

    @OptIn(UnstableApi::class)
    private val exoplayer =
        ExoPlayer.Builder(context).setLooper(looper).build().apply {
            addAnalyticsListener(exoplayerListener)
            handler.post {
                notificationController?.showNotification(this)
                prepare()
            }
        }

    private val analyticsListener = exoplayer.addApiVideoAnalyticsListener()

    /**
     * Gets if player is playing
     */
    val isPlaying: Boolean
        get() = exoplayer.isPlaying

    /**
     * Gets if the current playing video is a live stream
     */
    val isLive: Boolean
        get() = exoplayer.isCurrentMediaItemLive

    /**
     * Gets/sets the current video position in seconds
     */
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
            val nextCurrentTime = value.coerceIn(0.0f, duration)
            exoplayer.seekTo((nextCurrentTime * 1000.0).toLong())
        }

    /**
     * Gets/sets the video duration in seconds
     */
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

    private var previousVolume = 1.0f

    /**
     * Mutes/unmutes the video
     */
    var isMuted: Boolean
        /**
         * Get the mute states
         *
         * @return true if the video is muted, false otherwise
         */
        get() = volume == 0.0f
        /**
         * Set the mute states
         *
         * @param value true if the video is muted, false otherwise
         */
        set(value) {
            volume = if (value) {
                previousVolume = volume
                0.0f
            } else {
                previousVolume
            }
        }

    /**
     * Gets/Sets the video volume
     */
    var volume: Float
        /**
         * Get audio volume
         *
         * @return volume between 0 and 1.0
         */
        get() = exoplayer.volume
        /**
         * Set audio volume
         *
         * @param value volume between 0 and 1.0
         */
        set(value) {
            exoplayer.volume = value
        }

    /**
     * Gets the video size
     *
     * @return the video size
     */
    val videoSize: Size?
        @OptIn(UnstableApi::class)
        get() = exoplayer.videoFormat?.let { Size(it.width, it.height) }

    /**
     * Gets/Sets the autoplay state
     */
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

    /**
     * Gets/Sets the looping state
     */
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

    var playbackSpeed: Float
        /**
         * Get the playback speed
         *
         * @return the playback speed
         */
        get() = exoplayer.playbackParameters.speed
        /**
         * Set the playback speed
         *
         * @param value the playback speed
         */
        set(value) {
            exoplayer.setPlaybackSpeed(value)
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
        exoplayer.removeAnalyticsListener(analyticsListener)
        notificationController?.release()
        exoplayer.release()
    }

    /**
     * Sets the player view
     *
     * @param view the [ApiVideoExoPlayerView].
     */
    fun setPlayerView(view: ApiVideoExoPlayerView) = setPlayerView(view.playerView)

    /**
     * Sets the player view
     *
     * @param playerView the [PlayerView]
     */
    fun setPlayerView(playerView: PlayerView) {
        playerView.player = exoplayer
    }

    /**
     * Sets the player view
     *
     * @param surfaceView the [SurfaceView]
     */
    fun setSurfaceView(surfaceView: SurfaceView) {
        exoplayer.setVideoSurfaceView(surfaceView)
    }

    /**
     * Sets the player view
     *
     * @param surface the [Surface]
     */
    fun setSurface(surface: Surface) {
        exoplayer.setVideoSurface(surface)
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun switchTargetView(oldPlayerView: PlayerView, newPlayerView: PlayerView) {
        PlayerView.switchTargetView(exoplayer, oldPlayerView, newPlayerView)
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun switchTargetView(
        oldPlayerView: ApiVideoExoPlayerView,
        newPlayerView: ApiVideoExoPlayerView
    ) = switchTargetView(oldPlayerView.playerView, newPlayerView.playerView)

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