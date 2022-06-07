package kelompok.tiga.app.util

sealed class PlayerState {
    object PLaying : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
}