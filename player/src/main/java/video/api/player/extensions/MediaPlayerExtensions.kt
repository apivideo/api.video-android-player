package video.api.player.extensions

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import video.api.player.models.ApiVideoUrlFactory

/**
 * Sets a media source to [MediaPlayer] to read from api.video HLS.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to use
 */
fun MediaPlayer.setDataSource(
    context: Context,
    videoUrlFactory: ApiVideoUrlFactory,
) {
    videoUrlFactory.createVideoUrl {
        val uri = Uri.parse(it.uri)
        setDataSource(context, uri, it.headers)
    }
}

/**
 * Sets a media source to [MediaPlayer] to read from api.video MP4 URL.
 *
 * @param videoUrlFactory The [ApiVideoUrlFactory] to use
 */
fun MediaPlayer.setMp4DataSource(
    context: Context,
    videoUrlFactory: ApiVideoUrlFactory,
) {
    videoUrlFactory.createMp4VideoUrl {
        val uri = Uri.parse(it.uri)
        setDataSource(context, uri, it.headers)
    }
}
