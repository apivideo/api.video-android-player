package video.api.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import video.api.player.models.ApiVideoMediaDescriptionAdapter


/**
 * The api.video player controller class.
 *
 * @param context the application context
 */
class ApiVideoPlayerNotificationController
constructor(
    private val context: Context,
    private val playerNotificationManager: PlayerNotificationManager,
    private val mediaSession: MediaSessionCompat = MediaSessionCompat(context, TAG),
) {
    constructor(
        context: Context,
        mediaSession: MediaSessionCompat = MediaSessionCompat(context, TAG),
        notificationId: Int = DEFAULT_NOTIFICATION_ID,
        channelId: String = DEFAULT_NOTIFICATION_CHANNEL_ID,
        @StringRes channelNameResourceId: Int = R.string.default_channel_name,
        @StringRes channelDescriptionResourceId: Int = 0,
        notificationStyle: NotificationStyle = NotificationStyle(),
    ) : this(
        context,
        PlayerNotificationManager.Builder(context, notificationId, channelId)
            .setChannelNameResourceId(channelNameResourceId)
            .setChannelDescriptionResourceId(channelDescriptionResourceId)
            .setSmallIconResourceId(notificationStyle.smallIconResourceId)
            .setMediaDescriptionAdapter(ApiVideoMediaDescriptionAdapter())
            .build().apply {
                setUseChronometer(true)
                if (notificationStyle.colorResourceId != 0) {
                    setColorized(true)
                    setColor(context.getColor(notificationStyle.colorResourceId))
                }
            },
        mediaSession
    )

    init {
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    var isActive: Boolean
        get() = mediaSession.isActive
        set(value) {
            mediaSession.isActive = value
        }

    fun hideNotification() {
        playerNotificationManager.setPlayer(null)
    }

    fun showNotification(player: ExoPlayer) {
        playerNotificationManager.setPlayer(player)
    }

    companion object {
        private const val TAG = "ApiVideoPlayerNotificationController"
        private const val DEFAULT_NOTIFICATION_ID = 3333
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "api.video.player.notification.channel"
    }
}

/**
 * The api.video player notification style.
 *
 * @param smallIconResourceId the small icon resource id
 * @param colorResourceId the color resource id (only use for Android API <= 30)
 * @param visibility the notification visibility
 */
data class NotificationStyle(
    @DrawableRes val smallIconResourceId: Int = R.drawable.ic_api_video,
    @ColorRes val colorResourceId: Int = R.color.primary_orange,
    val visibility: Int = NotificationCompat.VISIBILITY_PUBLIC,
)