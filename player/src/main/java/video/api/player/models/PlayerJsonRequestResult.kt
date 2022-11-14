package video.api.player.models

data class PlayerJsonRequestResult(
    val playerManifest: String, // Not use anymore
    val headers: Map<String, String>?
)