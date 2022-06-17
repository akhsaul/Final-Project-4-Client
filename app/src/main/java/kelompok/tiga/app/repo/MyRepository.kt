package kelompok.tiga.app.repo

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kelompok.tiga.app.data.Result
import kelompok.tiga.app.net.Connector
import kelompok.tiga.app.util.Category
import kelompok.tiga.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.io.File

object MyRepository {
    private const val TAG = "MyRepository"

    fun getData(category: Category): Flow<Resource<Result>> = flow {
        val resultResponse = try {
            val result = Connector.getAPI().getListData(category.title)
            Log.i(TAG, "result is ${result.data}")
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "[$TAG] Error in API")
        }
        emit(resultResponse)
    }

    fun getImageURL(imageName: String): String {
        return Connector.buildImageURL(imageName)
    }

    fun getSoundPath(soundName: String): Flow<Resource<Uri>> = flow {
        val fileList: Array<File>? = Connector.getDownloadDir().listFiles { _, name ->
            name == soundName
        }

        if (!fileList.isNullOrEmpty()) {
            emit(Resource.Success(fileList[0].toUri()))
        } else {
            val result = runCatching {
                val response = Connector.getAPI().downloadSound(soundName)
                val fileOutput = File(Connector.getDownloadDir(), soundName)
                if (response.isSuccessful) {
                    Resource.Success(consume(response.body()!!, fileOutput))
                } else {
                    var message = "Unknown"
                    response.errorBody()?.use {body ->
                        val gson = Gson()
                        val map: Map<String, Any> = gson.fromJson(
                            body.string(),
                            object : TypeToken<Map<String, Any>>() {}.type
                        )

                        map.get("msg").let { message = it as String }
                    }

                    throw Error(message)
                }
            }.getOrElse {
                Resource.Error(it.message ?: "Unknown")
            }
            emit(result)
        }
    }

    private fun consume(body: ResponseBody, fileOutput: File): Uri {
        body.use { b ->
            b.byteStream().buffered().use { i ->
                fileOutput.outputStream().buffered().use { o ->
                    val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                    var n: Int
                    while (i.read(buf).also { n = it } != -1) {
                        o.write(buf, 0, n)
                    }
                }
            }
        }
        return fileOutput.toUri()
    }
}