package video.api.player.models

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

/**
 * An [ApiVideoMediaSourceFactory] is a wrapper around [VideoOptions] to create a [MediaSource] for [ExoPlayer].
 * It also manages the token session for private videos.
 *
 * @param videoOptions The [VideoOptions] to use
 * @param onError The callback to call when an error occurs
 */
class ApiVideoMediaSourceFactory(
    private val videoOptions: VideoOptions,
    private val onError: (Exception) -> Unit,
) {
    private val videoUrlFactory = ApiVideoVideoUrlFactory(videoOptions, onError)

    /**
     * Creates a [MediaSource] for [ExoPlayer] to read from api.video HLS.
     *
     * @param onSuccess The callback to call when the [MediaSource] is created
     */
    fun createMediaSource(
        onSuccess: (MediaSource) -> Unit,
    ) {
        videoUrlFactory.createVideoUrl {
            onSuccess(createMediaSource(it, videoOptions))
        }
    }

    /**
     * Creates a [MediaSource] for [ExoPlayer] to read from api.video MP4.
     *
     * @param onSuccess The callback to call when the [MediaSource] is created
     */
    fun createMp4MediaSource(
        onSuccess: (MediaSource) -> Unit,
    ) {
        videoUrlFactory.createMp4VideoUrl {
            onSuccess(createMediaSource(it, videoOptions))
        }
    }

    private fun createMediaSource(
        request: VideoRequest,
        videoOptions: VideoOptions,
    ): MediaSource {
        val mediaItem = MediaItem.Builder().setUri(request.uri).setMediaId(videoOptions.videoId)
            .setTag(videoOptions).build()
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        request.headers?.let { dataSourceFactory.setDefaultRequestProperties(it) }
        return DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(
            mediaItem
        )
    }
}