package video.api.player.models

import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import video.api.player.utils.RequestManager

/**
 * Creates a default [PlayerNotificationManager.MediaDescriptionAdapter] to populate the notification from api.video.
 */
class ApiVideoMediaDescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
    @OptIn(UnstableApi::class)
    override fun getCurrentContentTitle(player: Player): CharSequence {
        val displayTitle = player.mediaMetadata.displayTitle
        if (!TextUtils.isEmpty(displayTitle)) {
            return displayTitle!!
        }
        val title = player.mediaMetadata.title
        return title ?: ""
    }

    @OptIn(UnstableApi::class)
    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return null
    }

    @OptIn(UnstableApi::class)
    override fun getCurrentContentText(player: Player): CharSequence {
        val artist = player.mediaMetadata.artist
        val albumArtist = player.mediaMetadata.albumArtist
        return if (!TextUtils.isEmpty(artist)) {
            artist!!
        } else if (!TextUtils.isEmpty(albumArtist)) {
            albumArtist!!
        } else {
            ""
        }
    }

    @OptIn(UnstableApi::class)
    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
        val data = player.mediaMetadata.artworkData
        if (data != null) {
            return BitmapFactory.decodeByteArray(data,  /* offset = */0, data.size)
        }

        val uri = player.mediaMetadata.artworkUri
        if (uri != null) {
            val maxWidth = 0
            val maxHeight = 0
            val scaleType = ImageView.ScaleType.CENTER_CROP
            RequestManager.getImage(uri.toString(), maxWidth, maxHeight, scaleType, { bitmap ->
                callback.onBitmap(bitmap)
            }, { error ->
                Log.e(TAG, "Failed to load thumbnail", error)
            })
        }

        return null
    }

    companion object {
        private const val TAG = "ApiVideoMediaDescriptionAdapter"
    }
}