package dev.fastcampus.webflux.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.function.server.ServerRequest

val logger = KotlinLogging.logger {}

@Configuration
class ErrorHandler {

    @Bean
    fun errorAttributes(): DefaultErrorAttributes {
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(
                servletRequest: ServerRequest?,
                options: ErrorAttributeOptions?
            ): MutableMap<String, Any> {

                val request = servletRequest?.exchange()?.request ?: return mutableMapOf()

                MDC.put(KEY_TXID, request.txid)

                try {
                    logger.debug { "request id : ${request.id}" }

                    val e = super.getError(servletRequest)

                    logger.error(e) { "${e.message}" }

                    return super.getErrorAttributes(servletRequest, options)
                } finally {
                    request.txid = null
                    MDC.remove(KEY_TXID)
                }
            }
        }
    }

}

private val txidByReqId = HashMap<String,String>()

var ServerHttpRequest.txid: String?
    get() {
        return txidByReqId[this.id]
    }
    set(vaule) {
        if(vaule == null) txidByReqId.remove((this.id))
        else txidByReqId[this.id] = vaule
    }