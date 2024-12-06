package ie.setu.controller
import ie.setu.model.Book
import ie.setu.persistence.Serializer



class BookAPI (serializerType: Serializer){
    private var serializer: Serializer = serializerType
    private var books = ArrayList<Book>()

    @Throws(Exception::class)
    fun load() {
        books = serializer.read() as ArrayList<Book>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(books)
    }


    fun add(book: Book): Boolean {
        return books.add(book)
    }


//    fun listAllBooks(): String =
//        if  (books.isEmpty()) "No Books stored"
//        else formatListString(books)
//
    fun listAllBooks(): String {
    if (books.isEmpty()) {
        return "No notes stored"
    } else {
        return books.joinToString (separator = "\n") { book ->
            books.indexOf(book).toString() + ": " + book.toString()
        }
    }
}



//    fun listActiveBooks(): String =
//        if  (numberOfActiveBooks() == 0)  "No active Books stored"
//        else formatListString(books.filter { Book -> !Book.isBookArchived})


    fun listActiveBooks(): String {
        return if (numberOfActiveBooks() == 0) {
            "No active notes stored"
        } else {
            var listOfActiveBook = ""
            for (book in books) {
                if (!book.isBookArchived) {
                    listOfActiveBook += "${books.indexOf(book)}: $book \n"
                }
            }
            listOfActiveBook
        }
    }



//    fun listArchivedBooks(): String =
//        if  (numberOfArchivedBooks() == 0) "No archived Books stored"
//        else formatListString(books.filter { Book -> Book.isBookArchived})


    fun listArchivedBooks(): String {
        return if (numberOfArchivedBooks() == 0) {
            "No archived books stored"
        } else {
            var listOfArchivedBook = ""
            for (book in books) {
                if (book.isBookArchived) {
                    listOfArchivedBook += "${books.indexOf(book)}: $book \n"
                }
            }
            listOfArchivedBook
        }
    }


    fun listBooksBySelectedPriority(priority: Int): String =
        if (books.isEmpty()) "No Books stored"
        else {
            val listOfBooks = formatListString(books.filter{ book -> book.BookPriority == priority})
            if (listOfBooks.equals("")) "No Books with priority: $priority"
            else "${numberOfBookByPriority(5)} Books with priority $priority: $listOfBooks"
        }



    fun numberOfBooks(): Int {
        return books.size
    }

//    fun numberOfArchivedBooks(): Int = books.count { Book: Book -> Book.isBookArchived }

    fun numberOfArchivedBooks(): Int {
        var counter = 0
        for (book in books) {
            if (book.isBookArchived) {
                counter++
            }
        }
        return counter
    }

//    fun numberOfActiveBooks(): Int = books.count { Book: Book -> Book.isBookArchived }

    fun numberOfActiveBooks(): Int {
        return books.stream()
            .filter{note: Book -> !note.isBookArchived}
            .count()
            .toInt()
    }

//    fun numberOfBooksByPriority(i: Int): Int = books.count { Book: Book -> Book.isBookArchived }

    fun numberOfBookByPriority(priority: Int): Int {
        var counter = 0
        for (book in books) {
            if (book.BookPriority == priority) {
                counter++
            }
        }
        return counter
    }

    fun findBook(index: Int): Book? {
        return if (isValidListIndex(index, books)) {
            books[index]
        } else null
    }



    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    fun deleteBook(indexToDelete: Int): Book? {
        return if (isValidListIndex(indexToDelete, books)) {
            books.removeAt(indexToDelete)
        } else null
    }

//    fun searchByTitle (searchString : String) =
//        formatListString(
//
//            books.filter { Book ->
//                Book.BookTitle.contains(searchString, ignoreCase = true)
//            }
//        )

    fun searchByTitle(searchString : String) =
        books.filter { book -> book.BookTitle.contains(searchString, ignoreCase = true)}
            .joinToString (separator = "\n") {
                    book ->  books.indexOf(book).toString() + ": " + book.toString() }


    private fun formatListString(BooksToFormat : List<Book>) : String =
        BooksToFormat
            .joinToString (separator = "\n") { Book ->
                books.indexOf(Book).toString() + ": " + Book.toString() }






    fun updateBook(indexToUpdate: Int, book: Book?): Boolean {
        //find the Book object by the index number
        val foundBook = findBook(indexToUpdate)

        //if the Book exists, use the Book details passed as parameters to update the found Book in the ArrayList.
        if ((foundBook != null) && (book != null)) {
            foundBook.BookTitle = book.BookTitle
            foundBook.BookPriority = book.BookPriority
            foundBook.BookCategory = book.BookCategory
            return true
        }

        //if the Book was not found, return false, indicating that the update was not successful
        return false
    }

    fun isValidIndex(index: Int): Boolean {
        return isValidListIndex(index, books);
    }

    fun archiveBook(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val BookToArchive = books[indexToArchive]
            if (!BookToArchive.isBookArchived) {
                BookToArchive.isBookArchived = true
                return true
            }
        }
        return false
    }


}
