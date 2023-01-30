package video.api.player.models

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import video.api.player.utils.RequestManager

/**
 * An [ApiVideoUrlFactory] is a wrapper around [VideoOptions] to create an [Uri] for player.
 * It also manages the token session for private videos.
 *
 * @param videoOptions The [VideoOptions] to use
 * @param onError The callback to call when an error occurs
 */
class ApiVideoUrlFactory(
    private val videoOptions: VideoOptions,
    private val onError: (Exception) -> Unit,
) {
    private var xTokenSession: String? = null

    /**
     * Creates a [MediaSource] for [ExoPlayer] to read from api.video HLS.
     *
     * @param onSuccess The callback to call when the [MediaSource] is created
     */
    fun createVideoUrl(
        onSuccess: (VideoRequest) -> Unit,
    ) {
        createVideoUrl(videoOptions.hlsManifestUrl, onSuccess)
    }

    /**
     * Creates a [MediaSource] for [ExoPlayer] to read from api.video MP4.
     *
     * @param onSuccess The callback to call when the [MediaSource] is created
     */
    fun createMp4VideoUrl(
        onSuccess: (VideoRequest) -> Unit,
    ) {
        createVideoUrl(videoOptions.mp4Url, onSuccess)
    }

    private fun createVideoUrl(
        uri: String,
        onSuccess: (VideoRequest) -> Unit,
    ) {
        if (videoOptions.token == null) {
            onSuccess(createVideoUrl(uri))
        } else {
            // Check if the token session already exists
            if (xTokenSession != null) {
                onSuccess(createVideoUrl(uri, xTokenSession))
            } else {
                RequestManager.getSessionToken(
                    videoOptions.sessionTokenUrl,
                    videoOptions.videoType,
                    {
                        xTokenSession = it
                        onSuccess(createVideoUrl(uri, xTokenSession))
                    },
                    {
                        onError(it)
                    })
            }
        }
    }

    private fun createVideoUrl(
        uri: String,
        tokenSession: String? = null
    ): VideoRequest {
        return VideoRequest(uri, tokenSession?.let { mapOf("X-Token-Session" to it) })
    }
}