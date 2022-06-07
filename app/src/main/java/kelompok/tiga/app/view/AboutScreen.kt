package kelompok.tiga.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val TAG = "AboutScreen"

@Composable
fun AboutScreen(onBack: () -> Unit, names: List<String>, nims: List<String>) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFA7E1BD),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Tentang")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aplikasi ini dibuat oleh:",
                    style = MaterialTheme.typography.h5
                )
                Row {
                    Column(
                        modifier = Modifier.padding(top = 50.dp, end = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        names.forEach { name ->
                            Text(
                                text = name,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(top = 50.dp, start = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        nims.forEach { nim ->
                            Text(
                                text = nim,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
    )
}