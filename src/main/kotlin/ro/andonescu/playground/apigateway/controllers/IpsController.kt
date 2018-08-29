package ro.andonescu.playground.apigateway.controllers

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.Mono
import ro.andonescu.playground.apigateway.controllers.webforms.Page
import ro.andonescu.playground.apigateway.controllers.webforms.PageInfo
import ro.andonescu.playground.apigateway.services.DatabaseStorage


@RestController
@RequestMapping("api")
class IpsController(val databaseStorage: DatabaseStorage) {

    @GetMapping("ips")
    fun findAll(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestParam(required = false, defaultValue = "1") page: Int
    ): Mono<ResponseEntity<*>> {
        val (data, total) = databaseStorage.findAll(size, page)

        return Mono.just(
                ResponseEntity.ok()
                        .contentType(APPLICATION_JSON)
                        .body(
                                Page(PageInfo(size, page, total), data)
                        )
        )
    }
}
