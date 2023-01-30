package video.api.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.NoCache
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import video.api.player.mock.ImmediateResponseDelivery
import video.api.player.mock.MockHttpStack
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType
import video.api.player.utils.Resources
import java.io.IOException
import java.util.concurrent.CountDownLatch

class ApiVideoPlayerControllerTest {
    private val context = mockk<Context>(relaxed = true)
    private val mockHttpStack = MockHttpStack()
    private val playerView = mockk<StyledPlayerView>(relaxed = true)
    private val exoplayer = spyk<ExoPlayer>()
    private val mediaSource = mockk<MediaSource>()
    private val looper = mockk<Looper> {
        every { thread } returns Thread.currentThread()
    }

    @Before
    fun setUp() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns looper
        mockkConstructor(Handler::class)
        every { anyConstructed<Handler>().post(any()) } answers {
            (arg(0) as Runnable).run()
            true
        }

        // Mock RequestQueue
        val queue =
            RequestQueue(NoCache(), BasicNetwork(mockHttpStack), 2, ImmediateResponseDelivery()).apply {
                start()
            }
        mockkConstructor(RequestQueue::class)
        every { anyConstructed<RequestQueue>().add(any<Request<Any>>()) } answers {
            queue.add(
                firstArg()
            )
        }

        mockkConstructor(ExoPlayer.Builder::class)
        every { anyConstructed<ExoPlayer.Builder>().build() } returns exoplayer

        mockkConstructor(DefaultMediaSourceFactory::class)
        every { anyConstructed<ExoPlayer.Builder>().build() } returns exoplayer

        mockkClass(DefaultMediaSourceFactory::class)
        every { anyConstructed<DefaultMediaSourceFactory>().createMediaSource(any()) } returns mediaSource
    }

    @Test
    fun `get a valid player json`() {
        mockHttpStack.setResponseToReturn(
            HttpResponse(
                200,
                emptyList(),
                Resources.readFile("/assets/valid_player.json").toByteArray()
            )
        )

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                error.printStackTrace()
                println(error.message)
                lock.countDown()
            }
        }

        ApiVideoPlayerController(
            context,
            VideoOptions("test", VideoType.VOD),
            false,
            listener,
            playerView,
            notificationController = null
        )
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(1, lock.count) // OnError not called
    }

    @Test
    fun `get an exception on player json connection`() {
        mockHttpStack.setExceptionToThrow(IOException())

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerController.Listener {
            override fun onError(error: Exception) {
                lock.countDown()
            }
        }

        ApiVideoPlayerController(
            context,
            VideoOptions(
                "test",
                VideoType.VOD,
                "token"
            ), // The endpoint is only called when a token is provided
            false,
            listener,
            playerView,
            notificationController = null
        )
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun `play test`() {
        val player =
            ApiVideoPlayerController(
                context,
                VideoOptions("test", VideoType.VOD),
                styledPlayerView = playerView,
                notificationController = null
            )
        player.play()
        verify { exoplayer.playWhenReady = true }
    }

    @Test
    fun `pause test`() {
        val listener = object : ApiVideoPlayerController.Listener {
        }

        val player =
            ApiVideoPlayerController(
                context,
                VideoOptions("test", VideoType.VOD),
                false,
                listener,
                playerView,
                notificationController = null
            )
        player.pause()
        verify { exoplayer.pause() }
    }

    @Test
    fun `stop test`() {
        val listener = object : ApiVideoPlayerController.Listener {
        }

        val player =
            ApiVideoPlayerController(
                context,
                VideoOptions("test", VideoType.VOD),
                false,
                listener,
                playerView,
                notificationController = null
            )
        player.stop()
        verify { exoplayer.stop() }
    }

    @Test
    fun `release test`() {
        val listener = object : ApiVideoPlayerController.Listener {
        }

        val player =
            ApiVideoPlayerController(
                context,
                VideoOptions("test", VideoType.VOD),
                false,
                listener,
                playerView,
                looper,
                notificationController = null
            )
        player.release()
        verify { exoplayer.release() }
    }
}