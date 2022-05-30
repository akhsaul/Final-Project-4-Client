package kelompok.tiga.app

import kelompok.tiga.app.data.Content

data class Result(
    val hasResult: Boolean,
    val result: List<Content>,
    val total: Int
)