package video.api.player.models

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoOptionsTest {
    @Test
    fun `test vod url`() {
        val videoOptions = VideoOptions("vi5oNqxkifcXkT4auGNsvgZB", VideoType.VOD)
        assertEquals(
            "https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/hls/manifest.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test private vod url`() {
        val videoOptions = VideoOptions("vi5oNqxkifcXkT4auGNsvgZB", VideoType.VOD, "token")
        assertEquals(
            "https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/token/token/hls/manifest.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test mp4 url`() {
        val videoOptions = VideoOptions("vi5oNqxkifcXkT4auGNsvgZB", VideoType.VOD)
        assertEquals(
            "https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/mp4/source.mp4",
            videoOptions.mp4Url
        )
    }

    @Test
    fun `test private mp4 url`() {
        val videoOptions = VideoOptions("vi5oNqxkifcXkT4auGNsvgZB", VideoType.VOD, "token")
        assertEquals(
            "https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/token/token/mp4/source.mp4",
            videoOptions.mp4Url
        )
    }

    @Test
    fun `test live url`() {
        val videoOptions = VideoOptions("li77ACbZjzEJgmr8d0tm4xFt", VideoType.LIVE)
        assertEquals(
            "https://live.api.video/li77ACbZjzEJgmr8d0tm4xFt.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test private live url`() {
        val videoOptions = VideoOptions("li77ACbZjzEJgmr8d0tm4xFt", VideoType.LIVE, "token")
        assertEquals(
            "https://live.api.video/private/token/li77ACbZjzEJgmr8d0tm4xFt.m3u8",
            videoOptions.hlsManifestUrl
        )
    }

    @Test
    fun `test parse vod url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/hls/manifest.m3u8")
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, null)
    }

    @Test
    fun `test parse private vod url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/token/PRIVATE_TOKEN/hls/manifest.m3u8")
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, "PRIVATE_TOKEN")
    }

    @Test
    fun `test parse MP4 vod url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/mp4/source.mp4")
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, null)
    }

    @Test
    fun `test parse private MP4 vod url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://vod.api.video/vod/vi5oNqxkifcXkT4auGNsvgZB/token/PRIVATE_TOKEN/mp4/source.mp4")
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, "PRIVATE_TOKEN")
    }

    @Test
    fun `test parse live url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://live.api.video/li77ACbZjzEJgmr8d0tm4xFt.m3u8")
        assertEquals(videoOptions.videoId, "li77ACbZjzEJgmr8d0tm4xFt")
        assertEquals(videoOptions.videoType, VideoType.LIVE)
        assertEquals(videoOptions.token, null)
    }

    @Test
    fun `test parse private live url`() {
        val videoOptions =
            VideoOptions.fromUrl("https://live.api.video/private/PRIVATE_TOKEN/li77ACbZjzEJgmr8d0tm4xFt.m3u8")
        assertEquals(videoOptions.videoId, "li77ACbZjzEJgmr8d0tm4xFt")
        assertEquals(videoOptions.videoType, VideoType.LIVE)
        assertEquals(videoOptions.token, "PRIVATE_TOKEN")
    }

    @Test
    fun `test parse vod url with custom domain`() {
        val videoOptions =
            VideoOptions.fromUrl(
                "https://mycustom.vod.domain/vod/vi5oNqxkifcXkT4auGNsvgZB/hls/manifest.m3u8",
                "https://mycustom.vod.domain",
                "https://mycustom.live.domain"
            )
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, null)
    }

    @Test
    fun `test parse private vod url with custom domain`() {
        val videoOptions =
            VideoOptions.fromUrl(
                "https://mycustom.vod.domain/vod/vi5oNqxkifcXkT4auGNsvgZB/token/PRIVATE_TOKEN/hls/manifest.m3u8",
                "https://mycustom.vod.domain",
                "https://mycustom.live.domain"
            )
        assertEquals(videoOptions.videoId, "vi5oNqxkifcXkT4auGNsvgZB")
        assertEquals(videoOptions.videoType, VideoType.VOD)
        assertEquals(videoOptions.token, "PRIVATE_TOKEN")
    }

    @Test
    fun `test parse live url with custom domain`() {
        val videoOptions = VideoOptions.fromUrl(
            "https://mycustom.live.domain/li77ACbZjzEJgmr8d0tm4xFt.m3u8",
            "https://mycustom.vod.domain",
            "https://mycustom.live.domain"
        )
        assertEquals(videoOptions.videoId, "li77ACbZjzEJgmr8d0tm4xFt")
        assertEquals(videoOptions.videoType, VideoType.LIVE)
        assertEquals(videoOptions.token, null)
    }

    @Test
    fun `test parse private live url with custom domain`() {
        val videoOptions =
            VideoOptions.fromUrl(
                "https://mycustom.live.domain/private/PRIVATE_TOKEN/li77ACbZjzEJgmr8d0tm4xFt.m3u8",
                "https://mycustom.vod.domain",
                "https://mycustom.live.domain"
            )
        assertEquals(videoOptions.videoId, "li77ACbZjzEJgmr8d0tm4xFt")
        assertEquals(videoOptions.videoType, VideoType.LIVE)
        assertEquals(videoOptions.token, "PRIVATE_TOKEN")
    }
}
