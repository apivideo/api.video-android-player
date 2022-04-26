package video.api.player

import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.NoCache
import com.android.volley.toolbox.Volley
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import video.api.player.mock.ImmediateResponseDelivery
import video.api.player.mock.MockHttpStack
import video.api.player.models.VideoType
import video.api.player.utils.Resources
import java.io.IOException
import java.util.concurrent.CountDownLatch

class ApiVideoPlayerTest {
    private val mockHttpStack = MockHttpStack()

    @Before
    fun setUp() {
        // Mock RequestQueue
        val queue =
            RequestQueue(NoCache(), BasicNetwork(mockHttpStack), 2, ImmediateResponseDelivery())

        mockkStatic(Volley::class)
        every { Volley.newRequestQueue(any()) } returns queue
    }

    @Test
    fun `get a valid player json`() {
        mockHttpStack.setResponseToReturn(
            HttpResponse(
                200,
                emptyList(),
                Resources.readFile("/assets/player.json").toByteArray()
            )
        )

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerListener {
            override fun onError(error: String) {
                lock.countDown()
            }
        }

        ApiVideoPlayer(mockk(relaxed = true), "test", VideoType.LIVE, listener)
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

        val listener = object : ApiVideoPlayerListener {
            override fun onError(error: String) {
                lock.countDown()
            }
        }

        ApiVideoPlayer(mockk(relaxed = true), "test", VideoType.LIVE, listener)
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }

    @Test
    fun `get an exception on player json connection`() {
        mockHttpStack.setExceptionToThrow(IOException())

        val lock = CountDownLatch(1)

        val listener = object : ApiVideoPlayerListener {
            override fun onError(error: String) {
                lock.countDown()
            }
        }

        ApiVideoPlayer(mockk(relaxed = true), "test", VideoType.LIVE, listener)
        lock.await(1, java.util.concurrent.TimeUnit.SECONDS)

        assertEquals(0, lock.count)
    }
}