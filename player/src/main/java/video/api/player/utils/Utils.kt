package video.api.player.utils

import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import java.io.IOException
import java.net.URL
import java.util.regex.Pattern

object Utils {
    private const val VOD_TOKEN_DELIMITER = "token"
    private const val LIVE_TOKEN_DELIMITER = "private"

    fun parseMediaUrl(
        mediaUrl: String,
        vodDomainURL: String = VideoType.VOD.baseUrl,
        liveDomainURL: String = VideoType.LIVE.baseUrl
    ) = parseMediaUrl(URL(mediaUrl), URL(vodDomainURL), URL(liveDomainURL))

    fun parseMediaUrl(
        mediaUrl: URL,
        vodDomainURL: URL = URL(VideoType.VOD.baseUrl),
        liveDomainURL: URL = URL(VideoType.LIVE.baseUrl)
    ): VideoOptions {
        val regex =
            "https:/.*[/].*/(?<id>(vi|li)[^/^.]*)[/.].*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(mediaUrl.toString())

        if (matcher.groupCount() < 2) {
            throw IOException("The media url doesn't look like an api.video URL.")
        }

        try {
            matcher.find()
            // Group naming is not supported before Android API 26
            val videoType =
                if (mediaUrl.toString().startsWith(vodDomainURL.toString())) {
                    VideoType.VOD
                } else if (mediaUrl.toString().startsWith(liveDomainURL.toString())) {
                    VideoType.LIVE
                } else {
                    throw IOException("The media url must start with $vodDomainURL or $liveDomainURL")
                }
            val videoId = matcher.group(1) ?: throw IOException("Failed to get videoId")

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