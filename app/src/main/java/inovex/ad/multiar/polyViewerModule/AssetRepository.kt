package inovex.ad.multiar.polyViewerModule

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import inovex.ad.multiar.polyViewerModule.poly.AssetThumbnail
import inovex.ad.multiar.polyViewerModule.poly.Assets
import inovex.ad.multiar.polyViewerModule.poly.Resources
import inovex.ad.multiar.polyViewerModule.poly.UseCasePolyApi
import inovex.ad.multiar.walletModule.WalletEntry
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder


class AssetRepository(private val useCasePolyApi: UseCasePolyApi, private val context: Context) {

    private val TAG = javaClass.name.toString()


    private val intStorageDirectory = context.filesDir.toString()

    private var openDownloadsCounter = 0

    var thumbnail: MutableLiveData<List<AssetThumbnail>> = MutableLiveData()

    interface ModelRootURLReceiver {
        fun onPathToModelRootReceived(walletEntry: WalletEntry)
    }


    @Suppress("EnumEntryName")
    enum class AssetCategory {
        none, animals, architecture, art, food, nature, objects, people, scenes, technology, transport
    }


    fun tryToGetAssets(category: AssetCategory = AssetCategory.none, keywords: String? = null) {

        val call = useCasePolyApi.getAssetList(category, keywords)
        Log.d(TAG, "URL = " + call.request().url().toString())

        call.enqueue(object : Callback<Assets> {
            override fun onResponse(
                call: retrofit2.Call<Assets>?,
                response: Response<Assets>?
            ) {
                if (response != null) {
                    //Log.d("Asset List", response.body().toString())
                    val assets = response.body()

                    @Suppress("SENSELESS_COMPARISON")       // GELOGEN! Nicht senseless!
                    if (assets.assets != null) {
                        val assetThumbnails = makeAssetThumbnailList(assets)
                        thumbnail.postValue(assetThumbnails)
                    } else {
                        //TODO: Handle empty response
                        // Somehow inform user about
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Assets>?, t: Throwable?) {
                Log.e(
                    TAG,
                    "fetching Assets from server failed: " + t.toString()
                )
            }
        })
    }

    private fun makeAssetThumbnailList(assets: Assets): List<AssetThumbnail> {
        val mutableList: MutableList<AssetThumbnail> = mutableListOf()

        assets.assets.forEach {
            it.formats.forEach { format ->
                if (format.formatType == "GLTF2") {
                    val resources = mutableListOf<String>()
                    format.resources.forEach { resource ->
                        resources.add(resource.url)
                        // Log.d("RESOURCE TEST", resource.url)
                    }


                    if (!resources.any { resource ->
                            // Sort out every broken resource file names
                            resource.contains("%20")
                        }) {
                        val urlResources = Resources(format.root.url, resources)
                        mutableList.add(
                            AssetThumbnail(
                                it.displayName,
                                it.authorName,
                                it.name,
                                it.thumbnail.url,
                                urlResources
                            )
                        )
                    }

                }
            }
        }
        return mutableList
    }


    fun getLocalModelRootUrl(asset: AssetThumbnail, listener: ModelRootURLReceiver) {

        val assetFolderName = asset.name.replace("assets/", "")
        val assetFolder = context.filesDir.listFiles()


        // search local for asset folder and return it's path
        assetFolder.forEach {
            //Log.d(TAG, "FileName:" + it.name)
            if (it.name == assetFolderName) {

                val decodedURL = asset.resourceURLs.rootURL.substringAfterLast(
                    "/",
                    "somethingBadHappened"
                )

                val walletEntry = WalletEntry(
                    modelURL = context.filesDir.path + "/" + assetFolderName + "/" + decodedURL,
                    thumbnailUrl = asset.thumbnailURL
                )
                listener.onPathToModelRootReceived(walletEntry)
                return
            }
        }

        // no local version of model
        tryDownload3DModel(asset, listener)
    }

    private fun tryDownload3DModel(asset: AssetThumbnail, listener: ModelRootURLReceiver) {

        currentAsset = asset

        val assetFolderName = asset.name.replace("assets/", "")
        Log.d(TAG, "assetFolderName = $assetFolderName")

        val assetDir = File(intStorageDirectory, assetFolderName)
        Log.d(TAG, "assetDir = " + assetDir.path)
        assetDir.mkdir()


        val rootFileName = asset.resourceURLs.rootURL.substringAfterLast("/", "somethingBadHappened")
        doAsync {
            //Download rootfile
            Log.d(TAG, "asset.resourceURLs.rootURL = " + asset.resourceURLs.rootURL)
            tryDownloadFile(
                asset.resourceURLs.rootURL,
                assetDir.path,
                rootFileName,
                listener
            )
            rootFilePath = assetDir.path + "/$rootFileName"

            //Download resourcefiles
            Log.d(TAG, "asset.resourceURLs.resources:")
            asset.resourceURLs.resources.forEach {
                Log.d(TAG, "resource = $it")

                tryDownloadFile(
                    it,
                    assetDir.path,
                    it.substringAfterLast("/", "somethingBadHappened"),
                    listener
                )
            }
        }

    }

    private var rootFilePath = "error"
    private lateinit var currentAsset: AssetThumbnail

    private fun onDownloadsDone(listener: ModelRootURLReceiver, filePath: String) {

        val directory = filePath.substringBeforeLast(
            "/",
            "SomeThingBadHappened"
        )
        Log.d(TAG, "Files in $directory:")
        File(directory).listFiles().forEach {
            Log.d(TAG, it.name)
        }

        if (rootFilePath != "error") {
            getLocalModelRootUrl(currentAsset, listener)
        }
    }

    private fun tryDownloadFile(url: String, path: String, filename: String, listener: ModelRootURLReceiver) {

        openDownloadsCounter++

        val call = useCasePolyApi.getFile(url)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>?,
                response: Response<ResponseBody>?
            ) {
                if (response != null && response.isSuccessful) {
                    Log.d(TAG, "server contacted and has file")
                    writeResponseBodyToDisk(response.body(), path, filename)
                }
                checkForAllDownloadsDone(listener, "$path/$filename")
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>?, t: Throwable?) {
                Log.e(
                    TAG,
                    "server contact failed: " + t.toString()
                )
                checkForAllDownloadsDone(listener, "$path/$filename")
            }
        })
    }

    @Synchronized
    private fun checkForAllDownloadsDone(listener: ModelRootURLReceiver, rootFilePath: String) {
        openDownloadsCounter--

        if (openDownloadsCounter <= 0) {
            Log.d(TAG, "All downloads done!")
            onDownloadsDone(listener, rootFilePath)
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, path: String, filename: String): String {
        val decodedFilename = URLDecoder.decode(filename, "UTF-8")
        val file = File(path, decodedFilename)

        val inputStream = body.byteStream()
        val outputStream = FileOutputStream(file)

        val fileReader = ByteArray(4096)

        while (true) {
            val read = inputStream.read(fileReader)

            if (read == -1) {
                break
            }
            outputStream.write(fileReader, 0, read)
        }

        outputStream.flush()

        inputStream.close()
        outputStream.close()

        return file.toString()
    }
}