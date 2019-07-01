package inovex.ad.multiar.walletModule.list

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import inovex.ad.multiar.R
import inovex.ad.multiar.walletModule.WalletEntry

class RecyclerViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

    val thumbnailIv = itemView.findViewById<ImageView>(R.id.thumbnail_wallet_imageView)!!

    val removeButton = itemView.findViewById<FloatingActionButton>(R.id.floatingActionButton)!!

    var listPosition = 0


    fun bind(walletEntry: WalletEntry, listener: EntryRecyclerAdapter.OnEntryClickListener) {
        itemView.setOnClickListener {
            listener.onEntryClick(this)
        }

        removeButton.setOnClickListener {
            listener.onRemoveClick(walletEntry)
        }
    }

    fun setSelected(selected: Boolean) {
        if(selected) {
            thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedWalletEntry))
        } else {
            thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
    }

}