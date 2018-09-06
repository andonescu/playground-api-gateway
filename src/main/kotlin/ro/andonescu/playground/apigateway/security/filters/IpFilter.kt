package ro.andonescu.playground.apigateway.security.filters

import com.google.common.net.HttpHeaders.X_FORWARDED_FOR
import io.vavr.control.Option
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ro.andonescu.playground.apigateway.services.DatabaseStorage
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

@Component
@Order(1)
class IpFilter(databaseStorage: DatabaseStorage) : Filter {
    var logger = LoggerFactory.getLogger(IpFilter::class.java)

    override fun destroy() {
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        logger.debug("Entered IpFilter#doFilter method")

        // TODO : add ip validation
        chain?.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig?) {
    }


    private fun getClientIp(requestOpt: Option<HttpServletRequest>): Option<String> {
        return requestOpt.flatMap { request ->

            val remoteAddr = request.getHeader(X_FORWARDED_FOR)

            val ipAddress =
                    if (StringUtils.isBlank(remoteAddr)) request.remoteAddr
                    else
                        remoteAddr

            Option.of(ipAddress)
        }
    }
}