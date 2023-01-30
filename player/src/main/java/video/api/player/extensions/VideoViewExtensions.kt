package video.api.player.extensions

import android.net.Uri
import android.util.Log
import android.widget.VideoView
import video.api.player.models.ApiVideoUrlFactory
import video.api.player.models.VideoOptions

private const val TAG = "VideoViewExtensions"

/**
 * Sets a media source to [VideoView] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun VideoView.setVideo(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setVideo(ApiVideoUrlFactory(videoOptions, onError))
}

/**
 * Sets a media source to [VideoView] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to play
 */
fun VideoView.setVideo(
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
 * @param videoOptions The [VideoOptions] to play
 */
fun VideoView.setMp4Video(
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMp4Video(ApiVideoUrlFactory(videoOptions, onError))
}

/**
 * Sets a media source to [VideoView] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to play
 */
fun VideoView.setMp4Video(
    videoUrlFactory: ApiVideoUrlFactory,
) {
    videoUrlFactory.createMp4VideoUrl {
        val uri = Uri.parse(it.uri)
        setVideoURI(uri, it.headers)
    }
}
