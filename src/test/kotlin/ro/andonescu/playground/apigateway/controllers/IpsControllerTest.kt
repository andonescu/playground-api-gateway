package ro.andonescu.playground.apigateway.controllers

import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ro.andonescu.playground.apigateway.services.DatabaseStorage

@ExtendWith(SpringExtension::class)
@WebMvcTest
class IpsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private val databaseStorage: DatabaseStorage? = null


    @Test
    @DisplayName("IpsControllerTest#findAll should return all IPS from a specific page")
    fun findAll_forSpecificPageAndSize() {
        // given
        val ips = listOf("192.158.1.4", "192.158.1.2")
        Mockito.`when`(databaseStorage?.findAll(anyInt(), anyInt())).thenReturn(Pair(ips, ips.size))

        // when
        val mvcResult: MvcResult = mockMvc.perform(get("/api/ips"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        println(result.andReturn().response.contentAsString)

        result
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.pageInfo.page").value("1"))
                .andExpect(jsonPath("$.pageInfo.total").value(ips.size))
                .andExpect(jsonPath("$.data", hasSize<Any>(ips.size)))

    }
}