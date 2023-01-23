package video.api.player

import com.google.android.exoplayer2.ExoPlayer
import video.api.player.models.ApiVideoMediaSourceFactory
import video.api.player.models.VideoOptions

/**
 * Sets a media source to [ExoPlayer] to read from api.video HLS.
 *
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to use
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
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to use
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
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to use
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
 * @param mediaSourceFactory The [ApiVideoMediaSourceFactory] to use
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