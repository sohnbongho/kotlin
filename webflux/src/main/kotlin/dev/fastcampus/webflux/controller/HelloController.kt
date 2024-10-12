package dev.fastcampus.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    // Get일때는 @Parma을 쓰지 않아도 인식이 된다(스프링에서 지원)
    @GetMapping("/hello")
    suspend fun index(reqHello: ReqHello) : String {
        return "Hello async ${reqHello}"
    }
}

data class ReqHello(
    val name: String = "",
    val age : Int = 0,
)