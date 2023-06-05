package video.api.player.extensions

import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.utils.Utils
import java.net.URL

/**
 * Converts an api.video URL String to a [VideoOptions].
 *
 * @return corresponding [VideoOptions] or an exception
 */
fun URL.parseAsVideoOptions(
    vodDomainURL: URL = URL(VideoType.VOD.baseUrl),
    liveDomainURL: URL = URL(VideoType.LIVE.baseUrl)
) = Utils.parseMediaUrl(this, vodDomainURL, liveDomainURL)
