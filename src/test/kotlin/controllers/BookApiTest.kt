package controllers

import ie.setu.controller.BookAPI
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.persistence.XMLSerializer
import ie.setu.model.Book
import ie.setu.persistence.JSONSerializer
import java.io.File


class BookApiTest {

    private var learnKotlin: Book? = null
    private var summerHoliday: Book? = null
    private var codeApp: Book? = null
    private var testApp: Book? = null
    private var swim: Book? = null
    private var populatedBooks: BookAPI? = BookAPI(XMLSerializer(File("books.xml")))
    private var emptyBooks: BookAPI? = BookAPI(XMLSerializer(File("empty-books.xml")))


    @Nested
    inner class AddBookTest {

        @Test
        fun `add book returns true`() {

            val newBook = Book("New Book", 3, "New Category", false)
            assertEquals(5, populatedBooks!!.numberOfBooks())
            assertTrue(populatedBooks!!.add(newBook))
            assertEquals(6, populatedBooks!!.numberOfBooks())
            assertEquals(newBook, populatedBooks!!.findBook(populatedBooks!!.numberOfBooks() - 1))
        }

        @Test
        fun `add book to an empty list to add arraylist`() {
            val newBook = Book("New Book", 3, "New Category", false)
            assertEquals(0, emptyBooks!!.numberOfBooks())
            assertTrue(emptyBooks!!.add(newBook))
            assertEquals(1, emptyBooks!!.numberOfBooks())
            assertEquals(newBook, emptyBooks!!.findBook(emptyBooks!!.numberOfBooks() - 1))

        }
    }

    @Nested
    inner class listBooks {
        @Test
        fun `listAllBooks return no Books Stores message when arraylist is empty`() {

            assertEquals(0, emptyBooks!!.listAllBooks())
            assertTrue(emptyBooks!!.listAllBooks().lowercase().contains("no books stored"))

        }


        @Test
        fun `listAllBooks return list of books when arraylist has books store`() {

            assertEquals(5, populatedBooks!!.numberOfBooks())
            val bookString = populatedBooks!!.listAllBooks().lowercase()
            assertTrue(bookString.contains("learn kotlin"))
            assertTrue(bookString.contains("summer holiday"))
            assertTrue(bookString.contains("code app"))
            assertTrue(bookString.contains("test app"))
            assertTrue(bookString.contains("swim"))
        }

        @Test
        fun `listactiveBooks return no active Books Stores message when arraylist is empty`() {

            assertEquals(0, emptyBooks!!.listActiveBooks())
            assertTrue(emptyBooks!!.listActiveBooks().lowercase().contains("no active books stored"))

        }

        @Test
        fun `listactiveBooks return list of active books when arraylist has books store`() {

            assertEquals(3, populatedBooks!!.numberOfBooks())
            val activeBookString = populatedBooks!!.listActiveBooks().lowercase()
            assertTrue(activeBookString.contains("learn kotlin"))
            assertTrue(activeBookString.contains("code app"))
            assertTrue(activeBookString.contains("test app"))
            assertFalse(activeBookString.contains("summer holiday"))
            assertFalse(activeBookString.contains("swim"))
        }


        @Test
        fun `listBookSelected return no Books when arraylist is empty`() {

            assertEquals(0, populatedBooks!!.numberOfBooks())
            assertTrue(populatedBooks!!.listBooksBySelectedPriority(0).lowercase().contains("no books stored"))

        }


        @Test
        fun `listBookSelectedPority returns no book when no book of that priority exists`() {
            assertEquals(5, populatedBooks!!.numberOfBooks())
            val priority2String = populatedBooks!!.listBooksBySelectedPriority(2).lowercase()
            assertTrue(priority2String.contains("no books with priority: 2"))
            assertTrue(priority2String.contains("learn kotlin"))
        }

        @Test
        fun `listBookSelectedPority returns all book that match that priority when books of that priority exist`() {

            assertEquals(5, populatedBooks!!.numberOfBooks())
            val priority1String = populatedBooks!!.listBooksBySelectedPriority(1).lowercase()
            assertTrue(priority1String.contains("learn kotlin"))
            assertTrue(priority1String.contains("code app"))
            assertTrue(priority1String.contains("test app"))
            assertFalse(priority1String.contains("summer holiday"))
            assertFalse(priority1String.contains("swim"))


            val priority4String = populatedBooks!!.listBooksBySelectedPriority(4).lowercase()
            assertTrue(priority4String.contains("summer holiday"))
            assertTrue(priority4String.contains("swim"))
            assertFalse(priority4String.contains("learn kotlin"))
            assertFalse(priority4String.contains("code app"))
            assertFalse(priority4String.contains("test app"))


        }

    }
@Nested
    inner class DeleteBookTest {

        @Test
        fun `delete a book that does not exist, returns null `() {

            assertNull(emptyBooks!!.numberOfBooks())
            assertNull(populatedBooks!!.deleteBook(-1))
            assertNull(populatedBooks!!.deleteBook(5))
        }

        @Test
        fun `deleting a book that exists delete and returns deleted object`() {
            assertEquals(5, populatedBooks!!.numberOfBooks())
            assertEquals(swim, populatedBooks!!.deleteBook(4))
            assertEquals(4, populatedBooks!!.numberOfBooks())
            assertEquals(learnKotlin, populatedBooks!!.deleteBook(0))
            assertEquals(3, populatedBooks!!.numberOfBooks())
        }
    }


