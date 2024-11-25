package ie.setu

import controllers.bookAPI

import utils.readNextInt
import io.github.oshai.kotlinlogging.KotlinLogging
import model.Note
import persistence.XMLSerializer

import utils.readNextLine
import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}
private val bookAPI = bookAPI(XMLSerializer(File("book.xml")))

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
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | NOTE MENU                      |
         > |   1) Add a note                |
         > |   2) List all book            |
         > |   3) Update a note             |
         > |   4) Delete a note             |
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">"))
    return readNextInt(" > ==>>")
}


fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1  -> addNote()
            2  -> listbook()
            3  -> updateNote()
            4  -> deleteNote()
            5  -> archiveNote()
            6  -> searchbook()
            20  -> save()
            21  -> load()
            0  -> exitApp()
            else -> println("invalid option inputed: $option")
        }
    } while (true)
}

fun listActivebook() {
    println(bookAPI.listActivebook())
}

fun archiveNote() {
    listActivebook()
    if (bookAPI.numberOfActivebook() > 0) {
        //only ask the user to choose the note to archive if active book exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to bookAPI for archiving and check for success.
        if (bookAPI.archiveNote(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}


fun addNote(){
    val noteTitle = readNextLine("Enter a title for the note: ")
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine("Enter a category for the note: ")
    val isAdded = bookAPI.add(Note(noteTitle, notePriority, noteCategory, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}


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
    if (bookAPI.numberOfbook() > 0) {
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
    println(bookAPI.listArchivedbook())
}

fun listAllbook() {
    println(bookAPI.listAllbook())
}


fun updateNote() {
    //logger.info { "updatebook() function invoked" }
    listbook()
    if (bookAPI.numberOfbook() > 0) {
        //only ask the user to choose the note if book exist
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
        if (bookAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine("Enter a title for the note: ")
            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine("Enter a category for the note: ")

            //pass the index of the note and the new note details to bookAPI for updating and check for success.
            if (bookAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no book for this index number")
        }
    }
}

fun deleteNote(){
    //logger.info { "deletebook() function invoked" }
    listbook()
    if (bookAPI.numberOfbook() > 0) {
        //only ask the user to choose the note to delete if book exist
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        //pass the index of the note to bookAPI for deleting and check for success.
        val noteToDelete = bookAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun exitApp(){
    logger.info { "exitApp() function invoked" }
    exitProcess(0)
}