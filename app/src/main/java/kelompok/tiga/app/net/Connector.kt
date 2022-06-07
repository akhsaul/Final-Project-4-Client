package kelompok.tiga.app.net

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object Connector {
    private const val TAG = "Connector"
    private const val BASE_URL = "https://backend-kel3.xyz/api/"
    private var cacheDir: File? = null
    private var downloadDir: File? = null
    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    val getApi: MyApiService by lazy {
        requireNotNull(retrofit).create(MyApiService::class.java)
    }

    fun getCacheDir(): File {
        return requireNotNull(cacheDir)
    }

    fun getDownloadDir(): File {
        return requireNotNull(downloadDir)
    }

    fun buildImageURL(imgName: String): String {
        return BASE_URL + "data/image/" + imgName
    }

    /*
    fun downloadSound(soundName: String): File? {
        return runCatching {

            val response = requireNotNull(client).newCall(
                Request.Builder()
                    .url(BASE_URL + "data/sound/" + soundName)
                    .get()
                    .build()
            ).execute()
            val fileOutput = File(getDownloadDir(), soundName)

            response.body?.use { b ->
                b.byteStream().buffered().use { i ->
                    fileOutput.outputStream().buffered().use { o ->
                        val buf = ByteArray(8 * 1024)
                        var n: Int
                        while (i.read(buf).also { n = it } != -1) {
                            o.write(buf, 0, n)
                        }
                    }
                }
            }
            fileOutput
        }.onFailure {
            Log.e(TAG, "Error when downloadSound", it)
        }.getOrNull()
    }*/

    fun init(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (cacheDir == null && downloadDir == null) {
                synchronized(Connector) {
                    if (cacheDir == null && downloadDir == null) {
                        initPath(context)
                    }
                }
            }
            if (client == null) {
                synchronized(Connector) {
                    if (client == null) {
                        initClient()
                    }
                }
            }
            if (retrofit == null) {
                synchronized(Connector) {
                    if (retrofit == null) {
                        initRetrofit()
                    }
                }
            }
        }
    }

    private fun initRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(requireNotNull(client))
            .build()
    }

    private fun initClient() {
        client = OkHttpClient.Builder()
            .cache(Cache(getCacheDir(), 10_000_000))
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
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

        cacheDir = File(fixedDir, "cache").also {
            it.mkdirs()
            // make sure we have permission
            require(it.canRead() && it.canWrite())
            Log.e(TAG, it.absolutePath)
        }
        downloadDir = File(fixedDir, "download").also {
            it.mkdirs()
            // make sure we have permission
            require(it.canRead() && it.canWrite())
            Log.e(TAG, it.absolutePath)
        }
    }
}