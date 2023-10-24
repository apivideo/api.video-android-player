package video.api.player.models

/**
 * The type of the video.
 */
enum class VideoType(val baseUrl: String) {
    /**
     * Video on demand.
     */
    VOD("https://vod.api.video/vod"),

    /**
     * Live video.
     */
    LIVE("https://live.api.video");

    companion object {
        /**
         * Creates a [VideoType] from a [String].
         */
        internal fun fromString(string: String): VideoType {
            return when (string.lowercase()) {
                "vod" -> VOD
                "live" -> LIVE
                else -> throw IllegalArgumentException("Unknown video type: $string")
            }
        }
    }
}