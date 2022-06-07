package kelompok.tiga.app

import android.Manifest.permission.*
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kelompok.tiga.app.ui.theme.KaDoInTheme
import kelompok.tiga.app.util.PermissionState
import kelompok.tiga.app.view.AnotherScreen
import kelompok.tiga.app.view.HomeScreen

class MainActivity : ComponentActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private var state: MutableState<PermissionState> = mutableStateOf(PermissionState.Wait)

    companion object {
        private val neededPermissions: Array<String> = arrayOf(
            INTERNET, RECORD_AUDIO,
            MODIFY_AUDIO_SETTINGS, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAllPermission()
        setContent {
            KaDoInTheme {
                //TestScreen()
                when (state.value) {
                    is PermissionState.Wait -> {
                        AnotherScreen {
                            CircularProgressIndicator()
                        }
                    }
                    is PermissionState.Denied -> {
                        AnotherScreen {
                            Text(
                                text = "Aplikasi ini membutuhkan akses:\nInternet, Audio dan Penyimpanan",
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is PermissionState.Granted -> HomeScreen()
                }
            }
        }
    }

    private fun checkAllPermission() {
        val requestPermissions: MutableList<String> = mutableListOf()
        neededPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                requestPermissions.add(permission)
            }
        }
        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                requestPermissions.toTypedArray(),
                29
            )
        } else {
            state.value = PermissionState.Granted
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 29 && grantResults.isNotEmpty()) {
            // find any code of PERMISSION_DENIED in grantResults
            // if found then return the index of first element
            val hasDenied = grantResults.find { code ->
                code == PERMISSION_DENIED
            }

            if (hasDenied != null) {
                state.value = PermissionState.Denied
            } else {
                state.value = PermissionState.Granted
            }
        }
    }
}