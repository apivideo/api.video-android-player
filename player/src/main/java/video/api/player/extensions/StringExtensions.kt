package video.api.player.extensions

import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.utils.Utils

fun String.appendTokenSession(tokenSession: String?): String {
    return this + (tokenSession?.let { "?avh=$it" } ?: "")
}

/**
 * Converts String to a [VideoType].
 *
 * @return corresponding [VideoType] or an exception
 */
fun String.toVideoType() = VideoType.fromString(this)

/**
 * Converts an api.video URL String to a [VideoOptions].
 *
 * @return corresponding [VideoOptions] or an exception
 */
fun String.parseAsVideoOptions(
    vodDomainURL: String = VideoType.VOD.baseUrl,
    liveDomainURL: String = VideoType.LIVE.baseUrl
) = Utils.parseMediaUrl(this, vodDomainURL, liveDomainURL)
