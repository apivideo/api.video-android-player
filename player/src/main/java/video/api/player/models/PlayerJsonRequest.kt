package video.api.player.models

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class PlayerJsonRequest(
    url: String,
    private val listener: Response.Listener<SessionTokenResult>,
    errorListener: Response.ErrorListener? = null
) : Request<SessionTokenResult>(Method.GET, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse): Response<SessionTokenResult> {
        val sessionTokenResult = SessionTokenResult(response.headers!!["X-Token-Session"]!!)
        return Response.success(
            sessionTokenResult,
            HttpHeaderParser.parseCacheHeaders(response)
        )
    }

    override fun deliverResponse(response: SessionTokenResult?) {
        listener.onResponse(response)
    }
}