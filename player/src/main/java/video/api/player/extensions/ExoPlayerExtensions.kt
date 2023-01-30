package video.api.player.extensions

import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import video.api.player.models.ApiVideoMediaSourceFactory
import video.api.player.models.VideoOptions

private const val TAG = "ExoPlayerExtensions"

/**
 * Sets a media source to [ExoPlayer] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun ExoPlayer.setMediaSource(
    videoOptions: VideoOptions,
) {
    setMediaSource(ApiVideoMediaSourceFactory(videoOptions) { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    })
}

/**
 * Sets a media source to [ExoPlayer] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun ExoPlayer.setMediaSource(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMediaSource {
        setMediaSource(it)
    }
}

/**
 * Sets a media source to [ExoPlayer] to read from api.video MP4 URL.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun ExoPlayer.setMp4MediaSource(
    videoOptions: VideoOptions,
) {
    setMp4MediaSource(ApiVideoMediaSourceFactory(videoOptions) { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    })
}

/**
 * Sets a media source to [ExoPlayer] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun ExoPlayer.setMp4MediaSource(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMp4MediaSource {
        setMediaSource(it)
    }
}

/**
 * Adds a media source to [ExoPlayer] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun ExoPlayer.addMediaSource(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    addMediaSource(ApiVideoMediaSourceFactory(videoOptions, onError))
}

/**
 * Adds a media source to [ExoPlayer] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun ExoPlayer.addMediaSource(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMediaSource {
        addMediaSource(it)
    }
}

/**
 * Adds a media source to [ExoPlayer] to read from api.video MP4 URL.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun ExoPlayer.addMp4MediaSource(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    addMp4MediaSource(ApiVideoMediaSourceFactory(videoOptions, onError))
}

/**
 * Adds a media source to [ExoPlayer] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun ExoPlayer.addMp4MediaSource(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMp4MediaSource {
        addMediaSource(it)
    }
}

/**
 * Get the current video options
 *
 * @return The current [VideoOptions] or null if no media item is set
 */
val ExoPlayer.currentVideoOptions: VideoOptions?
    get() {
        val currentMediaItem = this.currentMediaItem
        return currentMediaItem?.localConfiguration?.tag as VideoOptions?
    }