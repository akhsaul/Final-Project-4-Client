package kelompok.tiga.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kelompok.tiga.app.ui.theme.AplikasiPengucapanBahasaInggrisTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiPengucapanBahasaInggrisTheme {
                val puppies by viewModels<DataModel>()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(puppies)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AplikasiPengucapanBahasaInggrisTheme {
        MyApp(DataModel())
    }
}

@Composable
fun MyApp(data: DataModel) {
    Scaffold(
        content = {
            HomeContent(data)
        }
    )
}

data class Puppy(
    val id: Int,
    val title: String,
    val sex: String,
    val age: Int,
    val desc: String = "description",
    val imgId: Int = 0
) {
    override fun toString(): String {
        return "$id. name=$title, sex=$sex, age=$age."
    }
}

class DataModel : ViewModel() {

    private val _data = MutableStateFlow(listOf<Puppy>())
    val data: StateFlow<List<Puppy>> get() = _data

    init {
        generate()
    }

    private fun generate() {
        viewModelScope.launch(Dispatchers.Default) {
            val testList = arrayListOf<Puppy>()
            val r = Random
            repeat(r.nextInt(1000, 5000)) {
                testList.add(
                    Puppy(
                        it, "Monty",
                        if (it.mod(2) == 0) "Male" else "Female",
                        r.nextInt(10, 50),
                        imgId = r.nextInt(1024, 1024 * 8 * 8)
                    )
                )
            }
            _data.emit(testList)
        }
    }
}

object DataProvider {
    val puppyList = buildList {
        val r = Random
        repeat(r.nextInt(25, 50)) {
            add(
                Puppy(
                    it, "Monty",
                    if (it.mod(2) == 0) "Male" else "Female",
                    r.nextInt(10, 50),
                    imgId = r.nextInt(1024, 1024 * 8 * 8)
                )
            )
        }
    }
}

@Composable
fun HomeContent(model: DataModel) {
    val data = model.data.collectAsState()

    LazyColumn {
        items(items = data.value,
            itemContent = {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    elevation = 2.dp,
                    backgroundColor = Color.White
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(text = it.toString())
                        }
                    }
                }
            }
        )
    }
}