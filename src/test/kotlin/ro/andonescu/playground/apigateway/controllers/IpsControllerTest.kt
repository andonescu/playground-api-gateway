package ro.andonescu.playground.apigateway.controllers

import io.vavr.control.Option
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ro.andonescu.playground.apigateway.services.DatabaseStorage

@ExtendWith(SpringExtension::class)
@WebMvcTest
class IpsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private val databaseStorage: DatabaseStorage? = null


    private val pageNoOne = 1
    private val defaultPageSize = 10

    private val lookupIPAddress = "192.168.2.1"

    @Test
    @DisplayName("IpsControllerTest#findAll should return all IPS from a specific page")
    fun findAll_forSpecificPageAndSize() {
        // given
        val ips = listOf("192.158.1.4", "192.158.1.2")
        Mockito.`when`(databaseStorage?.findAll(defaultPageSize, pageNoOne)).thenReturn(Pair(ips, ips.size))

        // when
        val mvcResult: MvcResult = mockMvc.perform(get("/api/ips"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.pageInfo.page").value(pageNoOne))
                .andExpect(jsonPath("$.pageInfo.total").value(ips.size))
                .andExpect(jsonPath("$.pageInfo.size").value(defaultPageSize))
                .andExpect(jsonPath("$.data", hasSize<Any>(ips.size)))
    }

    @Test
    @DisplayName("IpsControllerTest#findAll should return an empty list when page requested is outside the range")
    fun findAll_forEmptyList() {
        // given
        val pageNo100 = 100
        val maxIps = 20
        Mockito.`when`(databaseStorage?.findAll(10, pageNo100)).thenReturn(Pair(listOf(), maxIps))

        // when
        val mvcResult: MvcResult = mockMvc.perform(get("/api/ips?page=$pageNo100"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.pageInfo.page").value(pageNo100))
                .andExpect(jsonPath("$.pageInfo.total").value(maxIps))
                .andExpect(jsonPath("$.pageInfo.size").value(10))
                .andExpect(jsonPath("$.data", hasSize<Any>(0)))

    }

    @Test
    @DisplayName("IpsControllerTest#find should return the IP object if is stored in database")
    fun find_shouldReturnIpIfDiscovered() {

        //given
        Mockito.`when`(databaseStorage?.find(lookupIPAddress)).thenReturn(Option.of(lookupIPAddress))

        // when
        val mvcResult: MvcResult = mockMvc.perform(get("/api/ips/$lookupIPAddress"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.ip").value(lookupIPAddress))

    }

    @Test
    @DisplayName("IpsControllerTest#find should return not found if the ip address is not stored")
    fun find_shouldReturnNotFoundIfIpIsNotStored() {
        //given
        Mockito.`when`(databaseStorage?.find(lookupIPAddress)).thenReturn(Option.none())

        // when
        val mvcResult: MvcResult = mockMvc.perform(get("/api/ips/$lookupIPAddress"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isNotFound)
    }


    @Test
    @DisplayName("IpsControllerTest#delete should return OK if the IP is removed from the database")
    fun delete_shouldReturnIpIfDiscovered() {

        //given
        Mockito.`when`(databaseStorage?.remove(lookupIPAddress)).thenReturn(true)

        // when
        val mvcResult: MvcResult = mockMvc.perform(delete("/api/ips/$lookupIPAddress"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isOk())
    }

    @Test
    @DisplayName("IpsControllerTest#delete should return not found if the ip address is not stored")
    fun delete_shouldReturnNotFoundIfIpIsNotStored() {
        //given
        Mockito.`when`(databaseStorage?.remove(lookupIPAddress)).thenReturn(false)

        // when
        val mvcResult: MvcResult = mockMvc.perform(delete("/api/ips/$lookupIPAddress"))
                .andExpect(request().asyncStarted())
                .andReturn()

        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("IpsControllerTest#add should return BadRequest if the ip provided is not valid")
    fun add_shouldReturnBadRequestIfInvalid() {
        //given
        val invalidIpData = "{\"ip\":\"192.178.2A.3\"}"

        // when
        val mvcResult: MvcResult = mockMvc.perform(post("/api/ips").contentType(APPLICATION_JSON).content(invalidIpData))
                .andExpect(request().asyncStarted())
                .andReturn()


        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors", hasSize<Any>(1)))
                .andExpect(jsonPath("$.errors[0].field").value("ip"))
                .andExpect(jsonPath("$.errors[0].message").value("Invalid Ip Address"))
    }

    @Test
    @DisplayName("IpsControllerTest#add should return BadRequest if ip is not provided")
    fun add_shouldReturnBadRequestIfNotProvided() {
        //given
        val invalidIpData = "{\"ip\":\"\"}"

        // when
        val mvcResult: MvcResult = mockMvc.perform(post("/api/ips").contentType(APPLICATION_JSON).content(invalidIpData))
                .andExpect(request().asyncStarted())
                .andReturn()


        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors", hasSize<Any>(1)))
                .andExpect(jsonPath("$.errors[0].field").value("ip"))
                .andExpect(jsonPath("$.errors[0].message").value("Ip Address is not provided"))
    }

    @Test
    @DisplayName("IpsControllerTest#add should return BadRequest if ip is already stored")
    fun add_shouldReturnBadRequestIfIpIsStored() {
        //given
        val ipAddress = "192.178.2.3"
        val validJson = "{\"ip\":\"$ipAddress\"}"

        Mockito.`when`(databaseStorage?.find(lookupIPAddress)).thenReturn(Option.of(ipAddress))

        // when
        val mvcResult: MvcResult = mockMvc.perform(post("/api/ips").contentType(APPLICATION_JSON).content(validJson))
                .andExpect(request().asyncStarted())
                .andReturn()


        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors", hasSize<Any>(1)))
                .andExpect(jsonPath("$.errors[0].field").value("ip"))
                .andExpect(jsonPath("$.errors[0].message").value("Ip Address already exists"))
    }

    @Test
    @DisplayName("IpsControllerTest#add should return Created if ip is stored in db")
    fun add_shouldReturnCreatedIfIpExists() {
        //given
        val ipAddress = "192.178.2.3"
        val validJson = "{\"ip\":\"$ipAddress\"}"

        Mockito.`when`(databaseStorage?.find(lookupIPAddress)).thenReturn(Option.none())
        Mockito.`when`(databaseStorage?.add(lookupIPAddress)).thenReturn(Option.of(Unit))

        // when
        val mvcResult: MvcResult = mockMvc.perform(post("/api/ips").contentType(APPLICATION_JSON).content(validJson))
                .andExpect(request().asyncStarted())
                .andReturn()


        // then
        val result: ResultActions = this.mockMvc.perform(asyncDispatch(mvcResult))

        result
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", containsString("api/ips/$ipAddress")))
    }
}