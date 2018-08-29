package ro.andonescu.playground.apigateway.controllers

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import ro.andonescu.playground.apigateway.controllers.webforms.Ip
import ro.andonescu.playground.apigateway.controllers.webforms.Page
import ro.andonescu.playground.apigateway.controllers.webforms.PageInfo
import ro.andonescu.playground.apigateway.controllers.webforms.errors.ErrorField
import ro.andonescu.playground.apigateway.controllers.webforms.errors.ErrorMessage
import ro.andonescu.playground.apigateway.services.DatabaseStorage
import java.util.stream.Collectors
import javax.validation.Valid


@RestController
@RequestMapping("api/ips")
class IpsController(val databaseStorage: DatabaseStorage) {


    @GetMapping()
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

    @PostMapping
    fun add(@Valid @RequestBody ip: Ip, bindingResult: BindingResult): Mono<ResponseEntity<*>> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream().map { err -> ErrorField(err.field, err.toString()) }.collect(Collectors.toList())

            return Mono.just(
                    ResponseEntity.badRequest()
                            .contentType(APPLICATION_JSON)
                            .body(ErrorMessage(errors))
            )
        } else {
            TODO()
        }
    }

    @GetMapping("{ip}")
    fun find(@PathVariable ip: String): Mono<ResponseEntity<*>> {

        val toIpResponseEntity = { el: String -> ResponseEntity.ok().contentType(APPLICATION_JSON).body(Ip(ip)) }

        return Mono.just(
                databaseStorage
                        .find(ip)
                        .map(toIpResponseEntity)
                        .getOrElse(ResponseEntity.notFound().build())
        )
    }

    @DeleteMapping("{ip}")
    fun delete(@PathVariable ip: String): Mono<ResponseEntity<*>> {

        val response = if (databaseStorage.remove(ip))
            ResponseEntity.ok("")
        else
            ResponseEntity.notFound().build()

        return Mono.just(response)
    }
}
