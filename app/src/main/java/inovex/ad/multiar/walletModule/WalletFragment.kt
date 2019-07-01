package inovex.ad.multiar.walletModule


import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import inovex.ad.multiar.R
import inovex.ad.multiar.walletModule.list.EntryRecyclerAdapter
import inovex.ad.multiar.walletModule.list.RecyclerViewHolder
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 *
 */
class WalletFragment : Fragment(), EntryRecyclerAdapter.OnEntryClickListener {

    private val TAG = javaClass.name

    private val walletViewModel: WalletViewModel  by sharedViewModel()

    private lateinit var viewAdapter: EntryRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupAdapter()

        walletViewModel.entries.observe(this, Observer {
            if (it != null) {
                this.viewAdapter.listChanged(it)
            }
        })
    }

    private fun setupAdapter() {
        val numberOfColumns = 2
        val gridLayoutManager = GridLayoutManager(activity, numberOfColumns)

        viewAdapter = EntryRecyclerAdapter(
            onEntryClickListener = this,
            walletViewModel = walletViewModel,
            appContext = activity?.applicationContext as Context
        )

        walletRecyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = viewAdapter
        }
    }

    override fun onEntryClick(holder: RecyclerViewHolder) {
        if (walletViewModel.isArActive) {
            walletViewModel.currentEntry = holder.listPosition
            viewAdapter.setSelectedEntry(holder.listPosition)
        }
    }

    override fun onRemoveClick(walletEntry: WalletEntry) {
        walletViewModel.removeWalletEntry(walletEntry)
    }
}
