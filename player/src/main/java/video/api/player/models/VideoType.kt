package video.api.player.models

enum class VideoType(val baseUrl: String) {
    VOD("https://vod.api.video/vod"),
    LIVE("https://live.api.video")
}