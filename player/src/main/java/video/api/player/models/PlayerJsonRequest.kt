package video.api.player.models

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException

class PlayerJsonRequest(
    url: String,
    private val listener: Response.Listener<PlayerJsonRequestResult>,
    errorListener: Response.ErrorListener? = null
) : Request<PlayerJsonRequestResult>(Method.GET, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse): Response<PlayerJsonRequestResult> {
        val parsed = try {
            String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))
        } catch (e: UnsupportedEncodingException) {
            String(response.data)
        }

        val playerJsonRequestResult = PlayerJsonRequestResult(parsed, response.headers)
        return Response.success(
            playerJsonRequestResult,
            HttpHeaderParser.parseCacheHeaders(response)
        )
    }

    override fun deliverResponse(response: PlayerJsonRequestResult?) {
        listener.onResponse(response)
    }
}