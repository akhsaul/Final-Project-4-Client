package kelompok.tiga.app.net

import retrofit2.http.GET
import retrofit2.http.Path
import kelompok.tiga.app.data.Result
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Streaming

interface MyApiService {

    @GET("data/{category}/")
    suspend fun getListData(@Path("category") category: String): Result

    @Streaming
    @GET("data/sound/{soundName}")
    suspend fun downloadSound(@Path("soundName") soundName: String): Response<ResponseBody>
}