package video.api.player.models

import video.api.player.utils.RequestManager

/**
 * [ApiVideoMediaFactory] is a wrapper around [VideoOptions] to create URL for the different
 * api.video media items (HLS, MP4, thumbnails).
 * It also stores the token session for private videos.
 *
 * @param videoOptions The [VideoOptions] to use
 * @param onError The callback to call when an error occurs
 */
class ApiVideoMediaFactory(
    private val videoOptions: VideoOptions,
    private val onError: (Exception) -> Unit,
) {
    private var xTokenSession: String? = null

    /**
     * Gets the URL to read from api.video HLS.
     *
     * @param onSuccess The callback that returns the HLS URL
     */
    fun getVideoUrl(
        onSuccess: (PlayerMediaRequest) -> Unit,
    ) {
        getTokenSession(videoOptions.hlsManifestUrl, onSuccess)
    }

    /**
     * Gets the URL to read from api.video MP4.
     *
     * @param onSuccess The callback that returns the MP4 URL
     */
    fun getMp4VideoUrl(
        onSuccess: (PlayerMediaRequest) -> Unit,
    ) {
        getTokenSession(videoOptions.mp4Url, onSuccess)
    }

    /**
     * Gets the URL for the video's thumbnail.
     *
     * @param onSuccess The callback that returns the thumbnail URL
     */
    fun getThumbnailUrl(
        onSuccess: (String) -> Unit,
    ) {
        getTokenSession(videoOptions.thumbnailUrl) { request -> onSuccess(request.uriWithQuery) }
    }

    private fun getTokenSession(
        uri: String,
        onSuccess: (PlayerMediaRequest) -> Unit,
    ) {
        if (videoOptions.token == null) {
            onSuccess(PlayerMediaRequest(uri))
        } else {
            // Check if the token session already exists
            if (xTokenSession != null) {
                onSuccess(PlayerMediaRequest(uri, xTokenSession))
            } else {
                RequestManager.getSessionToken(
                    videoOptions.sessionTokenUrl,
                    {
                        xTokenSession = it
                        onSuccess(PlayerMediaRequest(uri, xTokenSession))
                    },
                    {
                        onError(it)
                    })
            }
        }
    }
}