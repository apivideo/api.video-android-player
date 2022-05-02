package video.api.player

import com.google.android.exoplayer2.analytics.AnalyticsListener

/**
 * Convert a Long in milliseconds to a Float in seconds.
 */
fun Long.toSeconds() = this.toFloat() / 1000

/**
 * An [EventTime] to a player analytics intelligible value
 */
fun AnalyticsListener.EventTime.toSeconds() = this.currentPlaybackPositionMs.toSeconds()