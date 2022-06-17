package kelompok.tiga.app.view

import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import kelompok.tiga.app.DetailActivity
import kelompok.tiga.app.R
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.model.MyViewModel
import kelompok.tiga.app.ui.theme.GreenMint
import kelompok.tiga.app.util.HomeEvent
import kelompok.tiga.app.util.Resource
import kelompok.tiga.app.util.SearchState
import kelompok.tiga.app.util.Singleton

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
        backgroundColor = GreenMint,
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
                                .padding(horizontal = 15.dp, vertical = 0.dp)
                        ) {
                            Text(text = "Total: ${result.data?.size}")
                        }
                        // Bottom
                        ContentBottom(
                            Singleton.require(result.data) {
                                "Data is NULL"
                            },
                            myViewModel
                        )
                    }
                    is Resource.Error -> {
                        result.message?.let { msg -> Log.e(TAG, msg) }
                        AnotherScreen {
                            Text(text = "Gagal mengambil data")
                        }
                    }
                    is Resource.Loading -> {
                        // TODO
                        //  use shimmer
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
private fun CategoryRow(viewModel: MyViewModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp, end = 15.dp,
                top = 15.dp, bottom = 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(Singleton.getCategoryList()) { item ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable(onClick = {
                        viewModel.setCategory(item)
                    }),
                shape = RoundedCornerShape(16.dp),
                elevation = 5.dp,
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
private fun ContentBottom(
    data: List<Content>, myViewModel: MyViewModel
) {
    if (data.isNotEmpty()) {
        val context = LocalContext.current
        myViewModel.initModel(context)
        // Start to load image spinner in resource folder
        val spinner = rememberAsyncImagePainter(
            model = R.drawable.spinner,
            imageLoader = Singleton.getImageLoader()
        )

        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            items(data) { item ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 15.dp)
                        .size(180.dp)
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
                    shape = RoundedCornerShape(15.dp),
                    elevation = 5.dp,
                    backgroundColor = Color(0xFFF7F7F7)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 45.dp)
                        ) {
                            SubcomposeAsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                model = myViewModel.getImageRequest()
                                    .data(myViewModel.getImgURL(item))
                                    .build(),
                                contentDescription = item.name,
                                imageLoader = Singleton.getImageLoader(),
                                loading = {
                                    SubcomposeAsyncImageContent(
                                        painter = spinner,
                                        contentDescription = "Spinner",
                                    )
                                },
                                success = {
                                    SubcomposeAsyncImageContent()
                                },
                                error = {
                                    SubcomposeAsyncImageContent(
                                        painter = painterResource(id = R.drawable.spinner),
                                        contentDescription = "Spinner",
                                    )
                                }
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 15.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.body1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    } else {
        AnotherScreen {
            Text(
                text = myViewModel.getMessageResponse(),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
    }
}