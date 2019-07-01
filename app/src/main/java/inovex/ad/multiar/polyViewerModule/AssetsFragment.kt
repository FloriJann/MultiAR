package inovex.ad.multiar.polyViewerModule

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import inovex.ad.multiar.MainActivity
import inovex.ad.multiar.R
import inovex.ad.multiar.polyViewerModule.poly.AssetThumbnail
import inovex.ad.multiar.polyViewerModule.poly.list.AssetRecyclerAdapter
import inovex.ad.multiar.polyViewerModule.poly.list.AssetRecyclerAdapter.OnThumbnailClickListener
import inovex.ad.multiar.walletModule.WalletEntry
import inovex.ad.multiar.walletModule.WalletViewModel
import kotlinx.android.synthetic.main.fragment_assets.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel


class AssetsFragment : Fragment(), OnThumbnailClickListener, AssetRepository.ModelRootURLReceiver {

    private val TAG = javaClass.name

    private val assetRepository: AssetRepository by inject()

    private val assetsViewModel: AssetsFragmentViewModel by sharedViewModel()

    private val walletViewModel: WalletViewModel by sharedViewModel()


    private lateinit var mainActivity: MainActivity

    private lateinit var viewAdapter: AssetRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_assets, container, false)
    }


    override fun onPause() {
        super.onPause()
        saveUIState()
    }

    override fun onResume() {
        super.onResume()
        restoreUIState()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = activity as MainActivity

        searchButton.setOnClickListener {
            searchForAssets()
        }

        // configure the keyboard, so that it closes itself and starts the search for models after action-button was pressed
        searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                searchForAssets()

                val inputManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(v.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                true
            } else {
                false
            }
        }

        assetsViewModel.thumbnail.observe(this, Observer { thumbnail ->
            if (thumbnail != null) {
                this.viewAdapter.listChanged(thumbnail)
            }
        })

        setupAdapter()
        populateSpinner()

        searchForAssets()
    }


    override fun onThumbnailClick(assetThumbnail: AssetThumbnail) {
        Log.d(TAG, assetThumbnail.displayName + " got clicked!")
        assetRepository.getLocalModelRootUrl(assetThumbnail, this)
    }


    override fun onPathToModelRootReceived(walletEntry: WalletEntry) {
        // Maybe do this in AssetRepo??
        walletViewModel.addWalletEntry(walletEntry)
    }

    private fun searchForAssets() {
        assetRepository.tryToGetAssets(
            categorySpinner.selectedItem.toString().toAssetCategory(),
            searchBar.text.toString()
        )
    }


    private fun saveUIState() {
        assetsViewModel.searchText = searchBar.text.toString()
        assetsViewModel.selectedCategory = categorySpinner.selectedItemPosition
    }

    private fun restoreUIState() {
        searchBar.setText(assetsViewModel.searchText)
        categorySpinner.setSelection(assetsViewModel.selectedCategory)
    }


    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun populateSpinner() {
        categorySpinner.adapter = ArrayAdapter<AssetRepository.AssetCategory>(
            this.context,
            android.R.layout.simple_spinner_item,
            AssetRepository.AssetCategory.values()
        )
    }

    private fun setupAdapter() {
        val numberOfColumns = 2
        val gridLayoutManager = GridLayoutManager(activity, numberOfColumns)

        viewAdapter = AssetRecyclerAdapter(
            arrayListOf(),
            onThumbnailClickListener = this,
            appContext = activity?.applicationContext as Context

        )

        assetsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = viewAdapter
        }
    }

    private fun String.toAssetCategory(): AssetRepository.AssetCategory {
        return when (this) {
            "animals" -> AssetRepository.AssetCategory.animals
            "architecture" -> AssetRepository.AssetCategory.architecture
            "art" -> AssetRepository.AssetCategory.art
            "food" -> AssetRepository.AssetCategory.food
            "nature" -> AssetRepository.AssetCategory.nature
            "objects" -> AssetRepository.AssetCategory.objects
            "people" -> AssetRepository.AssetCategory.people
            "scenes" -> AssetRepository.AssetCategory.scenes
            "technology" -> AssetRepository.AssetCategory.technology
            "transport" -> AssetRepository.AssetCategory.transport
            else -> AssetRepository.AssetCategory.none
        }
    }
}
