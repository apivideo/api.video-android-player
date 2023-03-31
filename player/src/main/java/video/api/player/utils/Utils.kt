package video.api.player.utils

import video.api.player.extensions.toVideoType
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import java.io.IOException
import java.util.regex.Pattern

object Utils {
    private const val VOD_TOKEN_DELIMITER = "token"
    private const val LIVE_TOKEN_DELIMITER = "private"

    fun parseMediaUrl(mediaUrl: String): VideoOptions {
        val regex =
            "https:/.*[/](?<type>vod|live).*/(?<id>(vi|li)[^/^.]*)[/.].*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(mediaUrl)

        if (matcher.groupCount() < 3) {
            throw IOException("The media url doesn't look like an api.video URL.")
        }

        try {
            matcher.find()
            // Group naming is not supported before Android API 26
            val videoType =
                matcher.group(1)?.toVideoType() ?: throw IOException("Failed to get video type")
            val videoId = matcher.group(2) ?: throw IOException("Failed to get videoId")

            val tokenDelimiter =
                if (videoType == VideoType.VOD) VOD_TOKEN_DELIMITER else LIVE_TOKEN_DELIMITER
            val token =
                if (mediaUrl.contains("$tokenDelimiter/")) {
                    mediaUrl.substringAfter("$tokenDelimiter/").substringBefore('/')
                } else {
                    null
                }

            return VideoOptions(
                videoId,
                videoType,
                token
            )
        } catch (e: Exception) {
            e.message?.let {
                throw IOException("The media url doesn't look like an api.video URL: $it")
            } ?: throw IOException("The media url doesn't look like an api.video URL.")
        }
    }
}