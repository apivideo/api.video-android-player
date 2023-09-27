package video.api.player.utils

import video.api.player.extensions.toVideoType
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import java.io.IOException
import java.net.URL
import java.util.regex.Pattern

object Utils {
    private const val VOD_TOKEN_DELIMITER = "token"
    private const val LIVE_TOKEN_DELIMITER = "private"

    fun parseMediaUrl(
        mediaUrl: String
    ) = parseMediaUrl(URL(mediaUrl))

    fun parseMediaUrl(
        mediaUrl: URL
    ): VideoOptions {
        /**
         * Group naming is not supported before Android API 26 and crashes
         * on very old version such as Android API 21
         */
        val regex = "https://[^/]+/(?>(vod|live)/)?(?>.*/)?((vi|li)[^/^.]*).*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(mediaUrl.toString())

        if (matcher.groupCount() < 3) {
            throw IOException("The media url doesn't look like an api.video URL.")
        }

        try {
            matcher.find()
            // Group naming is not supported before Android API 26
            val videoId = matcher.group(2) ?: throw IOException("Failed to get videoId")

            // For live, we might not have a type for now because there isn't any `/live/` in the URL.
            val firstGroup = matcher.group(1)
            val videoType = firstGroup?.toVideoType()
                ?: if (videoId.startsWith("li")) VideoType.LIVE else throw IOException(
                    "Failed to get videoType"
                )

            val tokenDelimiter =
                if (videoType == VideoType.VOD) VOD_TOKEN_DELIMITER else LIVE_TOKEN_DELIMITER
            val token =
                if (mediaUrl.toString().contains("$tokenDelimiter/")) {
                    mediaUrl.toString().substringAfter("$tokenDelimiter/").substringBefore('/')
                } else {
                    null
                }

            return VideoOptions(
                videoId,
                videoType,
                token
            )
        } catch (e: Exception) {
            throw IOException("The media url doesn't look like an api.video URL", e)
        }
    }
}