package ie.setu.model

data class Book (
    var BookTitle: String = "",
    var BookPriority: Int,
    var BookCategory: String = "",
    var isBookArchived: Boolean = false
){}