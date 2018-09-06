package ro.andonescu.playground.apigateway.services.impl

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.equalToIgnoringCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseStorageImplTest {

    var databaseStorage: DatabaseStorageImpl? = null

    val storedIps = listOf("192.168.2.2", "192.168.2.1", "192.168.2.3", "192.168.2.5")

    @BeforeEach
    fun before() {
        // we should not use tested methods to assemble our data, but in this case the exact storage is located in service
        databaseStorage = DatabaseStorageImpl()

        storedIps.map(databaseStorage!!::add)
    }

    @Test
    @DisplayName("DatabaseStorageImpl#findAll should return all IPS from a specific page with the total number of elements")
    fun findAll_forSpecificPageAndSize() {
        val (elements, totalSize) = databaseStorage!!.findAll(2, 1)

        assertEquals(4, totalSize)
        assertThat(elements.size, equalTo(2))

        assertThat(elements.joinToString(";"), equalToIgnoringCase("${storedIps.first()};${storedIps.drop(1).first()}"))
    }

    @Test
    @DisplayName("DatabaseStorageImpl#findAll should return an empty result for a page no & page combination bigger than the max")
    fun findAll_forBiggerPageNo() {
        val (elements, totalSize) = databaseStorage!!.findAll(2, 3)

        assertEquals(4, totalSize)
        assertThat(elements.size, equalTo(0))
    }

    @Test
    @DisplayName("DatabaseStorageImpl#findAll should return an empty result for a page no & page combination less than the min")
    fun findAll_forPageNoLessThan() {
        val (elements, totalSize) = databaseStorage!!.findAll(2, 0)

        assertEquals(4, totalSize)
        assertThat(elements.size, equalTo(0))
    }

    @Test
    @DisplayName("DatabaseStorageImpl#findAll should return less results than request if the page is smaller")
    fun findAll_forLessResults() {
        val (elements, totalSize) = databaseStorage!!.findAll(3, 2)

        assertEquals(4, totalSize)
        assertThat(elements.size, equalTo(1))

        assertThat(elements.joinToString(";"), equalToIgnoringCase("${storedIps.last()}"))
    }
}