package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.controller.monoA
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdvancedService {

    @CircuitBreaker(name = "test-circuit", fallbackMethod = "fallback")
    fun unstable(flag: Boolean?) : Mono<String> {
        return monoA {
            if(flag == true)
                throw RuntimeException("test fail!")
            "success"
        }
    }

    fun fallback(e: Throwable) : Mono<String>{
        return monoA {
            "fallback response: ${e.message}"
        }
    }

}