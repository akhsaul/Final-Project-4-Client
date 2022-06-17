package kelompok.tiga.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kelompok.tiga.app.ui.theme.GreenMint

@Composable
fun AnotherScreen(content: @Composable (ColumnScope.() -> Unit)) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GreenMint
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}