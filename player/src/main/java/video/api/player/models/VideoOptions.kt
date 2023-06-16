package video.api.player.models

import video.api.player.extensions.parseAsVideoOptions
import video.api.player.utils.Utils
import java.net.URL

/**
 * Description of the video to play.
 *
 * @param videoId the video ID of the video to play
 * @param videoType the [VideoType] of the video to play. Only [VideoType.VOD] is supported.
 * @param token the private video token (only needed for private video, set to null otherwise)
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

    val sessionTokenUrl = if (videoType == VideoType.VOD) {
        "$vodUrl/session"
    } else {
        hlsManifestUrl // Temp: return the same url as hlsManifestUrl for live
    }

    val mp4Url = "$vodUrl/mp4/source.mp4"

    val thumbnailUrl = "$vodUrl/thumbnail.jpg"

    companion object {
        /**
         * Creates a [VideoOptions] from an api.video URL.
         *
         * @param url the URL of the video to play
         */
        fun fromUrl(
            url: String
        ) = url.parseAsVideoOptions()

        /**
         * Creates a [VideoOptions] from an api.video URL.
         *
         * @param url the URL of the video to play
         */
        fun fromUrl(
            url: URL
        ) = url.parseAsVideoOptions()
    }
}