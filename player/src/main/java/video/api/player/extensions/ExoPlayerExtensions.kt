package video.api.player.extensions

import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.Player
import video.api.player.models.ApiVideoMediaSourceFactory
import video.api.player.models.VideoOptions

private const val TAG = "ExoPlayerExtensions"

/**
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video HLS.
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
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video HLS.
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
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
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
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
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
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video HLS.
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
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video HLS.
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
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
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
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
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
 * Sets a [MediaItem] to [Player] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun Player.setMediaItem(
    videoOptions: VideoOptions,
) {
    setMediaItem(ApiVideoMediaSourceFactory(videoOptions) { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    })
}

/**
 * Sets a [MediaItem] to [Player] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun Player.setMediaItem(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMediaItem {
        setMediaItem(it)
    }
}

/**
 * Sets a [MediaItem] to [Player] to read from api.video MP4 URL.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun Player.setMp4MediaItem(
    videoOptions: VideoOptions,
) {
    setMp4MediaItem(ApiVideoMediaSourceFactory(videoOptions) { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    })
}

/**
 * Sets a [MediaItem] to [Player] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun Player.setMp4MediaItem(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMp4MediaItem {
        setMediaItem(it)
    }
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun Player.addMediaItem(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    addMediaItem(ApiVideoMediaSourceFactory(videoOptions, onError))
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun Player.addMediaItem(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMediaItem {
        addMediaItem(it)
    }
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video MP4 URL.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun Player.addMp4MediaItem(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    addMp4MediaItem(ApiVideoMediaSourceFactory(videoOptions, onError))
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to play
 */
fun Player.addMp4MediaItem(
    mediaSourceFactory: ApiVideoMediaSourceFactory,
) {
    mediaSourceFactory.createMp4MediaItem {
        addMediaItem(it)
    }
}

/**
 * Get the current video options
 *
 * @return The current [VideoOptions] or null if no media item is set
 */
val Player.currentVideoOptions: VideoOptions?
    get() {
        val currentMediaItem = this.currentMediaItem
        return currentMediaItem?.localConfiguration?.tag as VideoOptions?
    }

/**
 * Copy the state of another player to this player
 *
 * @param otherPlayer The player to copy the state from
 */
fun Player.getStateFrom(otherPlayer: Player) {
    var playbackPositionMs = C.TIME_UNSET
    var currentItemIndex = C.INDEX_UNSET
    var playWhenReady = false

    // Get state from other player
    val playbackState: Int = otherPlayer.playbackState
    if (playbackState != Player.STATE_ENDED) {
        playbackPositionMs = otherPlayer.currentPosition
        playWhenReady = otherPlayer.playWhenReady
        currentItemIndex = otherPlayer.currentMediaItemIndex
    }

    val mediaQueue = mutableListOf<MediaItem>()
    for (i in 0 until otherPlayer.mediaItemCount) {
        mediaQueue.add(otherPlayer.getMediaItemAt(i))
    }

    setMediaItems(mediaQueue, currentItemIndex, playbackPositionMs)
    setPlayWhenReady(playWhenReady)
    prepare()
}