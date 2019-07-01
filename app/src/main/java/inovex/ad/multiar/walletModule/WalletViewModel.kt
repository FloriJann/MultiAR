package inovex.ad.multiar.walletModule

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import inovex.ad.multiar.R

class WalletViewModel(val app: Application) : AndroidViewModel(app) {

    private val TAG = javaClass.name

    private val entryList = mutableListOf<WalletEntry>()
    private val mutableEntries: MutableLiveData<List<WalletEntry>> = MutableLiveData()

    val entries: LiveData<List<WalletEntry>> = mutableEntries
    var currentEntry = 0
    var isArActive = false

    fun addWalletEntry(newEntry: WalletEntry) {
        if (!entryList.contains(newEntry)) {
            entryList.add(newEntry)
            mutableEntries.postValue(entryList)
            Log.d(TAG, "added $newEntry. to wallet")
            val toast = Toast.makeText(app, "Added to wallet!", Toast.LENGTH_LONG)
            toast.view.setBackgroundColor(ContextCompat.getColor(app, R.color.selectedWalletEntry))
            toast.setGravity(Gravity.TOP, 0, 300)
            toast.show()
        } else {
            Log.d(TAG, "Wallet already contains entry!")
        }
    }

    fun removeWalletEntry(entry: WalletEntry) {
        if (entryList.remove(entry)) {
            mutableEntries.postValue(entryList)
        }
    }
}