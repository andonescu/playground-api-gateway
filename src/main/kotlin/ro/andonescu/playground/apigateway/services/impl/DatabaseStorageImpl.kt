package ro.andonescu.playground.apigateway.services.impl

import io.vavr.API.None
import io.vavr.API.Some
import io.vavr.control.Option
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import ro.andonescu.playground.apigateway.services.DatabaseStorage
import java.util.concurrent.ConcurrentHashMap

@Service
class DatabaseStorageImpl : DatabaseStorage {

    private val database: MutableSet<String> = ConcurrentHashMap.newKeySet<String>()

    override fun findAll(size: Int, page: Int): Pair<List<String>, Int> {
        val totalSize = database.size

        val startPos = (page - 1) * size

        // in case we request a page which does not exist
        if (startPos >= totalSize || startPos < 0) {
            return Pair(listOf(), totalSize)
        }


        val endPos = if (startPos + page > totalSize - 1)
            startPos + page - ((startPos + page) - totalSize) - 1
        else
            startPos + page

        return Pair(database.toList().subList(startPos, endPos + 1), totalSize)
    }

    override fun find(ip: String?): Option<String> {
        return if (database.contains(ip))
            Some(ip)
        else
            None<String>()
    }

    override fun add(ip: String?): Option<Unit> {
        return if (StringUtils.isNotBlank(ip) && database.add(ip!!))
            Some(Unit)
        else
            None<Unit>()
    }

    override fun remove(ip: String?): Boolean {
        TODO("not implemented")
    }
}