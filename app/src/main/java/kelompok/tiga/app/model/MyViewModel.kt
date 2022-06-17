package kelompok.tiga.app.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kelompok.tiga.app.data.Content
import kelompok.tiga.app.linearSearch
import kelompok.tiga.app.repo.MyRepository
import kelompok.tiga.app.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {
    companion object {
        private const val TAG: String = "MyViewModel"
    }

    private val selectedCategory: MutableState<Category> = mutableStateOf(Category.Hewan)
    private var _msgResponse: String = "Unknown"
    private val _data: MutableStateFlow<Resource<List<Content>>> =
        MutableStateFlow(Resource.Initial())
    private var resultSearch: MutableStateFlow<Resource<List<Content>>> =
        MutableStateFlow(Resource.Initial())
    private val event: MutableState<HomeEvent> = mutableStateOf(HomeEvent.Normal)
    private var imgRequest: ImageRequest.Builder? = null

    fun initModel(context: Context) {
        if (imgRequest == null) {
            synchronized(MyViewModel::class.java) {
                if (imgRequest == null) {
                    imgRequest = ImageRequest.Builder(context)
                        .setHeader("User-Agent", "Client/KaDoIn")
                        .listener(
                            onStart = { req ->
                                Log.i(TAG, "Image started to load, ${req.data}")
                            },
                            onCancel = { req ->
                                Log.w(TAG, "Image Request Cancelled, ${req.data}")
                            },
                            onError = { req, res ->
                                Log.e(TAG, "Image failed to load, ${req.data}", res.throwable)
                            },
                            onSuccess = { req, _ ->
                                Log.i(TAG, "Image successfully loaded, ${req.data}")
                            }
                        )
                }
            }
        }
    }

    fun getImageRequest(): ImageRequest.Builder {
        return Singleton.require(imgRequest)
    }

    fun setCategory(category: Category) {
        selectedCategory.value = category
        requestListData()
    }

    fun isSelected(category: Category): Boolean {
        return selectedCategory.value == category
    }

    fun getMessageResponse(): String {
        return _msgResponse
    }

    fun requestListData() {
        Log.i(TAG, "call requestListData")
        Singleton.coroutine.launch {
            _data.emit(Resource.Loading())
            MyRepository.getData(selectedCategory.value)
                .onEach { state ->
                    when (state) {
                        is Resource.Success -> {
                            // if return state of success, then it should be has DATA
                            // kotlin always complain about null values
                            val result = requireNotNull(state.data)
                            _msgResponse = result.msg
                            Log.i(TAG, "result is ${result.data}")
                            _data.emit(Resource.Success(result.data))
                        }
                        is Resource.Error -> {
                            // if return state of success, then it should be has MESSAGE
                            _data.emit(
                                Resource.Error(
                                    state.message ?: "Error when try to get data"
                                )
                            )
                        }
                        else -> {
                            // should not go in here
                            throw IllegalStateException("Unexpected Behaviour")
                        }
                    }
                }.collect()
        }
    }

    fun getImgURL(content: Content): String {
        return MyRepository.getImageURL(content.image)
    }

    private val _searchState: MutableState<SearchState> = mutableStateOf(SearchState.CLOSED)
    val searchState: State<SearchState> = _searchState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchState(newVal: SearchState) {
        _searchState.value = newVal
    }

    fun updateSearchTextState(newVal: String) {
        _searchTextState.value = newVal
    }

    fun onTriggerEvent(event: HomeEvent) {
        this.event.value = event
        getData()
    }

    fun getData(): StateFlow<Resource<List<Content>>> {
        return when (event.value) {
            is HomeEvent.Search -> {
                Log.i(TAG, "$event Triggered")
                searchData()
                resultSearch
            }
            is HomeEvent.Normal, is HomeEvent.Restore -> {
                Log.i(TAG, "$event Triggered")
                viewModelScope.launch {
                    resultSearch.emit(Resource.Initial())
                }
                _data
            }
        }
    }

    private fun searchData() {
        Singleton.coroutine.launch {
            resultSearch.emit(Resource.Loading())
            if (_data.value is Resource.Success) {
                val data = linearSearch(_data.value.data, _searchTextState.value)
                resultSearch.emit(Resource.Success(data))
            }
        }
    }
}