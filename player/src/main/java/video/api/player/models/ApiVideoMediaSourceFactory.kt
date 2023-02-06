package video.api.player.models

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes.APPLICATION_M3U8
import com.google.android.exoplayer2.util.MimeTypes.VIDEO_MP4
import java.security.InvalidParameterException

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
    private val videoUrlFactory = ApiVideoUrlFactory(videoOptions, onError)

    /**
     * Creates a [MediaItem] for [ExoPlayer] to read from api.video HLS.
     *
     * @param onSuccess The callback to call when the [MediaItem] is created
     */
    fun createMediaItem(
        onSuccess: (MediaItem) -> Unit,
    ) {
        videoUrlFactory.createVideoUrl {
            onSuccess(createMediaItem(it, videoOptions))
        }
    }

    /**
     * Creates a [MediaItem] for [ExoPlayer] to read from api.video HLS.
     *
     * @param onSuccess The callback to call when the [MediaItem] is created
     */
    fun createMp4MediaItem(
        onSuccess: (MediaItem) -> Unit,
    ) {
        videoUrlFactory.createMp4VideoUrl {
            onSuccess(createMediaItem(it, videoOptions))
        }
    }

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

    private fun createMediaItem(
        request: VideoRequest,
        videoOptions: VideoOptions,
    ): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(videoOptions.thumbnailUrl))
            .build()
        return MediaItem.Builder()
            .setUri(request.uri)
            .setMediaId(videoOptions.videoId)
            .setTag(videoOptions)
            .setMediaMetadata(mediaMetadata)
            .setMimeType(
                if (request.uri.endsWith("m3u8")) {
                    APPLICATION_M3U8
                } else if (request.uri.endsWith("mp4")) {
                    VIDEO_MP4
                } else {
                    throw InvalidParameterException("Invalid video format for ${request.uri}")
                }
            ) // For cast extension
            .build()
    }

    private fun createMediaSource(
        request: VideoRequest,
        videoOptions: VideoOptions,
    ): MediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        request.headers?.let { dataSourceFactory.setDefaultRequestProperties(it) }
        return DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(
            createMediaItem(request, videoOptions)
        )
    }
}