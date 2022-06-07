package kelompok.tiga.app.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kelompok.tiga.app.AboutActivity
import kelompok.tiga.app.R
import kelompok.tiga.app.util.SearchState

private const val TAG = "TopBar"

@Composable
fun AppBar(
    context: Context,
    searchState: SearchState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchState) {
        SearchState.CLOSED -> {
            DefaultAppBar(
                context,
                onSearchClicked = onSearchTriggered
            )
        }
        SearchState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.Transparent,
        elevation = 0.dp
    ) {

        TextField(
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search here...",
                    color = Color.Black,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {
                        onSearchClicked(text)
                    },
                    modifier = Modifier.alpha(ContentAlpha.medium)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text.trim())
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.Black.copy(alpha = ContentAlpha.medium)
            )
        )
    }
}

@Composable
fun DefaultAppBar(
    context: Context,
    onSearchClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = context.resources.getString(R.string.app_name))
        },
        actions = {
            IconButton(
                modifier = Modifier.padding(end = 5.dp),
                onClick = { onSearchClicked() }) {
                Icon(Icons.Filled.Search, contentDescription = "Search Content")
            }
            IconButton(
                modifier = Modifier.padding(start = 5.dp),
                onClick = {
                    context.startActivity(Intent(context, AboutActivity::class.java))
                }) {
                Icon(Icons.Filled.Info, contentDescription = "Tentang")
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}