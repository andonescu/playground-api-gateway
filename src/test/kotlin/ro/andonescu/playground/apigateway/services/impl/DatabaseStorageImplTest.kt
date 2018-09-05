package ro.andonescu.playground.apigateway.services.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DatabaseStorageImplTest {

    var databaseStorage: DatabaseStorageImpl? = null

    val storedIps = arrayListOf("192.168.2.1", "192.168.2.2", "192.168.2.3", "192.168.2.5")

    @BeforeEach
    fun before() {
        // we should not use tested methods to assemble our data, but in this case the exact storage is located in service
        databaseStorage = DatabaseStorageImpl()

        storedIps.map (databaseStorage!!::add)
    }

    @Test
    @DisplayName("DatabaseStorageImpl#findAll should return all IPS from a specific page with the total number of elements")
    fun findAll_forSpecificPageAndSize() {
        val (elements, totalSize) = databaseStorage!!.findAll(2, 1)

        assertEquals(4, totalSize)
    }
}