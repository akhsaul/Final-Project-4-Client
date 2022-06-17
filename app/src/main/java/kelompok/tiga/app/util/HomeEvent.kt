package kelompok.tiga.app.util

sealed class HomeEvent {
    object Search : HomeEvent()
    object Restore : HomeEvent()
    object Normal : HomeEvent()
}