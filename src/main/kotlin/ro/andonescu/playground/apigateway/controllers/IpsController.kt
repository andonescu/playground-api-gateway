package ro.andonescu.playground.apigateway.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ro.andonescu.playground.apigateway.services.DatabaseStorage

@RestController
@RequestMapping("/api/ips")
class IpsController(val databaseStorage: DatabaseStorage) {

    @GetMapping("/")
    fun findAll(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestParam(required = false, defaultValue = "1") page: Int
    ) = databaseStorage.findAll(size, page)
}
