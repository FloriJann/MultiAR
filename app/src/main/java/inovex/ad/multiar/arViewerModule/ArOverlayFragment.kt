package inovex.ad.multiar.arViewerModule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import inovex.ad.multiar.MainActivity
import inovex.ad.multiar.R
import kotlinx.android.synthetic.main.fragment_ar_overlay.*

class ArOverlayFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onStart() {
        super.onStart()

        mainActivity = activity as MainActivity

        deleteButton.setOnClickListener {
            deleteClicked()
        }


        offsetSeekBar.max = 200
        offsetSeekBar.min = 0
        offsetSeekBar.progress = 0
        offsetSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mainActivity.arOverlayOffsetChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })

        camLookSwitch.setOnClickListener {
            mainActivity.arOverlaySwitchClicked(camLookSwitch.isChecked)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar_overlay, container, false)
    }

    private fun deleteClicked() {
        mainActivity.arOverlayDeleteClicked()
    }
}