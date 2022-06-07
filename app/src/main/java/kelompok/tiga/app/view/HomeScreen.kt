package kelompok.tiga.app.view

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kelompok.tiga.app.DetailActivity
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.model.HomeEvent
import kelompok.tiga.app.model.MyViewModel
import kelompok.tiga.app.ui.theme.*
import kelompok.tiga.app.util.Category
import kelompok.tiga.app.util.Resource
import kelompok.tiga.app.util.SearchState
import kotlin.random.Random

private const val TAG: String = "HomeScreen"

@Composable
fun HomeScreen(myViewModel: MyViewModel = viewModel()) {
    Log.i(TAG, "before observe getData")
    val state = myViewModel.getData().collectAsState()
    Log.i(TAG, "after observe getData")

    val searchState by myViewModel.searchState
    val searchTextState by myViewModel.searchTextState
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFA7E1BD),
        topBar = {
            AppBar(
                context = context,
                searchState = searchState,
                searchTextState = searchTextState,
                onTextChange = {
                    myViewModel.updateSearchTextState(it)
                },
                onCloseClicked = {
                    myViewModel.updateSearchState(SearchState.CLOSED)
                    myViewModel.onTriggerEvent(HomeEvent.Restore)
                },
                onSearchClicked = {
                    if (it.isNotEmpty() && it.isNotBlank()) {
                        myViewModel.onTriggerEvent(HomeEvent.Search)
                        Log.i(TAG, "text value is $it")
                        //Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                onSearchTriggered = {
                    myViewModel.updateSearchState(SearchState.OPENED)
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CategoryRow(viewModel = myViewModel)

                when (val result = state.value) {
                    is Resource.Success -> {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                        ) {
                            Text(text = "Total: ${result.data?.size}")
                        }
                        //NoteItem(requireNotNull(result.data))
                        // Bottom
                        ContentBottom(requireNotNull(result.data), myViewModel)
                    }
                    is Resource.Error -> {
                        result.message?.let { msg -> Log.i(TAG, msg) }
                        AnotherScreen {
                            Text(text = "Gagal mengambil data")
                        }
                    }
                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Initial -> {
                        LaunchedEffect(true) {
                            myViewModel.requestListData()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CategoryRow(viewModel: MyViewModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(Category.list) { item ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable(onClick = {
                        viewModel.setCategory(item)
                    }),
                shape = RoundedCornerShape(16.dp),
                elevation = 2.dp,
                backgroundColor = if (viewModel.isSelected(item)) {
                    Color(0xFF367D5D)
                } else {
                    Color(0xFFF7F7F7)
                }
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                    ) {
                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = item.img),
                            contentDescription = item.title
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.button,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.defaultMinSize(minWidth = 85.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentBottom(data: List<Content>, myViewModel: MyViewModel) {
    val context = LocalContext.current

    val shimmerColors = listOf(
        Color.LightGray.copy(0.9f),
        Color.LightGray.copy(0.2f),
        Color.LightGray.copy(0.9f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1200,
                easing = FastOutLinearInEasing
            ),
            RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    if (data.isNotEmpty()) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            items(data) { item ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxSize()
                        .clickable {
                            Log.i(TAG, "${item.id}, ${item.title}")
                            val intent = Intent(context, DetailActivity::class.java)
                                .putExtra("id", item.id)
                                .putExtra("name", item.name)
                                .putExtra("title", item.title)
                                .putExtra("image", item.image)
                                .putExtra("sound", item.sound)
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    backgroundColor = Color(0xFFF7F7F7)
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                model = ImageRequest.Builder(context)
                                    .data(myViewModel.getImgURL(item))
                                    .build(),
                                contentDescription = item.name
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                                //.background(brush)
                            )
                        }
                    }
                }
            }
        }
    } else {
        AnotherScreen{
            Text(
                text = myViewModel.getMessageResponse(),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
    }
}

private val colors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    data: List<Content>,
    modifier: Modifier = Modifier.fillMaxSize(),
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(data) { res ->
            Box(
                modifier = modifier
            ) {

                val color by remember {
                    mutableStateOf(colors[Random.nextInt(0, colors.size)])
                }
                Canvas(modifier = Modifier.matchParentSize()) {
                    val clipPath = Path().apply {
                        lineTo(size.width - cutCornerSize.toPx(), 0f)
                        lineTo(size.width, cutCornerSize.toPx())
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }

                    clipPath(clipPath) {
                        drawRoundRect(
                            color = color,
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                        drawRoundRect(
                            color = Color(
                                ColorUtils.blendARGB(color.toArgb(), 0x000000, 0.2f)
                            ),
                            topLeft = Offset(size.width - cutCornerSize.toPx(), -100f),
                            size = Size(cutCornerSize.toPx() + 100f, cutCornerSize.toPx() + 100f),
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(end = 32.dp)
                ) {
                    /*
                    Text(
                        text = res.title,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))*/
                    //Image(painter = painterResource(id = res.image), contentDescription = res.title)
                    Text(
                        text = res.title,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}