package video.api.player

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.NoCache
import com.android.volley.toolbox.Volley
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

class ApiVideoPlayerTest {
    private val context = mockk<Context>(relaxed = true)
    private val mockHttpStack = MockHttpStack()
    private val playerView = mockk<StyledPlayerView>(relaxed = true)
    private val exoplayer = spyk<ExoPlayer>()
    private val mediaSource = mockk<MediaSource>()

    @Before
    fun setUp() {
        // Mock RequestQueue
        val queue =
            RequestQueue(NoCache(), BasicNetwork(mockHttpStack), 2, ImmediateResponseDelivery())

        mockkStatic(Volley::class)
        every { Volley.newRequestQueue(any()) } returns queue

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

        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                error.printStackTrace()
                println(error.message)
                lock.countDown()
            }
        }

        ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(1, lock.count) // OnError not called
    }

    @Test
    fun `get an invalid player json`() {
        mockHttpStack.setResponseToReturn(
            HttpResponse(
                200,
                emptyList(),
                byteArrayOf(7, 34, 12)
            )
        )

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                lock.countDown()
            }
        }

        ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun `get an exception on player json connection`() {
        mockHttpStack.setExceptionToThrow(IOException())

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayer.Listener {
            override fun onError(error: Exception) {
                lock.countDown()
            }
        }

        ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun `play test`() {
        val listener = object : ApiVideoPlayer.Listener {
        }

        val player =
            ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        player.play()
        verify { exoplayer.play() }
    }

    @Test
    fun `pause test`() {
        val listener = object : ApiVideoPlayer.Listener {
        }

        val player =
            ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        player.pause()
        verify { exoplayer.pause() }
    }

    @Test
    fun `stop test`() {
        val listener = object : ApiVideoPlayer.Listener {
        }

        val player =
            ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        player.stop()
        verify { exoplayer.stop() }
    }

    @Test
    fun `release test`() {
        val listener = object : ApiVideoPlayer.Listener {
        }

        val player =
            ApiVideoPlayer(context, VideoOptions("test", VideoType.VOD), listener, playerView)
        player.release()
        verify { exoplayer.release() }
    }
}