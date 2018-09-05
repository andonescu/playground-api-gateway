package ro.andonescu.playground.apigateway.controllers

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import ro.andonescu.playground.apigateway.controllers.validators.IpValidator
import ro.andonescu.playground.apigateway.controllers.webforms.Ip
import ro.andonescu.playground.apigateway.controllers.webforms.Page
import ro.andonescu.playground.apigateway.controllers.webforms.PageInfo
import ro.andonescu.playground.apigateway.controllers.webforms.errors.ErrorField
import ro.andonescu.playground.apigateway.controllers.webforms.errors.ErrorMessage
import ro.andonescu.playground.apigateway.services.DatabaseStorage
import java.net.URI
import java.util.stream.Collectors
import javax.validation.Valid


@RestController
@RequestMapping("api/ips")
class IpsController(val databaseStorage: DatabaseStorage, val ipValidator: IpValidator) {

    @InitBinder
    protected fun initBinder(binder: WebDataBinder) {
        binder.validator = ipValidator
    }


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
    fun add(@RequestBody @Valid ip: Ip, bindingResult: BindingResult): Mono<ResponseEntity<*>> {
        val response = if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream().map { err -> ErrorField(err.field, err.defaultMessage) }.collect(Collectors.toList())

            ResponseEntity.badRequest().contentType(APPLICATION_JSON).body(ErrorMessage(errors))
        } else {
            databaseStorage
                    .add(ip.ip)
                    .map { _ ->
                        ResponseEntity.created(URI("/api/ips/${ip.ip}")).build<String>()
                    }
                    .getOrElse(ResponseEntity.unprocessableEntity().build<String>())
        }

        return Mono.just(response)
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
