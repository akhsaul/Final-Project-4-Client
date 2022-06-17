package kelompok.tiga.app.util

import android.content.Context
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.util.DebugLogger
import kelompok.tiga.app.net.Connector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object Singleton {
    private const val TAG: String = "Singleton"
    val coroutine = CoroutineScope(Dispatchers.IO)
    private var imgLoader: ImageLoader? = null
    private var categoryList: MutableList<Category> = mutableListOf()

    fun getImageLoader(): ImageLoader {
        return require(imgLoader)
    }

    @OptIn(ExperimentalContracts::class)
    fun require(condition: Boolean) {
        contract {
            returns() implies condition
        }
        require(condition) {
            "Failed requirement."
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun require(condition: Boolean, message: () -> Any) {
        contract {
            returns() implies condition
        }
        if (!condition) {
            throw IllegalArgumentException(message().toString())
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun <T> require(value: T?): T {
        contract {
            returns() implies (value != null)
        }
        return require(value) {
            "Required value was null."
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun <T> require(value: T?, message: () -> Any): T {
        contract {
            returns() implies (value != null)
        }
        return value ?: throw IllegalArgumentException(message().toString())
    }

    @OptIn(ExperimentalContracts::class)
    fun <T> require(condition: Boolean, value: T): T {
        contract {
            returns() implies condition
        }
        return require(condition, value) {
            "Failed requirement."
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun <T> require(condition: Boolean, value: T, message: () -> Any): T {
        contract {
            returns() implies condition
        }
        return if (condition) {
            value
        } else {
            throw IllegalArgumentException(message().toString())
        }
    }

    fun getCategoryList(): List<Category> {
        return require(categoryList.isNotEmpty().and(categoryList.size == 4), categoryList)
    }

    fun init(context: Context) {
        coroutine.launch {
            if (categoryList.isEmpty()
                || imgLoader == null
            ) {
                Log.i(TAG, "Start synchronized $TAG")

                synchronized(Singleton::class.java) {
                    if (categoryList.isEmpty()) {
                        categoryList.apply {
                            add(Category.Hewan)
                            add(Category.Buah)
                            add(Category.Benda)
                            add(Category.Tumbuhan)
                        }
                    }

                    if (imgLoader == null) {
                        imgLoader = ImageLoader.Builder(context)
                            .logger(DebugLogger())
                            .components {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    add(ImageDecoderDecoder.Factory())
                                } else {
                                    add(GifDecoder.Factory())
                                }
                            }
                            .build()
                    }

                    Log.i(TAG, "Success synchronized $TAG")
                }
            }
        }
        coroutine.launch {
            Connector.init(context)
        }
    }
}