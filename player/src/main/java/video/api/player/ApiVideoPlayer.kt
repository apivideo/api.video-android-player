package video.api.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import video.api.player.models.PlayerJson
import video.api.player.models.VideoType

class ApiVideoPlayer(
    private val context: Context,
    private val videoId: String,
    private val videoType: VideoType,
    private val listener: Listener,
    private val playerView: StyledPlayerView
) {
    private val queue = Volley.newRequestQueue(context).apply {
        start()
    }
    private lateinit var playerJson: PlayerJson
    private val exoplayerListener = object : Player.Listener {
    }
    private val exoplayer = ExoPlayer.Builder(context).build().apply {
        addListener(exoplayerListener)
    }

    init {
        getPlayerJson({
            playerJson = it
            preparePlayer(playerJson)
        }, {
            listener.onError(it)
        })
    }

    var currentTime: Float
        /**
         * Get current video position in seconds
         *
         * @return current video position in seconds
         */
        get() = exoplayer.currentPosition.toFloat() / 1000.0f
        set(value) {
            exoplayer.seekTo((value * 1000.0).toLong())
        }

    val duration: Float
        /**
         * Get video duration in seconds
         *
         * @return video duration in seconds
         */
        get() = exoplayer.duration / 1000.0f

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

    var volume: Float
        /**
         * Get audio volume
         *
         * @return volume between 0 and 1.0
         */
        get() = exoplayer.deviceVolume.toFloat() / (exoplayer.deviceInfo.maxVolume - exoplayer.deviceInfo.minVolume) - exoplayer.deviceInfo.minVolume
        /**
         * Set audio volume
         *
         * @param value volume between 0 and 1.0
         */
        set(value) {
            exoplayer.deviceVolume =
                (value * (exoplayer.deviceInfo.maxVolume - exoplayer.deviceInfo.minVolume) + exoplayer.deviceInfo.minVolume).toInt()
        }

    fun mute() {
        exoplayer.isDeviceMuted = true
    }

    fun unmute() {
        exoplayer.isDeviceMuted = false
    }

    fun hideControls() {
        playerView.useController = false
    }

    fun showControls() {
        playerView.useController = true
        playerView.showController()
    }

    private fun preparePlayer(playerJson: PlayerJson) {
        val mediaItem = MediaItem.fromUri(playerJson.video.src)

        if (playerJson.loop) {
            exoplayer.repeatMode = Player.REPEAT_MODE_ALL
        }
        exoplayer.playWhenReady = playerJson.autoplay

        exoplayer.setMediaItem(mediaItem)
        exoplayer.prepare()
        playerView.player = exoplayer

        if (playerJson.video.poster != null) {
            loadPoster(playerJson.video.poster) { }
        }
        playerView.useController = playerJson.`visible-controls`
    }

    private fun loadPoster(posterUrl: String, callback: (Drawable) -> Unit) {
        val imageRequest = ImageRequest(
            posterUrl,
            { bitmap ->
                try {
                    callback(BitmapDrawable(context.resources, bitmap))
                } catch (e: Exception) {
                    Log.e(TAG, e.message ?: "Failed to transform poster to drawable")
                }
            },
            0,
            0,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.ARGB_8888,
            { error ->
                Log.e(TAG, error.message ?: "Failed to get poster")
            }
        )

        queue.add(imageRequest)
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
        private val TAG = "ApiVideoPlayer"

        private fun getPlayerJsonUrl(videoId: String, videoType: VideoType) =
            videoType.baseUrl + videoId + "/player.json"
    }

    interface Listener {
        fun onError(error: String)
    }
}