package inovex.ad.multiar.polyViewerModule.poly

import inovex.ad.multiar.polyViewerModule.AssetRepository
import okhttp3.ResponseBody
import retrofit2.Call

class UseCasePolyApi(private val polyApi: PolyApi) {

    private val TAG = this.javaClass.name

    private val FORMAT = "GLTF2"
    private val FIELDS =
        "assets(authorName%2Cdescription%2CdisplayName%2Cformats%2Cname%2Cthumbnail)"     // determine the necessary fields
    private val API_KEY = "Insert Your Key Here"
    private val PAGESIZE = "50"

    private val queryMap = mutableMapOf(
        "format" to FORMAT,
        "fields" to FIELDS,
        "key" to API_KEY,
        "pageSize" to PAGESIZE
    )


    fun getAssetList(
        category: AssetRepository.AssetCategory = AssetRepository.AssetCategory.none,
        keywords: String? = null
    ): Call<Assets> {

        if (category.name != "none") {
            queryMap["category"] = category.name
        }
        if (keywords != null) {
            queryMap["keywords"] = keywords
        }

        return polyApi.getAssetList(queryMap)
    }

    fun getFile(fileUrl: String): Call<ResponseBody> {
        return polyApi.get3DModel(fileUrl)
    }
}