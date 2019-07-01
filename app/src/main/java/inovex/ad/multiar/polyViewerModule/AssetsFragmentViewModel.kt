package inovex.ad.multiar.polyViewerModule

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class AssetsFragmentViewModel(assetRepository: AssetRepository, app: Application) : AndroidViewModel(app) {

    val thumbnail = assetRepository.thumbnail

    var selectedCategory = 0
    var searchText = ""
}