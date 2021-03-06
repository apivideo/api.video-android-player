package video.api.player.example

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import com.android.volley.ClientError
import com.google.android.material.snackbar.Snackbar
import video.api.player.ApiVideoPlayer
import video.api.player.example.databinding.ActivityMainBinding
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val sharedPref: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val videoId: String
        get() = sharedPref.getString(getString(R.string.video_id_key), "")
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

    private val playerListener = object : ApiVideoPlayer.Listener {
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

        override fun onFullScreenModeChanged(isFullScreen: Boolean) {
            if (isFullScreen) {
                supportActionBar?.hide()
                hideSystemUI()
                binding.fab.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                binding.playerView.layoutParams.apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            } else {
                supportActionBar?.show()
                showSystemUI()
                binding.fab.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                binding.playerView.layoutParams.apply {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
        }

        override fun onVideoSizeChanged(resolution: Size) {
            displayMessage("onVideoSizeChanged: $resolution")
        }
    }

    private lateinit var player: ApiVideoPlayer

    private fun displayMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            showMenu(view)
        }

        binding.currentTime.setLabelFormatter { value: Float ->
            val newCurrentTime = value * player.duration / 100
            "${String.format("%.3f", newCurrentTime)}/${player.duration}"
        }
        binding.currentTime.addOnChangeListener { _, value, _ ->
            player.currentTime = value * player.duration / 100
        }

        binding.play.setOnClickListener { player.play() }
        binding.pause.setOnClickListener { player.pause() }
        binding.stop.setOnClickListener { player.stop() }

        binding.mute.setOnClickListener { player.isMuted = true }
        binding.volume.addOnChangeListener { _, volume, _ ->
            player.volume = (volume / 100f)
        }
        binding.unmute.setOnClickListener { player.isMuted = false }

        binding.showControls.setOnClickListener { player.showControls() }
        binding.hideControls.setOnClickListener { player.hideControls() }

        binding.showSubtitles.setOnClickListener { player.showSubtitles() }
        binding.hideSubtitles.setOnClickListener { player.hideSubtitles() }
    }

    override fun onResume() {
        super.onResume()

        loadPlayer()
    }

    private fun loadPlayer() {
        player = ApiVideoPlayer(
            this,
            VideoOptions(videoId, videoType, privateVideoToken),
            playerListener,
            binding.playerView,
            showFullScreenButton = true
        )
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
}
