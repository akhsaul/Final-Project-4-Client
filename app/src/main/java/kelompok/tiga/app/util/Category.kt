package kelompok.tiga.app.util

import kelompok.tiga.app.R

sealed class Category(val title: String, val img: Int) {
    object Hewan : Category("Hewan", R.drawable.ic_animals)
    object Buah : Category("Buah", R.drawable.ic_fruits)
    object Benda : Category("Benda", R.drawable.ic_objects)
    object Tumbuhan : Category("Tumbuhan", R.drawable.ic_plants)
    companion object {
        val list: List<Category> by lazy {
            listOf(Hewan, Buah, Benda, Tumbuhan)
        }
    }
}
