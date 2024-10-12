package dev.fastcampus.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxApplication

fun main(args: Array<String>) {
	runApplication<WebfluxApplication>(*args)
}
