package video.api.player.models

/**
 * Encapsulates a video URL and headers.
 */
data class VideoRequest(
    val uri: String,
    val headers: Map<String, String>? = null
)