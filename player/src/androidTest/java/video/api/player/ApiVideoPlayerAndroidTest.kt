package video.api.player

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.exoplayer2.ui.StyledPlayerView
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import video.api.client.api.clients.VideosApi
import video.api.client.api.models.Environment
import video.api.player.models.VideoType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class ApiVideoPlayerAndroidTest {
    companion object {
        private const val VALID_VIDEO_ID = "vi2G6Qr8ZVE67dWLNymk7qbc"
        private const val INVALID_VIDEO_ID = "unknownVideoId"
        private const val PRIVATE_VIDEO_ID = "viMgTZ1KULkXrjFfDCTBtLs"
    }

    private lateinit var player: ApiVideoPlayer
    private var apiKey: String? = null
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val playerView = StyledPlayerView(context)

    @Before
    fun setUp() {
        val arguments = InstrumentationRegistry.getArguments()
        apiKey = arguments.getString("INTEGRATION_TESTS_API_KEY")
    }

    @Test
    fun assertDurationTest() {
        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayer.Listener {
            override fun onReady() {
                lock.countDown()
            }
        }
        player = ApiVideoPlayer(context, VALID_VIDEO_ID, VideoType.VOD, listener, playerView)
        lock.await(5, TimeUnit.SECONDS)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            assertEquals(60.2F, player.duration)
        }
    }


    @Test
    fun unknownVideoId() {
        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                lock.countDown()
            }
        }
        player = ApiVideoPlayer(context, INVALID_VIDEO_ID, VideoType.VOD, listener, playerView)
        lock.await(5, TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun singlePlayTest() {
        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                errorLock.countDown()
            }

            override fun onReady() {
                readyLock.countDown()
            }

            override fun onFirstPlay() {
                firstPlayLock.countDown()
            }

            override fun onPlay() {
                playLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayer(context, VALID_VIDEO_ID, VideoType.VOD, listener, playerView)

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(62, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(0, endLock.count)
    }


    @Test
    fun privateVideoSinglePlayTest() {
        assumeTrue("Required API key", apiKey != null)
        assumeTrue("API key not set", apiKey != "null")

        val videosApi = VideosApi(
            apiKey!!,
            Environment.PRODUCTION.basePath
        )
        val video = videosApi.get(PRIVATE_VIDEO_ID)
        val privateToken = video.assets!!.player.toString().split("=")[1]

        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                errorLock.countDown()
            }

            override fun onReady() {
                readyLock.countDown()
            }

            override fun onFirstPlay() {
                firstPlayLock.countDown()
            }

            override fun onPlay() {
                playLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayer(
            context,
            PRIVATE_VIDEO_ID,
            VideoType.VOD,
            listener,
            playerView,
            token = privateToken
        )

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(10, TimeUnit.SECONDS) // Video duration is ~6 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(0, endLock.count)
    }
}