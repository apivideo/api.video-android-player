package video.api.player.extensions

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import video.api.player.models.ApiVideoExoPlayerMediaFactory
import video.api.player.models.VideoOptions

private const val TAG = "ExoPlayerExtensions"

/**
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun ExoPlayer.setMediaSource(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMediaSource(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
@OptIn(UnstableApi::class)
fun ExoPlayer.setMediaSource(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMediaSource {
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
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMp4MediaSource(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Sets a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
@OptIn(UnstableApi::class)
fun ExoPlayer.setMp4MediaSource(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMp4MediaSource {
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
    addMediaSource(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
@OptIn(UnstableApi::class)
fun ExoPlayer.addMediaSource(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMediaSource {
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
    addMp4MediaSource(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Adds a [MediaSource] to [ExoPlayer] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
@OptIn(UnstableApi::class)
fun ExoPlayer.addMp4MediaSource(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMp4MediaSource {
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
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMediaItem(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Sets a [MediaItem] to [Player] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
fun Player.setMediaItem(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMediaItem {
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
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMp4MediaItem(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Sets a [MediaItem] to [Player] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
fun Player.setMp4MediaItem(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMp4MediaItem {
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
    addMediaItem(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
fun Player.addMediaItem(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMediaItem {
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
    addMp4MediaItem(ApiVideoExoPlayerMediaFactory(videoOptions, onError))
}

/**
 * Adds a [MediaItem] to [Player] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param mediaSourceFactory The [ApiVideoExoPlayerMediaFactory] to play
 */
fun Player.addMp4MediaItem(
    mediaSourceFactory: ApiVideoExoPlayerMediaFactory,
) {
    mediaSourceFactory.getMp4MediaItem {
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