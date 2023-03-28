package video.api.player.models

enum class VideoType(val baseUrl: String) {
    VOD("https://vod.api.video/vod"),
    LIVE("https://live.api.video");

    companion object {
        fun fromString(string: String): VideoType {
            return when (string.lowercase()) {
                "vod" -> VOD
                "live" -> LIVE
                else -> throw IllegalArgumentException("Unknown video type: $string")
            }
        }
    }
}