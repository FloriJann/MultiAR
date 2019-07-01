package inovex.ad.multiar.walletModule.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import inovex.ad.multiar.GlideApp
import inovex.ad.multiar.R
import inovex.ad.multiar.walletModule.WalletEntry
import inovex.ad.multiar.walletModule.WalletViewModel

class EntryRecyclerAdapter(
    private val appContext: Context,
    private val walletViewModel: WalletViewModel,
    private val onEntryClickListener: OnEntryClickListener
) :
    RecyclerView.Adapter<RecyclerViewHolder>() {

    interface OnEntryClickListener {
        fun onEntryClick(holder: RecyclerViewHolder)
        fun onRemoveClick(walletEntry: WalletEntry)
    }

    var entries = listOf<WalletEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(
            appContext,
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item_wallet_entry, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentEntry = entries[position]

        GlideApp.with(appContext)
            .load(currentEntry.thumbnailUrl)
            .placeholder(R.drawable.placeholder_img)
            .into(holder.thumbnailIv)

        holder.bind(currentEntry, onEntryClickListener)

        holder.listPosition = position

        if (!walletViewModel.isArActive) {
            holder.removeButton.show()
        } else {
            holder.removeButton.hide()
            holder.setSelected(walletViewModel.currentEntry == position)
        }
    }

    fun setSelectedEntry(position: Int) {
        walletViewModel.currentEntry = position
        notifyDataSetChanged()
    }

    fun listChanged(list: List<WalletEntry>) {
        this.entries = list
        notifyDataSetChanged()
    }
}