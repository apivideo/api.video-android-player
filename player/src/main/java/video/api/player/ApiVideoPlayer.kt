package video.api.player

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.models.PlayerJson
import video.api.player.models.VideoType

class ApiVideoPlayer(
    context: Context,
    private val videoId: String,
    private val videoType: VideoType,
    private val listener: Listener,
    private val playerView: StyledPlayerView
) {
    private val queue = Volley.newRequestQueue(context).apply {
        start()
    }
    private lateinit var playerJson: PlayerJson
    private val exoplayer = ExoPlayer.Builder(context).build()

    init {
        getPlayerJson({
            playerJson = it
            preparePlayer(playerJson.video.src)
        }, {
            listener.onError(it)
        })
    }

    fun play() {
        exoplayer.play()
    }

    fun pause() {
        exoplayer.pause()
    }

    fun stop() {
        exoplayer.stop()
    }

    fun release() {
        exoplayer.release()
    }

    fun mute() {
        exoplayer.isDeviceMuted = true
    }

    fun unmute() {
        exoplayer.isDeviceMuted = false
    }

    private fun preparePlayer(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoplayer.setMediaItem(mediaItem)
        exoplayer.prepare()
        playerView.player = exoplayer
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

    interface Listener {
        fun onError(error: String)
    }
}