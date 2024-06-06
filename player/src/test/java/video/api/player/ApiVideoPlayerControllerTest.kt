package video.api.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.NoCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
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
    private val playerView = mockk<PlayerView>(relaxed = true)
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
            RequestQueue(
                NoCache(),
                BasicNetwork(mockHttpStack),
                2,
                ImmediateResponseDelivery()
            ).apply {
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
    fun `play test`() {
        val player =
            ApiVideoPlayerController(
                context,
                VideoOptions("test", VideoType.VOD),
                playerView = playerView,
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