package video.api.player

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import video.api.player.models.PlayerJson
import video.api.player.models.VideoType

class ApiVideoPlayer(
    context: Context,
    private val videoId: String,
    private val videoType: VideoType,
    private val listener: ApiVideoPlayerListener
) {
    private val queue = Volley.newRequestQueue(context).apply {
        start()
    }
    private lateinit var playerJson: PlayerJson

    init {
        getPlayerJson({
            playerJson = it
        }, {
            listener.onError(it)
        })
    }

    private fun getPlayerJson(onSuccess: (PlayerJson) -> Unit, onError: (String) -> Unit) {
        val stringRequest = StringRequest(
            getPlayerJsonUrl(videoId, videoType),
            { response ->
                try {
                    onSuccess(PlayerJson.from(response))
                } catch (e: Exception) {
                    onError(e.message ?: "Failed to deserialized player.json")
                }
            },
            { error ->
                onError(error.message ?: "Failed to get player.json")
            }
        )

        queue.add(stringRequest)
    }

    companion object {
        private fun getPlayerJsonUrl(videoId: String, videoType: VideoType) =
            videoType.baseUrl + videoId + "/player.json"
    }
}