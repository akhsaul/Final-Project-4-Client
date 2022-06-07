package kelompok.tiga.app.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibde.visualizer.LineBarVisualizer
import kelompok.tiga.app.AboutActivity
import kelompok.tiga.app.R
import kelompok.tiga.app.model.DetailViewModel
import kelompok.tiga.app.util.MediaState
import kelompok.tiga.app.util.PlayerState

private const val TAG = "DetailScreen"

@Composable
fun DetailScreen(viewModel: DetailViewModel, onBack: () -> Unit) {
    // Fetching the local context
    val mContext = LocalContext.current
    val state = viewModel.mediaState.collectAsState()
    var playerState: PlayerState by remember {
        mutableStateOf(PlayerState.Completed)
    }
    viewModel.setOnCompletionListener {
        playerState = PlayerState.Completed
    }

    val imgBuilder by remember {
        derivedStateOf {
            ImageRequest.Builder(mContext)
                .listener(
                    onStart = { request -> },
                    onCancel = { request ->

                    },
                    onError = { request, result ->
                        result.throwable
                    }
                )
                .placeholder(0)
                .build()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFA7E1BD),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = mContext.resources.getString(R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        mContext.startActivity(Intent(mContext, AboutActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Info, contentDescription = "Tentang")
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        },
        content = {
            when (state.value) {
                is MediaState.Ready -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 30.dp)
                                .padding(bottom = 30.dp),
                            shape = RoundedCornerShape(30.dp),
                            elevation = 5.dp,
                            backgroundColor = Color(0xFFF7F7F7)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    model = imgBuilder.newBuilder()
                                        .data(viewModel.getImgURL())
                                        .build(),
                                    contentDescription = viewModel.getData().name
                                )
                                Text(
                                    text = viewModel.getData().name,
                                    style = MaterialTheme.typography.h5,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "[" + viewModel.getData().title + "]",
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                //Visualizer
                                AndroidView(
                                    modifier = Modifier
                                        .height(110.dp)
                                        .padding(top = 10.dp),
                                    factory = { context ->
                                        LineBarVisualizer(context)
                                    }
                                ) {
                                    it.setPlayer(viewModel.getAudioSession())
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                playerState = if (playerState == PlayerState.PLaying) {
                                    viewModel.pauseAudio()
                                } else {
                                    Log.i("Ikhsan", "try to play")
                                    viewModel.playAudio()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (playerState) {
                                        is PlayerState.PLaying -> R.drawable.ic_pause
                                        is PlayerState.Paused -> R.drawable.ic_play
                                        is PlayerState.Completed -> R.drawable.ic_play
                                    }
                                ),
                                contentDescription = "Audio Controls",
                                modifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 80.dp)
                            )
                        }
                    }
                }
                is MediaState.Loading -> {
                    AnotherScreen {
                        CircularProgressIndicator()
                    }
                }
                is MediaState.Error -> {
                    AnotherScreen {
                        Text(text = "Gagal mengambil data")
                    }
                }
                is MediaState.Initial -> {
                    viewModel.init(mContext)
                }
            }
        }
    )
}