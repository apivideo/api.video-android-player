package video.api.player.models

import video.api.player.extensions.parseAsVideoOptions
import java.net.URL

/**
 * Description of the video to play.
 *
 * @constructor Creates a [VideoOptions] from a [videoId]. an explicit [VideoType] and the private [token].
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

    /**
     * Creates a [VideoOptions] from a [videoId] and the private [token].
     * The [VideoType] is inferred from the video ID.
     *
     * @param videoId the video ID of the video to play
     * @param token the private video token (only needed for private video, set to null otherwise)
     */
    constructor(videoId: String, token: String?) : this(
        videoId,
        inferVideoType(videoId),
        token
    )

    /**
     * The URL of the HLS manifest.
     */
    internal val hlsManifestUrl = if (videoType == VideoType.VOD) {
        "$vodUrl/hls/manifest.m3u8"
    } else {
        "$liveUrl.m3u8"
    }

    /**
     * The URL of the session token.
     */
    internal val sessionTokenUrl = if (videoType == VideoType.VOD) {
        "$vodUrl/session"
    } else {
        "$liveUrl/session"
    }

    /**
     * The URL of the MP4 source.
     */
    internal val mp4Url = "$vodUrl/mp4/source.mp4"

    /**
     * The URL of the thumbnail.
     */
    internal val thumbnailUrl = "$vodUrl/thumbnail.jpg"

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

        /**
         * Infers the [VideoType] from the videoId.
         *
         * @param videoId the video ID
         * @return the [VideoType]
         */
        private fun inferVideoType(videoId: String): VideoType {
            return if (videoId.startsWith("vi")) {
                VideoType.VOD
            } else if (videoId.startsWith("li")) {
                VideoType.LIVE
            } else {
                throw IllegalArgumentException(
                    "Failed to infer the video type from the videoId: $videoId"
                )
            }
        }
    }
}