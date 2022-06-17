package kelompok.tiga.app.model

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.request.ImageRequest
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.repo.MyRepository
import kelompok.tiga.app.util.MediaState
import kelompok.tiga.app.util.PlayerState
import kelompok.tiga.app.util.Resource
import kelompok.tiga.app.util.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var data: Content? = null
    private var setOnCompletionListener: (MediaPlayer) -> Unit = { _ ->
        Log.i(TAG, "Media Complete")
    }
    private var _mediaState: MutableStateFlow<MediaState> =
        MutableStateFlow(MediaState.Initial)
    val mediaState: StateFlow<MediaState> = _mediaState
    private var imgRequest: ImageRequest.Builder? = null

    fun initModel(context: Context) {
        if (imgRequest == null) {
            synchronized(DetailViewModel::class.java) {
                if (imgRequest == null) {
                    imgRequest = ImageRequest.Builder(context)
                        .setHeader("User-Agent", "Client/KaDoIn")
                        .listener(
                            onStart = { req ->
                                Log.i(TAG, "Image started to load, ${req.data}")
                            },
                            onCancel = { req ->
                                Log.w(TAG, "Image Request Cancelled, ${req.data}")
                            },
                            onError = { req, res ->
                                Log.e(TAG, "Image failed to load, ${req.data}", res.throwable)
                            },
                            onSuccess = { req, _ ->
                                Log.i(TAG, "Image successfully loaded, ${req.data}")
                            }
                        )
                }
            }
        }
        Singleton.coroutine.launch {
            _mediaState.emit(MediaState.Loading)
            Log.i(TAG, "Start initialize MediaPlayer")
            val state = runCatching {
                var uri: Uri? = null
                var message = "Uri of file NOT FOUND"

                val result = MyRepository.getSoundPath(getData().sound)
                result.onEach { resource ->
                    if (resource is Resource.Success) {
                        uri = requireNotNull(resource.data)
                    } else if (resource is Resource.Error) {
                        message = resource.message!!
                    }
                }.collect()

                if (uri != null) {
                    mediaPlayer = MediaPlayer().apply {
                        this.setDataSource(context, uri!!)
                        this.setOnCompletionListener {
                            setOnCompletionListener(it)
                        }
                        prepare()
                    }
                    MediaState.Ready
                } else {
                    throw FileNotFoundException(message)
                }
            }.onFailure {
                Log.e(TAG, "Error when initialize MediaPlayer", it)
            }.getOrDefault(MediaState.Error)

            _mediaState.emit(state)
        }
    }

    fun getImageRequest(): ImageRequest.Builder {
        return Singleton.require(imgRequest)
    }

    fun getData(): Content {
        return requireNotNull(data)
    }

    private fun getAudioManager(): AudioManager {
        return requireNotNull(audioManager)
    }

    private fun getMediaPlayer(): MediaPlayer {
        return requireNotNull(mediaPlayer)
    }

    fun release() {
        mediaPlayer = runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            null
        }.onFailure {
            Log.w(TAG, "Ignored Error", it)
        }.getOrDefault(null)
    }

    fun getImgURL(): String {
        return MyRepository.getImageURL(getData().image)
    }

    fun setManager(audioManager: AudioManager) = apply {
        this.audioManager = audioManager
    }

    fun setData(data: Content) = apply {
        this.data = data
    }

    fun playAudio(): PlayerState {
        if (getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC) < 1) {
            Log.i(TAG, "Volume 0, Force set into 1")
            getAudioManager().setStreamVolume(
                AudioManager.STREAM_MUSIC,
                1,
                AudioManager.FLAG_PLAY_SOUND
            )
        }
        getMediaPlayer().start()
        return PlayerState.PLaying
    }

    fun pauseAudio(): PlayerState {
        getMediaPlayer().pause()
        return PlayerState.Paused
    }

    fun mediaOnComplete(listener: (MediaPlayer) -> Unit) {
        setOnCompletionListener = listener
    }

    fun getAudioSession(): Int {
        return getMediaPlayer().audioSessionId
    }
}