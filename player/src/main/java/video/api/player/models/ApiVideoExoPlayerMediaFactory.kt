package video.api.player.models

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes.APPLICATION_M3U8
import androidx.media3.common.MimeTypes.VIDEO_MP4
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import video.api.player.extensions.appendTokenSession
import java.security.InvalidParameterException

/**
 * [ApiVideoExoPlayerMediaFactory] has the same purpose as [ApiVideoMediaFactory] but dedicated to
 * [ExoPlayer].
 * It also stores the token session for private videos.
 *
 * @param videoOptions The [VideoOptions] to use
 * @param onError The callback to call when an error occurs
 */
class ApiVideoExoPlayerMediaFactory(
    private val videoOptions: VideoOptions,
    private val onError: (Exception) -> Unit,
) {
    private val videoUrlFactory = ApiVideoMediaFactory(videoOptions, onError)

    /**
     * Gets a [MediaItem] for a [Player] instance to read from api.video HLS.
     *
     * @param onSuccess The callback that returns the [MediaItem]
     */
    fun getMediaItem(
        onSuccess: (MediaItem) -> Unit,
    ) {
        videoUrlFactory.getVideoUrl {
            onSuccess(createMediaItem(it, videoOptions))
        }
    }

    /**
     * Gets a [MediaItem] for a [Player] instance to read from api.video HLS.
     *
     * @param onSuccess The callback that returns the [MediaItem]
     */
    fun getMp4MediaItem(
        onSuccess: (MediaItem) -> Unit,
    ) {
        videoUrlFactory.getMp4VideoUrl {
            onSuccess(createMediaItem(it, videoOptions))
        }
    }

    /**
     * Gets a [MediaSource] for an [ExoPlayer] instance to read from api.video HLS.
     *
     * @param onSuccess The callback that returns the [MediaSource]
     */
    fun getMediaSource(
        onSuccess: (MediaSource) -> Unit,
    ) {
        videoUrlFactory.getVideoUrl {
            onSuccess(createMediaSource(it, videoOptions))
        }
    }

    /**
     * Gets a [MediaSource] for an [ExoPlayer] instance to read from api.video MP4.
     *
     * @param onSuccess The callback that returns the [MediaSource]
     */
    fun getMp4MediaSource(
        onSuccess: (MediaSource) -> Unit,
    ) {
        videoUrlFactory.getMp4VideoUrl {
            onSuccess(createMediaSource(it, videoOptions))
        }
    }

    /**
     * Gets the thumbnail url for the video.
     *
     * @param onSuccess The callback that returns the thumbnail url
     */
    fun getThumbnailUrl(onSuccess: (String) -> Unit) {
        videoUrlFactory.getThumbnailUrl(onSuccess)
    }

    private fun createMediaItem(
        request: PlayerMediaRequest,
        videoOptions: VideoOptions,
    ): MediaItem {
        val mediaMetadata = MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(videoOptions.thumbnailUrl.appendTokenSession(request.xTokenSession)))
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

    @OptIn(UnstableApi::class)
    private fun createMediaSource(
        request: PlayerMediaRequest,
        videoOptions: VideoOptions,
    ): MediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        request.headers?.let { dataSourceFactory.setDefaultRequestProperties(it) }
        return DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(
            createMediaItem(request, videoOptions)
        )
    }
}