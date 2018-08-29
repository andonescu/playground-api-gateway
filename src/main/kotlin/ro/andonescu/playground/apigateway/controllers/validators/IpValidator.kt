package ro.andonescu.playground.apigateway.controllers.validators

import com.google.common.net.InetAddresses
import io.vavr.control.Try
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import ro.andonescu.playground.apigateway.controllers.webforms.Ip
import ro.andonescu.playground.apigateway.services.DatabaseStorage

@Component
class IpValidator @Autowired constructor(val databaseStorage: DatabaseStorage) : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == Ip::class
    }

    override fun validate(target: Any?, errors: Errors) {
        val ipObj = target as Ip

        if (StringUtils.isBlank(ipObj.ip)) {
            errors.rejectValue("ip", "Ip Address is not provided")
        } else if (Try.of { -> InetAddresses.forString(ipObj.ip) }.isFailure) {
            errors.rejectValue("ip", "Invalid Ip Address")
        } else if (databaseStorage.find(ipObj.ip).isDefined) {
            errors.rejectValue("ip", "Ip Address already exists")
        }
    }
}