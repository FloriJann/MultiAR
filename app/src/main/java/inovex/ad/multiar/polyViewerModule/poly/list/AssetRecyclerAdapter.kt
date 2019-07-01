package inovex.ad.multiar.polyViewerModule.poly.list

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import inovex.ad.multiar.GlideApp
import inovex.ad.multiar.R
import inovex.ad.multiar.polyViewerModule.poly.AssetThumbnail

class AssetRecyclerAdapter(
    private var assets: List<AssetThumbnail>,
    private val onThumbnailClickListener: OnThumbnailClickListener,
    private val appContext: Context
) :
    Adapter<RecyclerViewHolder>() {

    interface OnThumbnailClickListener {
        fun onThumbnailClick(assetThumbnail: AssetThumbnail)
    }

    val TAG = javaClass.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        //Log.d(TAG, "onCreateViewHolder()")
        return RecyclerViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item_asset, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return assets.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        //Log.d(TAG, "onBindViewHolder()")
        val currentAsset = assets[position]

        holder.authorNameTv.text = "by "  + currentAsset.authorName
        holder.displayNameTv.text = currentAsset.displayName

        GlideApp.with(appContext)
            .load(currentAsset.thumbnailURL)
            .placeholder(R.drawable.placeholder_img)
            .into(holder.thumbnailIv)

        holder.bind(currentAsset, onThumbnailClickListener)
    }

    fun listChanged(newList: List<AssetThumbnail>) {
        //Log.d(TAG,"listChanged()")
        //Log.d(TAG,"newList = "+newList.toString())
        this.assets = newList
        notifyDataSetChanged()
    }
}