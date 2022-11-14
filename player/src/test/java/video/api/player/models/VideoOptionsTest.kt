package video.api.player.models

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoOptionsTest {
    @Test
    fun `test vod url`() {
        val videoOptions = VideoOptions("videoId", VideoType.VOD)
        assertEquals(
            "https://vod.api.video/vod/videoId/hls/manifest.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test private vod url`() {
        val videoOptions = VideoOptions("videoId", VideoType.VOD, "token")
        assertEquals(
            "https://vod.api.video/vod/videoId/token/token/hls/manifest.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test mp4 url`() {
        val videoOptions = VideoOptions("videoId", VideoType.VOD)
        assertEquals("https://vod.api.video/vod/videoId/mp4/source.mp4", videoOptions.mp4Url)
    }

    @Test
    fun `test private mp4 url`() {
        val videoOptions = VideoOptions("videoId", VideoType.VOD, "token")
        assertEquals(
            "https://vod.api.video/vod/videoId/token/token/mp4/source.mp4",
            videoOptions.mp4Url
        )
    }

    @Test
    fun `test live url`() {
        val videoOptions = VideoOptions("videoId", VideoType.LIVE)
        assertEquals(
            "https://live.api.video/videoId.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test private live url`() {
        val videoOptions = VideoOptions("videoId", VideoType.LIVE, "token")
        assertEquals(
            "https://live.api.video/private/token/videoId.m3u8",
            videoOptions.hlsManifestUrl
        )
    }
}
