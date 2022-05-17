package video.api.player.models

/**
 * Video configuration
 *
 * @param videoId the video ID of the video to play
 * @param videoType the [VideoType] of the video to play. Only [VideoType.VOD] is supported.
 * @param token private video token (only needed for private video, set to null otherwise)
 */
data class VideoOptions(
    val videoId: String,
    val videoType: VideoType,
    val token: String? = null
)