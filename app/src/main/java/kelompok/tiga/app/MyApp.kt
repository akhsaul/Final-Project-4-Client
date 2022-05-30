package kelompok.tiga.app

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kelompok.tiga.app.ui.theme.AplikasiPengucapanBahasaInggrisTheme

private val TAG = "MyApp.kt"

@Preview(
    showBackground = true, apiLevel = 21, device = Devices.PIXEL_XL
)
@Composable
fun HomePreview() {
    AplikasiPengucapanBahasaInggrisTheme {
        val navController = rememberNavController()
        Home(navController)
    }
}

@Composable
fun Home(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Button(onClick = {
                                navController.navigate(Screen.Info.route)
                        }) {
                            Text(text = "Info")
                        }
                    }
                }
            },
            content = {
                Content(navController)
            }
        )
    }
}

@Composable
fun Content(navController: NavController) {
    //val data = model.data.collectAsState()
    var category by remember {
        mutableStateOf(0)
    }
    val a: TestDataModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Category(category, onClick = { index ->
            category = index
        })

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Text(text = "Count: 5")
        }
        ListContent(category, a)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListContent(categoryId: Int, data: TestDataModel) {
    val a = data.data.collectAsState()
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(a.value) { str ->
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
                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = R.drawable.lion),
                            contentDescription = str.title
                        )
                        Text(
                            text = Util.categories[categoryId],
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Category(selected: Int, onClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        itemsIndexed(Util.categories) { index, item ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable(onClick = {
                        onClick(index)
                    }),
                shape = RoundedCornerShape(16.dp),
                elevation = 2.dp,
                backgroundColor = if (selected == index) {
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
                                .requiredSize(85.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(
                                id = when (index) {
                                    0 -> R.drawable.ic_animals
                                    1 -> R.drawable.ic_fruits
                                    2 -> R.drawable.ic_object
                                    else -> R.drawable.ic_animals
                                }
                            ),
                            contentDescription = item
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.defaultMinSize(minWidth = 85.dp)
                        )
                    }
                }
            }
        }
    }
}
