package ro.andonescu.playground.apigateway.controllers.webforms

data class Page<T>(val pageInfo: PageInfo, val data: List<T>)