package video.api.player

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import video.api.client.ApiVideoClient
import video.api.client.api.models.Environment
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class ApiVideoPlayerControllerAndroidTest {
    companion object {
        private const val VALID_VIDEO_ID = "vi2G6Qr8ZVE67dWLNymk7qbc"
        private const val INVALID_VIDEO_ID = "unknownVideoId"
        private const val PRIVATE_VIDEO_ID = "viMgTZ1KULkXrjFfDCTBtLs"

        private const val TAG = "ApiVideoPlayerControllerAndroidTest"
    }

    private lateinit var player: ApiVideoPlayerController
    private var apiKey: String? = null
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setUp() {
        val arguments = InstrumentationRegistry.getArguments()
        apiKey = arguments.getString("INTEGRATION_TESTS_API_KEY")
    }

    @Test
    fun assertDurationTest() {
        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerController.Listener {
            override fun onReady() {
                lock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            VideoOptions(VALID_VIDEO_ID, VideoType.VOD),
            listener = listener
        )
        lock.await(5, TimeUnit.SECONDS)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            assertEquals(60.2F, player.duration)
        }
    }


    @Test
    fun unknownVideoId() {
        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
                lock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            VideoOptions(INVALID_VIDEO_ID, VideoType.VOD),
            listener = listener
        )
        lock.await(5, TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun singlePlayTest() {
        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(1)
        val pauseLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
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

            override fun onPause() {
                pauseLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            VideoOptions(VALID_VIDEO_ID, VideoType.VOD),
            listener = listener
        )

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(70, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(1, pauseLock.count)
        assertEquals(0, endLock.count)
    }

    @Test
    fun singlePlayWithVideoOptionTest() {
        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(1)
        val pauseLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
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

            override fun onPause() {
                pauseLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            listener = listener
        )

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.videoOptions = VideoOptions(VALID_VIDEO_ID, VideoType.VOD)
        }

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(70, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(1, pauseLock.count)
        assertEquals(0, endLock.count)
    }

    @Test
    fun multiplePlayWithVideoOptionTest() {
        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(2)
        val firstPlayLock = CountDownLatch(2)
        val playLock = CountDownLatch(2)
        val pauseLock = CountDownLatch(1)
        val endLock = CountDownLatch(2)
        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
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

            override fun onPause() {
                pauseLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            listener = listener
        )

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.videoOptions = VideoOptions(VALID_VIDEO_ID, VideoType.VOD)
        }

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(1, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(70, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.videoOptions = VideoOptions(VALID_VIDEO_ID, VideoType.VOD)
        }

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(70, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(1, pauseLock.count)
        assertEquals(0, endLock.count)
    }

    @Test
    fun pausePlayTest() {
        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(2)
        val pauseLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
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

            override fun onPause() {
                pauseLock.countDown()
            }

            override fun onEnd() {
                endLock.countDown()
            }
        }
        player = ApiVideoPlayerController(
            context,
            VideoOptions(VALID_VIDEO_ID, VideoType.VOD),
            listener = listener
        )

        readyLock.await(5, TimeUnit.SECONDS)
        assertEquals(0, readyLock.count)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }
        sleep(1000)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.pause()
        }
        sleep(1000)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            player.play()
        }

        endLock.await(70, TimeUnit.SECONDS) // Video duration is 60.2 seconds

        assertEquals(1, errorLock.count) // No error has happened
        assertEquals(0, playLock.count)
        assertEquals(0, firstPlayLock.count)
        assertEquals(0, pauseLock.count)
        assertEquals(0, endLock.count)
    }

    @Test
    fun privateVideoSinglePlayTest() {
        assumeTrue("Required API key", apiKey != null)
        assumeTrue("API key not set", apiKey != "null")

        val apiClient = ApiVideoClient(apiKey!!, Environment.PRODUCTION)
        apiClient.setApplicationName("player-integration-tests", "0")
        val videosApi = apiClient.videos()

        val video = videosApi.get(PRIVATE_VIDEO_ID)
        val privateToken = video.assets!!.player.toString().split("=")[1]

        val errorLock = CountDownLatch(1)
        val readyLock = CountDownLatch(1)
        val firstPlayLock = CountDownLatch(1)
        val playLock = CountDownLatch(1)
        val endLock = CountDownLatch(1)
        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                Log.e(TAG, "An error occurred", error)
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
        player = ApiVideoPlayerController(
            context,
            VideoOptions(PRIVATE_VIDEO_ID, VideoType.VOD, privateToken),
            listener = listener
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