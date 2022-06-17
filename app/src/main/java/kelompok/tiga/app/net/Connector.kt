package kelompok.tiga.app.net

import android.content.Context
import android.os.Environment
import android.util.Log
import kelompok.tiga.app.util.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object Connector {
    private const val TAG = "Connector"
    private const val BASE_URL = "https://backend-kel3.xyz/api/"
    private var cacheDir: File? = null
    private var downloadDir: File? = null
    private var clientWithCache: OkHttpClient? = null
    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    fun getCacheDir(): File {
        return Singleton.require(cacheDir) {
            "CacheDir is not found"
        }
    }

    fun getDownloadDir(): File {
        return Singleton.require(downloadDir) {
            "DownloadDir is not found"
        }
    }

    internal fun getClient(useCache: Boolean): OkHttpClient {
        return Singleton.require(
            if (useCache) {
                clientWithCache
            } else {
                client
            }
        ) {
            "OkHttpClient is not initialized"
        }
    }

    private fun getRetrofit(): Retrofit {
        return Singleton.require(retrofit) {
            "Retrofit is not initialized"
        }
    }

    fun getAPI(): MyApiService {
        return getRetrofit().create(MyApiService::class.java)
    }

    fun buildImageURL(imgName: String): String {
        return BASE_URL + "data/image/" + imgName
    }

    internal fun init(context: Context) {
        if (cacheDir == null && downloadDir == null
            || client == null && clientWithCache == null
            || retrofit == null
        ) {
            Log.i(TAG, "Start synchronized $TAG")
            synchronized(Connector::class.java) {
                if (cacheDir == null && downloadDir == null) {
                    initPath(context)
                }

                if (client == null) {
                    initClient()
                }

                if (retrofit == null) {
                    initRetrofit()
                }

                Log.i(TAG, "Success synchronized $TAG")
            }
        }
    }

    private fun initRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient(true))
            .build()
    }

    private fun initClient() {
        client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            )
            .build()

        clientWithCache = client?.newBuilder()
            ?.cache(Cache(getCacheDir(), 10_000_000))
            ?.build()
    }

    private fun initPath(context: Context) {
        val list: List<File?> = listOf(
            context.filesDir,
            context.cacheDir,
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            context.externalCacheDir,
        )
        var fixedDir: File? = null
        for (file in list) {
            if (file != null) {
                if (file.canRead() && file.canWrite()) {
                    fixedDir = file
                    break
                }
            }
        }

        cacheDir = File(fixedDir, "cache").also { f ->
            f.mkdirs()
            // make sure we have permission
            require(f.canRead() && f.canWrite())
            Log.e(TAG, f.absolutePath)
        }
        downloadDir = File(fixedDir, "download").also { f ->
            f.mkdirs()
            // make sure we have permission
            require(f.canRead() && f.canWrite())
            Log.e(TAG, f.absolutePath)
        }
    }
}