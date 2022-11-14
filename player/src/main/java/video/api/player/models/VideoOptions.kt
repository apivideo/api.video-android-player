package video.api.player.models

/**
 * Description of the video to play.
 *
 * @param videoId the video ID of the video to play
 * @param videoType the [VideoType] of the video to play. Only [VideoType.VOD] is supported.
 * @param token private video token (only needed for private video, set to null otherwise)
 */
data class VideoOptions(
    val videoId: String,
    val videoType: VideoType,
    val token: String? = null
) {
    private val baseUrl = "${videoType.baseUrl}/$videoId"
    private val vodUrl = baseUrl + (token?.let { "/token/$it" } ?: "")
    private val liveUrl = videoType.baseUrl + (token?.let { "/private/$it" } ?: "") + "/$videoId"

    val hlsManifestUrl = if (videoType == VideoType.VOD) {
        "$vodUrl/hls/manifest.m3u8"
    } else {
        "$liveUrl.m3u8"
    }

    val mp4Url = "$vodUrl/mp4/source.mp4"
}