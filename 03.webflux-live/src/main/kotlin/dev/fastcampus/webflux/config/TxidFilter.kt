package dev.fastcampus.webflux.config

import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import java.util.UUID

const val KEY_TXID = "txid"

@Component
@Order(1)
class TxidFilter: WebFilter {

    init {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(
            KEY_TXID,
            {MDC.get(KEY_TXID)},
            {vaule -> MDC.put(KEY_TXID, vaule)},
            {MDC.remove(KEY_TXID)}
        )

    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val uuid = exchange.request.headers["x-txid"]?.firstOrNull() ?: "${UUID.randomUUID()}"

        MDC.put(KEY_TXID, uuid)
        return chain.filter(exchange).contextWrite { ctx ->
            ctx.put(KEY_TXID, uuid)
        }.doOnError {
            exchange.request.txid = uuid
        }.doFinally {
            MDC.remove(KEY_TXID)
        }
    }
}