package ie.setu


import ie.setu.model.Book
import ie.setu.persistence.Serializer
import ie.setu.persistence.XMLSerializer
import ie.setu.utils.readNextInt
import ie.setu.utils.readNextLine

import ie.setu.controller.BookAPI

import io.github.oshai.kotlinlogging.KotlinLogging

import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}
private val bookAPI = BookAPI(XMLSerializer(File("book.xml")))

fun main() {
    runMenu()
}

fun save() {
    try {
        bookAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        bookAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun mainMenu(): Int {
    print(""" 
         > ----------------------------------
         > |        BOOK KEEPER APP         |
         > ----------------------------------
         > | Book MENU                      |
         > |   1) Add a book                |
         > |   2) List all book            |
         > |   3) Update a book             |
         > |   4) Delete a book             |
         > |   5) archive  Book             |
         > |   6) Search a book             | 
         > |  7) archiveBook                | 
         > |   20) Save  a book             |
         > |   21) Load  a book             |  
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">"))
    return readNextInt(" > ==>>")
}


fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1  -> addBook()
            2  -> listbook()
            3  -> updateBook()
            4  -> deleteBook()
            5  -> archiveBook()
            6  -> searchbook()
            20  -> save()
            21  -> load()
            0  -> exitApp()
            else -> println("invalid option inputed: $option")
        }
    } while (true)
}



fun listActivebook() {
    println(bookAPI.listActiveBooks())
}

fun archiveBook() {
    listActivebook()
    listbook()

    if (bookAPI.numberOfActiveBooks() > 0) {
        //only ask the user to choose the Book to archive if active book exist
        val indexToArchive = readNextInt("Enter the index of the Book to archive: ")
        //pass the index of the Book to bookAPI for archiving and check for success.
        if (bookAPI.archiveBook(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun listarchivebook() {
    println(bookAPI.listArchivedBooks())
}


fun archiveBook(indexToArchive: Int): Boolean {
    if (bookAPI.isValidIndex(indexToArchive)) { // Call isValidIndex on bookAPI
        val BookToArchive = bookAPI.findBook(indexToArchive)
        if (BookToArchive != null) {
            if (!BookToArchive.isBookArchived) {
                BookToArchive.isBookArchived = true
                return true
            }
        }
    }
    return false
}





fun addBook(){
    val BookTitle = readNextLine("Enter a title for the Book: ")
    val BookPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val BookCategory = readNextLine("Enter a category for the Book: ")
    val isAdded = bookAPI.add(Book(BookTitle, BookPriority, BookCategory, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}


//fun searchbook() {
//    val searchTitle = readNextLine("Enter the description to search by: ")
//    val searchResults = bookAPI.searchByTitle(searchTitle)
//    if (searchResults.isEmpty()) {
//        println("No book found")
//    } else {
//        println(searchResults)
//    }
//}

fun searchbook() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = bookAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No book found")
    } else {
        println(searchResults)
    }
}



fun listbook() {
    if (bookAPI.numberOfBooks() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL book          |
                  > |   2) View ACTIVE book       |
                  > |   3) View ARCHIVED book     |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllbook()
            2 -> listActivebook()
            3 -> listArchivedbook()
            else -> println("Invalid option entered: $option")
        }
    } else {
        println("Option Invalid - No book stored")
    }
}

fun listArchivedbook() {
    println(bookAPI.listArchivedBooks())
}

fun listAllbook() {
    println(bookAPI.listAllBooks())
}


//fun updateBook() {
//    //logger.info { "updatebook() function invoked" }
//    listbook()
//    if (bookAPI.numberOfBooks() > 0) {
//        //only ask the user to choose the Book if book exist
//        val indexToUpdate = readNextInt("Enter the index of the Book to update: ")
//        if (bookAPI.isValidIndex(indexToUpdate)) {
//            val BookTitle = readNextLine("Enter a title for the Book: ")
//            val BookPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
//            val BookCategory = readNextLine("Enter a category for the Book: ")
//
//            //pass the index of the Book and the new Book details to bookAPI for updating and check for success.
//            if (bookAPI.updateBook(indexToUpdate, Book(BookTitle, BookPriority, BookCategory, false))){
//                println("Update Successful")
//            } else {
//                println("Update Failed")
//            }
//        } else {
//            println("There are no book for this index number")
//        }
//    }
//}

fun updateBook() {
    //logger.info { "updatebook() function invoked" }
    listbook()
    if (bookAPI.numberOfBooks() > 0) {
        //only ask the user to choose the Book if book exist
        val indexToUpdate = readNextInt("Enter the index of the Book to update: ")
        if (bookAPI.isValidIndex(indexToUpdate)) { // Call isValidIndex on bookAPI
            val BookTitle = readNextLine("Enter a title for the Book: ")
            val BookPriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val BookCategory = readNextLine("Enter a category for the Book: ")

            //pass the index of the Book and the new Book details to bookAPI for updating and check for success.
            if (bookAPI.updateBook(indexToUpdate, Book(BookTitle, BookPriority, BookCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no book for this index number")
        }
    }
}

fun deleteBook(){
    //logger.info { "deletebook() function invoked" }
    listbook()
    if (bookAPI.numberOfBooks() > 0) {
        //only ask the user to choose the Book to delete if book exist
        val indexToDelete = readNextInt("Enter the index of the Book to delete: ")
        //pass the index of the Book to bookAPI for deleting and check for success.
        val BookToDelete = bookAPI.deleteBook(indexToDelete)
        if (BookToDelete != null) {
            println("Delete Successful! Deleted Book: ${BookToDelete.BookTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}








fun exitApp(){
    logger.info { "exitApp() function invoked" }
    exitProcess(0)
}