package video.api.player.utils

import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import video.api.player.models.PlayerJsonRequest
import video.api.player.models.SessionTokenRequest
import video.api.player.models.VideoType


object SessionTokenUtils {
    private val queue = RequestQueue(NoCache(), BasicNetwork(HurlStack())).apply { start() }

    fun getSessionToken(
        url: String,
        videoType: VideoType,
        onSuccess: (String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val sessionTokenRequest = if (videoType == VideoType.VOD) {
            SessionTokenRequest(url,
                { sessionTokenResult ->
                    onSuccess(sessionTokenResult.sessionToken)
                },
                { error ->
                    onError(error)
                }
            )
        } else {
            PlayerJsonRequest(url,
                { sessionTokenResult ->
                    onSuccess(sessionTokenResult.sessionToken)
                },
                { error ->
                    onError(error)
                }
            )
        }

        queue.add(sessionTokenRequest)
    }
}