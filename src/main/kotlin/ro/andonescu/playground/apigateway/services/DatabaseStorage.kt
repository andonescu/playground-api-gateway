package ro.andonescu.playground.apigateway.services

import io.vavr.control.Option

interface DatabaseStorage {

    fun findAll(size: Int?, page: Int?): Pair<List<String>, Int>

    fun find(ip: String?): Option<String>

    fun add(ip: String?): Option<Unit>

    fun remove(ip: String?): Boolean
}