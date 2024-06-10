package video.api.player.notifications

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import video.api.player.R
import video.api.player.models.ApiVideoMediaDescriptionAdapter


/**
 * The api.video player notification controller class.
 *
 * The `ApiVideoPlayerController` uses the `ApiVideoPlayerNotificationController` to display a
 * notification when the player is launched.
 *
 * @param context the application context
 * @param playerNotificationManager the [PlayerNotificationManager] to use
 * @param mediaSession the [MediaSessionCompat] to use
 */
class ApiVideoPlayerNotificationController
@OptIn(UnstableApi::class)
constructor(
    private val context: Context,
    private val playerNotificationManager: PlayerNotificationManager,
    private val mediaSession: MediaSessionCompat = MediaSessionCompat(context, TAG),
) {
    @OptIn(UnstableApi::class)
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
                    setColor(ContextCompat.getColor(context, notificationStyle.colorResourceId))
                }
            },
        mediaSession
    )

    init {
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    /**
     * Whether the notification is active.
     */
    var isActive: Boolean
        get() = mediaSession.isActive
        set(value) {
            mediaSession.isActive = value
        }

    @OptIn(UnstableApi::class)
    fun hideNotification() {
        playerNotificationManager.setPlayer(null)
    }

    @OptIn(UnstableApi::class)
    fun showNotification(player: ExoPlayer) {
        playerNotificationManager.setPlayer(player)
    }

    fun release() {
        mediaSession.release()
    }

    companion object {
        private const val TAG = "PlayerNotificationController"
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