package video.api.player.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PlayerJson(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val video: PlayerJsonVideo,
    val chapters: Map<String, String> = emptyMap(),
    val panoramic: Boolean = false,
    val live: Boolean = false,
    //val theme: PlayerJsonTheme? = null,
    val autoplay: Boolean = false,
    val loop: Boolean = false,
    val `visible-controls`: Boolean = true,
    val `visible-title`: Boolean = true,
    val api: Boolean = false
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun from(serializable: String) =
            json.decodeFromString(serializer(), serializable)
    }
}

@Serializable
data class PlayerJsonVideo(val src: String, val poster: String? = null, val mp4: String? = null)

@Serializable
data class PlayerJsonTheme(val src: String? = null, val logo: PlayerJsonLogo? = null)

@Serializable
data class PlayerJsonLogo(
    val src: String,
    val url: String,
    val title: String? = null,
    val target: String? = null
)