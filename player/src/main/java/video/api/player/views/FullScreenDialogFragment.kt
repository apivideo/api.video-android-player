package video.api.player.views

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import video.api.player.R
import video.api.player.databinding.FullscreenLayoutBinding
import video.api.player.extensions.hideSystemUI

/**
 * A full screen dialog fragment that contains a [subView].
 *
 * @param subView The view to be displayed in the dialog.
 */
class FullScreenDialogFragment(private val subView: View) :
    DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()

        activity?.window?.hideSystemUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FullscreenLayoutBinding.inflate(layoutInflater, container, false)

        if (subView.parent != null) {
            (subView.parent as ViewGroup).removeView(subView)
        }
        subView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.CENTER
        }
        binding.container.addView(subView)

        return binding.root
    }
}