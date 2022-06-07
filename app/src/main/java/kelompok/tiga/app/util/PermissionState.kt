package kelompok.tiga.app.util

sealed class PermissionState {
    object Wait : PermissionState()
    object Denied : PermissionState()
    object Granted : PermissionState()
}