package kelompok.tiga.app

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kelompok.tiga.app.ui.theme.AplikasiPengucapanBahasaInggrisTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    val TAG = this.javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiPengucapanBahasaInggrisTheme {
                val navController = rememberNavController()
                Log.d(TAG, navController.toString())
                Navigator(navController)
                //val a: TestDataModel = viewModel()
                //val puppies by viewModels<DataModel>()
                // A surface container using the 'background' color from the theme
            }
        }
    }
}

private val data = """
    {
    "name":"nama_item",
    "category":"plant",
    "audio":"link_audio",
    "img":"link_image"
    }
""".trimIndent()

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
            //repeat(r.nextInt(1000, 5000)) {
            repeat(1000) {
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

data class Item(
    val id: Int,
    val title: String,
    val imgId: Int
)

class TestDataModel : ViewModel() {
    companion object {
        private val animals = arrayOf(
            mapOf(
                "title" to "Lion",
                "image" to R.drawable.lion
            )
        )
        private val fruits = arrayOf(
            mapOf(
                "title" to "Apple",
                "image" to R.drawable.apple
            )
        )
    }

    private val _data = MutableStateFlow(listOf<Item>())
    val data: StateFlow<List<Item>> get() = _data

    private fun generate() {
        viewModelScope.launch(Dispatchers.Default) {
            val testList = arrayListOf<Item>()
            val r = Random
            //repeat(r.nextInt(1000, 5000)) {
            repeat(10) {
            }
            _data.emit(testList)
        }
    }

    init {
        generate()
    }
}

