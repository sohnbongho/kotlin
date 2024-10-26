package dev.fastcampus.webflux.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

var logger = KotlinLogging.logger{}

@RestController
class AdvancedController {

    @GetMapping("/test/txid")
    suspend fun txtid(){

        logger.debug{"hello txid"}

        delay(100)
        Mono.fromCallable {
            logger.debug { "teactor call!" }
        }.subscribeOn(Schedulers.boundedElastic()).awaitSingle()

        mono {
            logger.debug { "final test" }
        }.awaitSingleOrNull()

        logger.debug{"end"}
    }
}
public fun<T> monoA(
    context: CoroutineContext = MDCContext(),
    block: suspend CoroutineScope.() -> T?
):Mono<T>{
    return kotlinx.coroutines.reactor.mono(context, block)
}

@Aspect
@Component
class AdvancedAspectConfig {

    @Around("""
       @annotation(org.springframework.web.bind.annotation.RequestMapping) 
       @annotation(org.springframework.web.bind.annotation.GetMapping) 
       @annotation(org.springframework.web.bind.annotation.PostMapping)  
       @annotation(org.springframework.web.bind.annotation.PutMapping) 
       @annotation(org.springframework.web.bind.annotation.DeleteMapping) || 
       @annotation(org.springframework.web.bind.annotation.PatchMapping)
    """)
    fun bindMdcContext(jp: ProceedingJoinPoint): Any? {

        var method = (jp.signature as MethodSignature).method

        return if(KotlinDetector.isSuspendingFunction(method)){
            val continuation = jp.args.last() as Continuation<Any>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation<Any>(newContext) { continuation.resume(it)}
            val jpArg = jp.args.dropLast(1) + newContinuation
            jp.proceed(jpArg.toTypedArray())
        }
        else{
            jp.proceed()
        }
    }

}