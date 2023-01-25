package video.api.player

import android.media.MediaPlayer
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import video.api.player.extensions.setDataSource
import video.api.player.models.ApiVideoUrlFactory
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediaPlayerExtensionsKtTest {
    private var player = MediaPlayer()
    private var apiKey: String? = null
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setUp() {
        val arguments = InstrumentationRegistry.getArguments()
        apiKey = arguments.getString("INTEGRATION_TESTS_API_KEY")
    }

    @After
    fun tearDown() {
        player.release()
    }

    @Test
    fun assertDurationTest() {
        val lock = CountDownLatch(1)

        player.setOnPreparedListener { lock.countDown() }
        player.setOnErrorListener { _, _, _ ->
            Log.e(TAG, "Player error")
            false
        }
        player.setDataSource(context,
            ApiVideoUrlFactory(
                VideoOptions(VideoIds.VALID_VIDEO_ID, VideoType.VOD)
            ) {
                Log.e(TAG, "Error: $it")
            }
        )
        player.prepare()
        lock.await(5, TimeUnit.SECONDS)

        assertEquals(60200, player.duration)
    }

    companion object {
        private const val TAG = "VideoViewExtensionsKtTest"
    }
}
