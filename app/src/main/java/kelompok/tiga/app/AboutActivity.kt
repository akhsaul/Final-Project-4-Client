package kelompok.tiga.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kelompok.tiga.app.ui.theme.KaDoInTheme
import kelompok.tiga.app.view.AboutScreen

class AboutActivity : ComponentActivity() {
    companion object {
        val names: List<String> = listOf(
            "Krishna Dwipayudha",
            "Nursela Basuni",
            "Ikhsan Maulana",
            "Putri Nabila"
        )
        val nims: List<String> = listOf(
            "20416255201185",
            "20416255201205",
            "20416255201192",
            "20416255201050"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaDoInTheme {
                AboutScreen(onBack = { this.finish() }, names, nims)
            }
        }
    }
}
