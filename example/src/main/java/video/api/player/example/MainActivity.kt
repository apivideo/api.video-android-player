package video.api.player.example

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import video.api.player.ApiVideoPlayer
import video.api.player.example.databinding.ActivityMainBinding
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

    private val playerListener = object : ApiVideoPlayer.Listener {
        override fun onError(error: String) {
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }

    private lateinit var player: ApiVideoPlayer

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

        binding.mute.setOnClickListener { player.mute() }
        binding.volume.addOnChangeListener { _, volume, _ ->
            player.volume = (volume / 100f)
        }
        binding.unmute.setOnClickListener { player.unmute() }
    }

    override fun onResume() {
        super.onResume()

        loadPlayer()
    }

    private fun loadPlayer() {
        player = ApiVideoPlayer(this, videoId, videoType, playerListener, binding.playerView)
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
