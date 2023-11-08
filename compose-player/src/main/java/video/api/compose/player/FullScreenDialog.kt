package video.api.compose.player

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import video.api.compose.player.extensions.findActivity
import video.api.player.extensions.hideSystemUI
import video.api.player.models.VideoOptions
import video.api.player.models.VideoType

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun FullScreenDialog(
    onDismissRequest: () -> Unit = {},
    securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            securePolicy = securePolicy,
            decorFitsSystemWindows = false,
        ),
    ) {
        SideEffect {
            val currentActivity = context.findActivity()
            currentActivity!!.window.hideSystemUI()
        }

        content()
    }
}

@Preview
@Composable
private fun FullScreenDialogPreview() {
    MaterialTheme {
        FullScreenDialog {
            ApiVideoPlayer(
                VideoOptions("vi77Dgk0F8eLwaFOtC5870yn", VideoType.VOD),
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}