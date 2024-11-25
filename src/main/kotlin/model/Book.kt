package ie.setu.model

data class Book (
    var BookTitle: String = "",
    var BookPriority: String = "",
    var BookCategory: String = "",
    var isBookArchived: Boolean = false
){}