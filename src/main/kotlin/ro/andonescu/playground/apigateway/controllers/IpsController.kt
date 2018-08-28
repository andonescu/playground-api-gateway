package ro.andonescu.playground.apigateway.controllers

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ro.andonescu.playground.apigateway.controllers.webforms.Page
import ro.andonescu.playground.apigateway.controllers.webforms.PageInfo
import ro.andonescu.playground.apigateway.services.DatabaseStorage


@RestController
@RequestMapping("/api/ips")
class IpsController(val databaseStorage: DatabaseStorage) {

    @GetMapping("/")
    fun findAll(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestParam(required = false, defaultValue = "1") page: Int
    ): Mono<ServerResponse> {
        val (data, total) = databaseStorage.findAll(size, page)

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(
                        fromObject(Page(PageInfo(data.size, page, total), data))
                )
    }
}
