package video.api.player.example

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.ClientError
import com.google.android.material.snackbar.Snackbar
import video.api.player.ApiVideoPlayerController
import video.api.player.example.databinding.ActivityMainBinding
import video.api.player.models.ApiVideoPlayerFullScreenController
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.views.ApiVideoExoPlayerView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val sharedPref: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val videoId: String
        get() = sharedPref.getString(getString(R.string.video_id_key), null)
            ?: throw IllegalArgumentException("Video ID is not set")
    private val videoType: VideoType
        get() {
            val videoType =
                sharedPref.getBoolean(getString(R.string.video_type_key), false)
            return if (videoType) {
                VideoType.LIVE
            } else {
                VideoType.VOD
            }
        }
    private val privateVideoToken: String?
        get() {
            val token = sharedPref.getString(getString(R.string.private_video_token_key), null)
            return if (!token.isNullOrEmpty()) {
                token
            } else {
                null
            }
        }

    private val playerControllerListener = object : ApiVideoPlayerController.Listener {
        override fun onError(error: Exception) {
            val message = when {
                error.message != null -> {
                    error.message
                }

                error is ClientError -> {
                    error.networkResponse.statusCode.toString()
                }

                else -> {
                    error.toString()
                }
            }
            displayMessage(message ?: "Unknown error")
        }

        override fun onReady() {
            displayMessage("onReady")
        }

        override fun onFirstPlay() {
            displayMessage("onFirstPlay")
        }

        override fun onPlay() {
            //       displayMessage("onPlay")
        }

        override fun onPause() {
            //       displayMessage("onPause")
        }

        override fun onSeek() {
            displayMessage("onSeek")
        }

        override fun onEnd() {
            displayMessage("onEnd")
        }

        override fun onVideoSizeChanged(resolution: Size) {
            displayMessage("onVideoSizeChanged: $resolution")
        }
    }

    private val fullScreenController: ApiVideoPlayerFullScreenController by lazy {
        ApiVideoPlayerFullScreenController(
            supportFragmentManager,
            binding.playerView,
            playerController,
            object : ApiVideoExoPlayerView.FullScreenListener {
                override fun onFullScreenModeChanged(isFullScreen: Boolean) {
                    requestedOrientation = if (isFullScreen) {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    }
                }
            }
        )
    }
    private val playerController: ApiVideoPlayerController by lazy {
        ApiVideoPlayerController(
            applicationContext,
            null,
            true,
            playerControllerListener,
            binding.playerView
        )
    }

    private fun displayMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            showMenu(view)
        }

        binding.currentTime.setLabelFormatter { value: Float ->
            val newCurrentTime = value * playerController.duration / 100
            "${String.format("%.3f", newCurrentTime)}/${playerController.duration}"
        }
        binding.currentTime.addOnChangeListener { _, value, _ ->
            playerController.currentTime = value * playerController.duration / 100
        }

        binding.play.setOnClickListener { playerController.play() }
        binding.pause.setOnClickListener { playerController.pause() }
        binding.stop.setOnClickListener { playerController.stop() }

        binding.mute.setOnClickListener { playerController.isMuted = true }
        binding.volume.addOnChangeListener { _, volume, _ ->
            playerController.volume = (volume / 100f)
        }
        binding.unmute.setOnClickListener { playerController.isMuted = false }

        binding.playerView.fullScreenListener = fullScreenController
        binding.showFullScreenButton.setOnClickListener {
            binding.playerView.fullScreenListener = fullScreenController
        }
        binding.hideFullScreenButton.setOnClickListener {
            binding.playerView.fullScreenListener = null
        }

        binding.showControls.setOnClickListener { binding.playerView.showControls = true }
        binding.hideControls.setOnClickListener { binding.playerView.showControls = false }

        binding.showSubtitles.setOnClickListener { binding.playerView.showSubtitles = true }
        binding.hideSubtitles.setOnClickListener { binding.playerView.showSubtitles = false }
    }

    override fun onResume() {
        super.onResume()

        loadVideo()
    }

    override fun onPause() {
        super.onPause()

        playerController.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        playerController.release()
    }

    private fun loadVideo() {
        try {
            playerController.videoOptions = VideoOptions(videoId, videoType, privateVideoToken)
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading video", e)
            displayMessage("Error while loading video: ${e.message}")
        }
    }

    private fun showMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_main, popup.menu)
        popup.show()
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_settings) {
                goToSettingsActivity()
                true
            } else {
                Log.e(TAG, "Unknown menu item ${it.itemId}")
                false
            }
        }
    }

    private fun goToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