    @Nested
    inner class UpdateBook {
        @Test
        fun `updating a book that does not exist returns false`(){
            assertFalse(populatedBooks!!.updateBook(6, Book("New Book", 3, "New Category", false)))
            assertFalse(populatedBooks!!.updateBook(-1, Book("New Book", 3, "New Category", false)))
            assertFalse(emptyBooks!!.updateBook(0, Book("New Book", 3, "New Category", false)))
        }



        @Test
        fun `updating a note that exists returns true and updates`() {
            //check note 5 exists and check the contents
            assertEquals(swim, populatedBooks!!.findBook(4))
            assertEquals("Swim - Pool", populatedBooks!!.findBook(4)!!.BookTitle)
            assertEquals(3, populatedBooks!!.findBook(4)!!.BookPriority)
            assertEquals("Hobby", populatedBooks!!.findBook(4)!!.BookCategory)

            //update note 5 with new information and ensure contents updated successfully
            assertTrue(populatedBooks!!.updateBook(4, Book("Updating books", 2, "College", false)))
            assertEquals("Updating Note", populatedBooks!!.findBook(4)!!.BookTitle)
            assertEquals(2, populatedBooks!!.findBook(4)!!.BookPriority)
            assertEquals("College", populatedBooks!!.findBook(4)!!.BookCategory)
        }
    }

    @Nested

    inner class PersistenceTests {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            // Saving an empty notes.XML file.
            val storingBooks = BookAPI(XMLSerializer(File("books.xml")))
            storingBooks.store()

            //Loading the empty notes.xml file into a new object
            val loadedBooks = BookAPI(XMLSerializer(File("books.xml")))
            loadedBooks.load()

            //Comparing the source of the notes (storingBooks) with the XML loaded notes (loadedBooks)
            assertEquals(0, storingBooks.numberOfBooks())
            assertEquals(0, loadedBooks.numberOfBooks())
            assertEquals(storingBooks.numberOfBooks(), loadedBooks.numberOfBooks())
        }

        @Test
        fun `saving and loading an loaded collection in XML doesn't loose data`() {
            // Storing 3 notes to the notes.XML file.
            val storingBooks = BookAPI(XMLSerializer(File("books.xml")))
            storingBooks.add(testApp!!)
            storingBooks.add(swim!!)
            storingBooks.add(summerHoliday!!)
            storingBooks.store()

            //Loading notes.xml into a different collection
            val loadedBooks = BookAPI(XMLSerializer(File("books.xml")))
            loadedBooks.load()

            //Comparing the source of the notes (storingBooks) with the XML loaded notes (loadedBooks)
            assertEquals(3, storingBooks.numberOfBooks())
            assertEquals(3, loadedBooks.numberOfBooks())
            assertEquals(storingBooks.numberOfBooks(), loadedBooks.numberOfBooks())
            assertEquals(storingBooks.findBook(0), loadedBooks.findBook(0))
            assertEquals(storingBooks.findBook(1), loadedBooks.findBook(1))
            assertEquals(storingBooks.findBook(2), loadedBooks.findBook(2))
        }

        @Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
            // Saving an empty notes.json file.
            val storingBooks = BookAPI(JSONSerializer(File("books.json")))
            storingBooks.store()

            //Loading the empty notes.json file into a new object
            val loadedBooks = BookAPI(JSONSerializer(File("books.json")))
            loadedBooks.load()

