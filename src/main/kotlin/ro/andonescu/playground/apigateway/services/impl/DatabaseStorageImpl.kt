package ro.andonescu.playground.apigateway.services.impl

import io.vavr.control.Option
import org.springframework.stereotype.Service
import ro.andonescu.playground.apigateway.services.DatabaseStorage

@Service
class DatabaseStorageImpl : DatabaseStorage {
    override fun findAll(size: Int?, page: Int?): Pair<List<String>, Int> {
        TODO("not implemented")
    }

    override fun find(ip: String?): Option<String> {
        TODO("not implemented")
    }

    override fun add(ip: String?): Option<Unit> {
        TODO("not implemented")
    }

    override fun remove(ip: String?): Boolean {
        TODO("not implemented")
    }
}