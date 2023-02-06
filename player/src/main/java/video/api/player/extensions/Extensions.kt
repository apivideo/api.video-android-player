package video.api.player.extensions

fun String.appendTokenSession(tokenSession: String?): String {
    return this + (tokenSession?.let { "?avh=$it" } ?: "")
}