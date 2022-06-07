package kelompok.tiga.app

import kelompok.tiga.app.data.Content

fun linearSearch(data: List<Content>?, key: String): List<Content> {
    val result: MutableList<Content> = mutableListOf()
    data?.forEach { content ->
        if (content.name.contains(key, ignoreCase = true)) {
            result.add(content)
        }
    }
    return result
}