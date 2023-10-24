package video.api.player.models

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

/**
 * Request to get a session token for a private video.
 */
internal class SessionTokenRequest(
    url: String,
    listener: Response.Listener<SessionTokenResult>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(url, Response.Listener { response ->
    listener.onResponse(SessionTokenResult(response.getString("session_token")))
}, errorListener)