package inovex.ad.multiar.polyViewerModule.poly

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

interface PolyApi {

    companion object Factory {
        private const val BASE_URL = "https://poly.googleapis.com/v1/"


        fun create(): PolyApi {

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PolyApi::class.java)
        }
    }


    @GET("assets")
    fun getAssetList(@QueryMap(encoded = true) options: Map<String, String>): Call<Assets>

    @Streaming
    @GET
    fun get3DModel(@Url fileUrl:String): Call<ResponseBody>
}