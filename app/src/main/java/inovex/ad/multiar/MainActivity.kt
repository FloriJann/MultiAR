package inovex.ad.multiar

import android.app.Activity
import android.app.ActivityManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import inovex.ad.multiar.arViewerModule.ArOverlayFragment
import inovex.ad.multiar.arViewerModule.MyArFragment
import inovex.ad.multiar.polyViewerModule.AssetsFragment
import inovex.ad.multiar.walletModule.WalletFragment
import inovex.ad.multiar.walletModule.WalletViewModel
import kotlinx.android.synthetic.main.fragment_ar_overlay.*
import org.jetbrains.anko.contentView
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private val MIN_OPENGL_VERSION = 3.0

    private val walletFragment = WalletFragment()
    private val arFragment = MyArFragment()
    private val arOverlayFragment = ArOverlayFragment()

    private val walletViewModel: WalletViewModel  by viewModel()

    private lateinit var walletCountTv: TextView        // the textView in the custom menu icon displaying the current amount of items in the wallet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }

        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AssetsFragment())
            .commit()

        contentView?.setOnSystemUiVisibilityChangeListener {
            Log.d(TAG, "OnSystemUiVisibilityChanged to " + it.toString())
            //contentView?.systemUiVisibility = 0
            //openOptionsMenu()
        }
    }


    override fun onStart() {
        Log.d(TAG, "vor onStart = " + contentView?.systemUiVisibility)
        super.onStart()
        Log.d(TAG, "nach onStart = " + contentView?.systemUiVisibility)
    }

    override fun onResume() {
        Log.d(TAG, "vor onResume = " + contentView?.systemUiVisibility)
        super.onResume()
        Log.d(TAG, "nach onResume = " + contentView?.systemUiVisibility)
        //contentView?.systemUiVisibility = 0

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)

        val customMenuItem = menu.findItem(R.id.wallet_item)
        val rootView = customMenuItem.actionView

        rootView.setOnClickListener {
            onOptionsItemSelected(customMenuItem)
        }

        walletCountTv = rootView.findViewById(R.id.wallet_count_tv)

        setWalletCount(0)
        walletViewModel.entries.observe(this, Observer {
            if (it != null) {
                setWalletCount(it.size)
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.wallet_item -> {
                resumeWalletFragment()
            }
            R.id.arScene_item -> {
                resumeArFragment()
            }
        }
        return true
    }

    private fun setWalletCount(int: Int) {
        walletCountTv.text = int.toString()
    }

    private fun resumeWalletFragment() {
        if (!walletFragment.isResumed) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, walletFragment)
                .addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .remove(walletFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun resumeArFragment() {
        if (!arFragment.isResumed) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, arFragment)
                .addToBackStack(null)
                .commit()


            if (!arOverlayFragment.isResumed) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, arOverlayFragment)
                    .commit()

                setArOverlayVisible(false)
            }

            walletViewModel.currentEntry = 0        // make sure the top-left Item  in the wallet is selected
        }
    }

    fun removeArOverlayFragment() {
        supportFragmentManager
            .beginTransaction()
            .remove(arOverlayFragment)
            .commit()

    }

    fun arOverlayDeleteClicked() {
        if (arFragment.isResumed) {
            arFragment.deleteSelectedNode()
        }
    }

    fun arOverlayOffsetChanged(progress: Int) {
        if (arFragment.isResumed) {
            arFragment.offsetChanged(progress)
        }
    }


    fun arOverlaySwitchClicked(value: Boolean) {
        if (arFragment.isResumed) {
            arFragment.lookSwitchSet(value)
        }
    }

    fun setArOverlayVisible(show: Boolean) {
        if (show) {
            if (!supportFragmentManager.fragments.contains(arOverlayFragment)) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, arOverlayFragment)
                    .commit()

            } else {
                supportFragmentManager
                    .beginTransaction()
                    .show(arOverlayFragment)
                    .commit()
            }

            arOverlayFragment.offsetSeekBar.progress = 0

        } else {
            supportFragmentManager
                .beginTransaction()
                .hide(arOverlayFragment)
                .commit()
        }
    }


    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * Finishes the activity if Sceneform can not run
     */
    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }
}
