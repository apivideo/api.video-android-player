package video.api.player.extensions

import android.net.Uri
import android.widget.VideoView
import video.api.player.models.ApiVideoUrlFactory

/**
 * Sets a media source to [VideoView] to read from api.video HLS.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to use
 */
fun VideoView.setVideoOptions(
    videoUrlFactory: ApiVideoUrlFactory,
) {
    videoUrlFactory.createVideoUrl {
        val uri = Uri.parse(it.uri)
        setVideoURI(uri, it.headers)
    }
}

/**
 * Sets a media source to [VideoView] to read from api.video MP4 URL.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to use
 */
fun VideoView.setMp4VideoOptions(
    videoUrlFactory: ApiVideoUrlFactory,
) {
    videoUrlFactory.createMp4VideoUrl {
        val uri = Uri.parse(it.uri)
        setVideoURI(uri, it.headers)
    }
}
