package kelompok.tiga.app.model

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.repo.MyRepository
import kelompok.tiga.app.util.MediaState
import kelompok.tiga.app.util.PlayerState
import kelompok.tiga.app.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class DetailViewModel : ViewModel() {
    companion object {
        const val TAG = "DetailViewModel"
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

    fun init(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            _mediaState.emit(MediaState.Loading)
            Log.e(TAG, "Start initialize MediaPlayer")
            val state = runCatching {
                var uri: Uri? = null
                var message = "Uri of file NOT FOUND"

                val result = MyRepository.getSoundPath(getData().sound)
                result.onEach { resource ->
                    if(resource is Resource.Success){
                        uri = requireNotNull(resource.data)
                    } else if (resource is Resource.Error){
                        message = resource.message!!
                    }
                }.collect()

                if (uri != null) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, uri!!)
                        prepare()
                        setOnCompletionListener(listener = setOnCompletionListener)
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

    fun setOnCompletionListener(listener: (MediaPlayer) -> Unit) {
        setOnCompletionListener = listener
    }

    fun getAudioSession(): Int {
        return getMediaPlayer().audioSessionId
    }
}