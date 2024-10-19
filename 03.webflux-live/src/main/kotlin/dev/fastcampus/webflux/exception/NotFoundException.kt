package dev.fastcampus.webflux.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String? = null, cause: Throwable? = null): RuntimeException(message, cause)