            //Comparing the source of the notes (storingBooks) with the json loaded notes (loadedBooks)
            assertEquals(0, storingBooks.numberOfBooks())
            assertEquals(0, loadedBooks.numberOfBooks())
            assertEquals(storingBooks.numberOfBooks(), loadedBooks.numberOfBooks())
        }

        @Test
        fun `saving and loading an loaded collection in JSON doesn't loose data`() {
            // Storing 3 notes to the notes.json file.
            val storingBooks = BookAPI(JSONSerializer(File("books.json")))
            storingBooks.add(testApp!!)
            storingBooks.add(swim!!)
            storingBooks.add(summerHoliday!!)
            storingBooks.store()

            //Loading notes.json into a different collection
            val loadedBooks = BookAPI(JSONSerializer(File("books.json")))
            loadedBooks.load()

            //Comparing the source of the notes (storingBooks) with the json loaded notes (loadedBooks)
            assertEquals(3, storingBooks.numberOfBooks())
            assertEquals(3, loadedBooks.numberOfBooks())
            assertEquals(storingBooks.numberOfBooks(), loadedBooks.numberOfBooks())
            assertEquals(storingBooks.findBook(0), loadedBooks.findBook(0))
            assertEquals(storingBooks.findBook(1), loadedBooks.findBook(1))
            assertEquals(storingBooks.findBook(2), loadedBooks.findBook(2))
        }


    }

    @Nested
    inner class ArchiveNotes {
        @Test
        fun `archiving a book that does not exist returns false`() {
            assertFalse(populatedBooks!!.archiveBook(6))
            assertFalse(populatedBooks!!.archiveBook(-1))
            assertFalse(emptyBooks!!.archiveBook(0))
        }


        @Test
        fun `archiving an already archived book returns false`(){
            assertTrue(populatedBooks!!.findBook(2)!!.isBookArchived)
            assertFalse(populatedBooks!!.archiveBook(2))
        }

        @Test
        fun `archiving an active book that exists returns true and archives`() {
            assertFalse(populatedBooks!!.findBook(1)!!.isBookArchived)
            assertTrue(populatedBooks!!.archiveBook(1))
            assertTrue(populatedBooks!!.findBook(1)!!.isBookArchived)
        }
    }



    @Nested
    inner class CountingMethods {

        @Test
        fun numberOfBookCalculatedCorrectly() {
            assertEquals(5, populatedBooks!!.numberOfBooks())
            assertEquals(0, emptyBooks!!.numberOfBooks())
        }

        @Test
        fun numberOfArchivedBookCalculatedCorrectly() {
            assertEquals(2, populatedBooks!!.numberOfArchivedBooks())
            assertEquals(0, emptyBooks!!.numberOfArchivedBooks())
        }

        @Test
        fun numberOfActiveBookCalculatedCorrectly() {
            assertEquals(3, populatedBooks!!.numberOfActiveBooks())
            assertEquals(0, emptyBooks!!.numberOfActiveBooks())
        }

        @Test
        fun numberOfBookByPriorityCalculatedCorrectly() {
            assertEquals(1, populatedBooks!!.numberOfBookByPriority(1))
            assertEquals(0, populatedBooks!!.numberOfBookByPriority(2))
            assertEquals(1, populatedBooks!!.numberOfBookByPriority(3))
            assertEquals(2, populatedBooks!!.numberOfBookByPriority(4))
            assertEquals(1, populatedBooks!!.numberOfBookByPriority(5))
            assertEquals(0, emptyBooks!!.numberOfBookByPriority(1))
        }
    }







    @Nested
    inner class ListBooks {

        @Test
        fun `listAllBooks when ArrayList returns No Books Stored message when ArrayList is empty`() {
            assertEquals(0, emptyBooks!!.numberOfBooks())
            assertTrue(emptyBooks!!.listAllBooks().lowercase().contains("no books stored"))
        }


        @Test
        fun `listAllBooks returns Books when ArrayList has book stored`() {
            assertEquals(5, populatedBooks!!.numberOfBooks())
            val booksString = populatedBooks!!.listAllBooks().lowercase()
            assertTrue(booksString.contains("learning kotlin"))
            assertTrue(booksString.contains("code app"))
            assertTrue(booksString.contains("test app"))
            assertTrue(booksString.contains("swim"))
            assertTrue(booksString.contains("summer holiday"))
        }

        @Test
        fun `listActiveBooks returns no active book stored when ArrayList is empty`() {
            assertEquals(0, emptyBooks!!.numberOfActiveBooks())
            assertTrue(
                emptyBooks!!.listActiveBooks().lowercase().contains("no active notes")
            )
        }

        @Test
        fun `listActiveBooks returns active books when ArrayList has active books stored`() {
            assertEquals(3, populatedBooks!!.numberOfActiveBooks())
            val activeNotesString = populatedBooks!!.listActiveBooks().lowercase()
            assertTrue(activeNotesString.contains("learning kotlin"))
            assertFalse(activeNotesString.contains("code app"))
            assertTrue(activeNotesString.contains("summer holiday"))
            assertTrue(activeNotesString.contains("test app"))
            assertFalse(activeNotesString.contains("swim"))
        }

        @Test
        fun `listArchivedBooks returns no archived books when ArrayList is empty`() {
            assertEquals(0, emptyBooks!!.numberOfArchivedBooks())
            assertTrue(
                emptyBooks!!.listArchivedBooks().lowercase().contains("no archived notes")
            )
        }

        @Test
        fun `listArchivedNotes returns archived notes when ArrayList has archived notes stored`() {
            assertEquals(2, populatedBooks!!.numberOfArchivedBooks())
            val archivedBookString = populatedBooks!!.listArchivedBooks().lowercase()
            assertFalse(archivedBookString.contains("learning kotlin"))
            assertTrue(archivedBookString.contains("code app"))
            assertFalse(archivedBookString.contains("summer holiday"))
            assertFalse(archivedBookString.contains("test app"))
            assertTrue(archivedBookString.contains("swim"))
        }

        @Test
        fun `listBookBySelectedPriority returns No Books when ArrayList is empty`() {
            assertEquals(0, emptyBooks!!.numberOfBooks())
            assertTrue(emptyBooks!!.listBooksBySelectedPriority(1).lowercase().contains("no notes")
            )
        }

        @Test
        fun `listBookBySelectedPriority returns no book when no notes of that priority exist`() {
            //Priority 1 (1 note), 2 (none), 3 (1 note). 4 (2 notes), 5 (1 note)
            assertEquals(5, populatedBooks!!.numberOfBooks())
            val priority2String = populatedBooks!!.listBooksBySelectedPriority(2).lowercase()
            assertTrue(priority2String.contains("no notes"))
            assertTrue(priority2String.contains("2"))
        }



        @Test
        fun `listBookBySelectedPriority returns all book that match that priority when books of that priority exist`() {
            //Priority 1 (1 note), 2 (none), 3 (1 note). 4 (2 notes), 5 (1 note)
            assertEquals(5, populatedBooks!!.numberOfBooks())
            val priority1String = populatedBooks!!.listBooksBySelectedPriority(1).lowercase()
            assertTrue(priority1String.contains("1 note"))
            assertTrue(priority1String.contains("priority 1"))
            assertTrue(priority1String.contains("summer holiday"))
            assertFalse(priority1String.contains("swim"))
            assertFalse(priority1String.contains("learning kotlin"))
            assertFalse(priority1String.contains("code app"))
            assertFalse(priority1String.contains("test app"))


            val priority4String = populatedBooks!!.listBooksBySelectedPriority(4).lowercase()
            assertTrue(priority4String.contains("2 note"))
            assertTrue(priority4String.contains("priority 4"))
            assertFalse(priority4String.contains("swim"))
            assertTrue(priority4String.contains("code app"))
            assertTrue(priority4String.contains("test app"))
            assertFalse(priority4String.contains("learning kotlin"))
            assertFalse(priority4String.contains("summer holiday"))
        }



    }



    @Nested
    inner class SearchMethods {

        @Test
        fun `search book by title returns no books when no books with that title exist`() {
            //Searching a populated collection for a title that doesn't exist.
            assertEquals(5, populatedBooks!!.numberOfBooks())
            val searchResults = populatedBooks!!.searchByTitle("no results expected")
            assertTrue(searchResults.isEmpty())

            //Searching an empty collection
            assertEquals(0, emptyBooks!!.numberOfBooks())
            assertTrue(emptyBooks!!.searchByTitle("").isEmpty())
        }

        @Test
        fun `search book by title returns books when books with that title exist`() {
            assertEquals(5, populatedBooks!!.numberOfBooks())

            //Searching a populated collection for a full title that exists (case matches exactly)
            var searchResults = populatedBooks!!.searchByTitle("Code App")
            assertTrue(searchResults.contains("Code App"))
            assertFalse(searchResults.contains("Test App"))

            //Searching a populated collection for a partial title that exists (case matches exactly)
            searchResults = populatedBooks!!.searchByTitle("App")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))

            //Searching a populated collection for a partial title that exists (case doesn't match)
            searchResults = populatedBooks!!.searchByTitle("aPp")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))
        }
    }















}