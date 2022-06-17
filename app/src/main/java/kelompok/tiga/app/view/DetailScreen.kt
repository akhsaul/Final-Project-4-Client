package kelompok.tiga.app.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.*
import coil.request.ImageRequest
import com.chibde.visualizer.LineBarVisualizer
import kelompok.tiga.app.AboutActivity
import kelompok.tiga.app.R
import kelompok.tiga.app.model.DetailViewModel
import kelompok.tiga.app.ui.theme.GreenMint
import kelompok.tiga.app.util.MediaState
import kelompok.tiga.app.util.PlayerState
import kelompok.tiga.app.util.Singleton
import kotlin.random.Random

private const val TAG = "DetailScreen"

@Composable
fun DetailScreen(viewModel: DetailViewModel, onBack: () -> Unit) {
    // Fetching the local context
    val mContext = LocalContext.current
    val state = viewModel.mediaState.collectAsState()
    var playerState: PlayerState by remember {
        mutableStateOf(PlayerState.Completed)
    }
    viewModel.initModel(mContext)
    viewModel.mediaOnComplete {
        playerState = PlayerState.Completed
    }
    // Start to load image spinner in resource folder
    val spinner = rememberAsyncImagePainter(
        model = R.drawable.spinner,
        imageLoader = Singleton.getImageLoader()
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = GreenMint,
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
                            .padding(horizontal = 30.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Content(viewModel, playerState, spinner) {
                            playerState = if (playerState == PlayerState.PLaying) {
                                viewModel.pauseAudio()
                            } else {
                                Log.i(TAG, "try to play")
                                viewModel.playAudio()
                            }
                        }
                    }
                }
                is MediaState.Loading -> {
                    // TODO
                    //  use Shimmer
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
                    viewModel.initModel(mContext)
                }
            }
        }
    )
}

@Composable
private fun Content(
    viewModel: DetailViewModel,
    playerState: PlayerState,
    spinner: Painter,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(350.dp)
            .padding(
                start = 0.dp, end = 0.dp,
                top = 0.dp, bottom = 30.dp
            ),
        shape = RoundedCornerShape(30.dp),
        elevation = 15.dp,
        backgroundColor = Color(0xFFF7F7F7)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(
                        start = 10.dp, end = 10.dp,
                        top = 20.dp, bottom = 10.dp
                    ),
                verticalArrangement = Arrangement.Center
            ) {

                SubcomposeAsyncImage(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    model = viewModel.getImageRequest()
                        .data(viewModel.getImgURL())
                        .build(),
                    contentDescription = viewModel.getData().name,
                    imageLoader = Singleton.getImageLoader()
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            SubcomposeAsyncImageContent(
                                painter = spinner,
                                contentDescription = "Spinner",
                            )
                        }
                        is AsyncImagePainter.State.Error -> {
                            SubcomposeAsyncImageContent(
                                painter = painterResource(R.drawable.broken_img),
                                contentDescription = "Image Error",
                            )
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                Text(
                    text = viewModel.getData().name,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
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
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    factory = { context ->
                        LineBarVisualizer(context)
                    }
                ) {
                    it.setPlayer(viewModel.getAudioSession())
                }
            }
        }
    }

    IconButton(onClick = onPlay) {
        Icon(
            imageVector = when (playerState) {
                is PlayerState.PLaying -> Icons.Outlined.PauseCircle
                is PlayerState.Paused -> Icons.Outlined.PlayCircle
                is PlayerState.Completed -> Icons.Outlined.PlayCircle
            },
            contentDescription = "Audio Controls",
            modifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 80.dp)
        )
    }
}