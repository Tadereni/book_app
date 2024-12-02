package controllers

import ie.setu.controller.BookAPI
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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

}