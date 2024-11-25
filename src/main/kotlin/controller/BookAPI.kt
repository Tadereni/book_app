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


    fun add(Book: Book): Boolean {
        return Books.add(Book)
    }


    fun listAllBooks(): String =
        if  (Books.isEmpty()) "No Books stored"
        else formatListString(Books)




    fun listActiveBooks(): String =
        if  (numberOfActiveBooks() == 0)  "No active Books stored"
        else formatListString(Books.filter { Book -> !Book.isBookArchived})




    fun listArchivedBooks(): String =
        if  (numberOfArchivedBooks() == 0) "No archived Books stored"
        else formatListString(Books.filter { Book -> Book.isBookArchived})




    fun listBooksBySelectedPriority(priority: Int): String =
        if (Books.isEmpty()) "No Books stored"
        else {
            val listOfBooks = formatListString(Books.filter{ Book -> Book.BookPriority == priority})
            if (listOfBooks.equals("")) "No Books with priority: $priority"
            else "${numberOfBooksByPriority()} Books with priority $priority: $listOfBooks"
        }



    fun numberOfBooks(): Int {
        return Books.size
    }

    fun numberOfArchivedBooks(): Int = Books.count { Book: Book -> Book.isBookArchived }

    fun numberOfActiveBooks(): Int = Books.count { Book: Book -> Book.isBookArchived }

    fun numberOfBooksByPriority(): Int = Books.count { Book: Book -> Book.isBookArchived }

    fun findBook(index: Int): Book? {
        return if (isValidListIndex(index, Books)) {
            Books[index]
        } else null
    }

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    fun deleteBook(indexToDelete: Int): Book? {
        return if (isValidListIndex(indexToDelete, Books)) {
            Books.removeAt(indexToDelete)
        } else null
    }

    fun searchByTitle (searchString : String) =
        formatListString(
            Books.filter { Book -> Book.BookTitle.contains(searchString, ignoreCase = true) })



    private fun formatListString(BooksToFormat : List<Book>) : String =
        BooksToFormat
            .joinToString (separator = "\n") { Book ->
                Books.indexOf(Book).toString() + ": " + Book.toString() }






    fun updateBook(indexToUpdate: Int, Book: Book?): Boolean {
        //find the Book object by the index number
        val foundBook = findBook(indexToUpdate)

        //if the Book exists, use the Book details passed as parameters to update the found Book in the ArrayList.
        if ((foundBook != null) && (Book != null)) {
            foundBook.BookTitle = Book.BookTitle
            foundBook.BookPriority = Book.BookPriority
            foundBook.BookCategory = Book.BookCategory
            return true
        }

        //if the Book was not found, return false, indicating that the update was not successful
        return false
    }

    fun isValidIndex(index: Int): Boolean {
        return isValidListIndex(index, Books);
    }

    fun archiveBook(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val BookToArchive = Books[indexToArchive]
            if (!BookToArchive.isBookArchived) {
                BookToArchive.isBookArchived = true
                return true
            }
        }
        return false
    }
}
