package video.api.player.utils

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import video.api.player.models.VideoOptions

/**
 * Convert a Long in milliseconds to a Float in seconds.
 */
fun Long.toSeconds() = this.toFloat() / 1000

/**
 * An [AnalyticsListener.EventTime] to a player analytics intelligible value
 */
fun AnalyticsListener.EventTime.toSeconds() = this.currentPlaybackPositionMs.toSeconds()

val ExoPlayer.currentVideoOptions: VideoOptions?
    get() {
        val currentMediaItem = this.currentMediaItem
        //val videoId = currentMediaItem.localConfiguration.tag as VideoOptions
        //val videoType = if (this.isCurrentMediaItemLive) VideoType.LIVE else VideoType.VOD
        return currentMediaItem?.localConfiguration?.tag as VideoOptions
    }