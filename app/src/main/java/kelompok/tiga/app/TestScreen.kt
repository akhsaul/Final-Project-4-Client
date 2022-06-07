package kelompok.tiga.app

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.util.Category

private const val TAG = "TestScreen"

@Composable
fun TestScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(0xFFA7E1BD),
        topBar = {
            TopAppBar {
                Text(text = TAG)
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CategoryRow()

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                ) {
                    Text(text = "Total: 4")
                }
                // Bottom
                ContentBottom(generateData)
            }
        }
    )
}

@Composable
fun CategoryRow() {
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
                    }),
                shape = RoundedCornerShape(16.dp),
                elevation = 2.dp,
                backgroundColor = Color(0xFFF7F7F7)
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
fun ContentBottom(data: List<Content>) {
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

    Column(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        LazyStaggeredGrid(
            columnCount = 2,
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        ) {
            data.forEach { item ->
                item(key = item.id) {
                    Card(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 0.dp)
                            .fillMaxSize()
                            .clickable {
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
                                        .data(item.image.toInt())
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
        }
    }

    /*
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
                                .data(item.image.toInt())
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
    }*/
}

private val generateData: List<Content> = listOf(
    Content(1, "anggur","",R.drawable.anggur.toString(),""),
    Content(2, "Blackboard","",R.drawable.blackboard.toString(),""),
    Content(3, "Singa","",R.drawable.singa.toString(),""),
    Content(4, "Sunflower","",R.drawable.sunflower.toString(),""),
)