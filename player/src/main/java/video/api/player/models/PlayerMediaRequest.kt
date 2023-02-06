package video.api.player.models

import video.api.player.extensions.appendTokenSession

/**
 * Encapsulates a video URL and headers.
 *
 * @param uri The URL to request
 * @param xTokenSession The X-Token-Session value to add in the query or in the headers
 */
data class PlayerMediaRequest(
    val uri: String,
    val xTokenSession: String? = null
) {
    /**
     * Headers to add to the request.
     */
    val headers: Map<String, String>? =
        xTokenSession?.let { mapOf("X-Token-Session" to it) }

    /**
     * URL with the X-Token-Session query parameter.
     */
    val uriWithQuery = uri.appendTokenSession(xTokenSession)
}