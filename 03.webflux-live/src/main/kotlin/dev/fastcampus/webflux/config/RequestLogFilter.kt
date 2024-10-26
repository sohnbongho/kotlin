package dev.fastcampus.webflux.config

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.channels.Channel
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels

private val logger = KotlinLogging.logger{}

@Component
@Order(11)
class RequestLogFilter :WebFilter{
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request;

        logger.info{"uri: [${request.method}] [${request.path}] [${request.remoteAddress}]"}

        if(request.headers.isNotEmpty()){
            logger.info{"headers: ${request.headers}"}
        }

        if(request.queryParams.isNotEmpty()){
            logger.info{"query: ${request.queryParams}"}
        }

        val decorator = object : ServerHttpRequestDecorator(request){

            override fun getBody(): Flux<DataBuffer> {
                return super.getBody().doOnNext{ dataBuffer ->
                    ByteArrayOutputStream().use { output ->
                        Channels.newChannel(output).use{channel ->
                            channel.write(dataBuffer.readableByteBuffers().next())
                        }
                        output.toByteArray().let{String(it)}
                    }.let { requestBody ->
                        logger.info { "payload: $requestBody" }

                    }
                }
            }
        }

        return chain.filter(exchange.mutate().request(decorator).build())
    }
}