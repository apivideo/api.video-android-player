package video.api.player.extensions

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import video.api.player.models.ApiVideoMediaFactory
import video.api.player.models.VideoOptions

private const val TAG = "MediaPlayerExtensions"

/**
 * Sets a media source to [MediaPlayer] to read from api.video HLS.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun MediaPlayer.setDataSource(
    context: Context,
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setDataSource(context, ApiVideoMediaFactory(videoOptions, onError))
}

/**
 * Sets a media source to [MediaPlayer] to read from api.video HLS.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param videoUrlFactory The [ApiVideoMediaFactory] to play
 */
fun MediaPlayer.setDataSource(
    context: Context,
    videoUrlFactory: ApiVideoMediaFactory,
) {
    videoUrlFactory.getVideoUrl {
        val uri = Uri.parse(it.uri)
        setDataSource(context, uri, it.headers)
    }
}


/**
 * Sets a media source to [MediaPlayer] to read from api.video MP4 URL.
 *
 * @param videoOptions The [VideoOptions] to play
 */
fun MediaPlayer.setMp4DataSource(
    context: Context,
    videoOptions: VideoOptions,
    onError: (Exception) -> Unit = { error ->
        Log.e(
            TAG,
            "Failed to create session $error"
        )
    }
) {
    setMp4DataSource(context, ApiVideoMediaFactory(videoOptions, onError))
}

/**
 * Sets a media source to [MediaPlayer] to read from api.video MP4 URL.
 * Use this method if you want to keep the session token for later usage.
 *
 * @param videoUrlFactory The [ApiVideoMediaFactory] to play
 */
fun MediaPlayer.setMp4DataSource(
    context: Context,
    videoUrlFactory: ApiVideoMediaFactory,
) {
    videoUrlFactory.getMp4VideoUrl {
        val uri = Uri.parse(it.uri)
        setDataSource(context, uri, it.headers)
    }
}
