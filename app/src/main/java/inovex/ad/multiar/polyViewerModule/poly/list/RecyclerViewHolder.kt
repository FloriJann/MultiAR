package inovex.ad.multiar.polyViewerModule.poly.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import inovex.ad.multiar.R
import inovex.ad.multiar.polyViewerModule.poly.AssetThumbnail

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val thumbnailIv = itemView.findViewById<ImageView>(R.id.thumbnail_imageView)!!

    val displayNameTv = itemView.findViewById<TextView>(R.id.displayName_textView)!!

    val authorNameTv = itemView.findViewById<TextView>(R.id.authorName)!!

    fun bind(thumbnail: AssetThumbnail, listener: AssetRecyclerAdapter.OnThumbnailClickListener) {
        itemView.setOnClickListener {
            listener.onThumbnailClick(thumbnail)
        }
    }
}