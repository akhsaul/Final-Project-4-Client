package kelompok.tiga.app.util

sealed class MediaState{
    object Initial: MediaState()
    object Loading: MediaState()
    object Error: MediaState()
    object Ready: MediaState()
}