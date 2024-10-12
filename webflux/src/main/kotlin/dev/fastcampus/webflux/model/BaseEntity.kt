package dev.fastcampus.webflux.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

open class BaseEntity(
    @CreatedDate
    var createdat: LocalDateTime? = null,
    @LastModifiedDate
    var updatedat: LocalDateTime? = null,
) {
    override fun toString(): String {
        return "createdat=$createdat, updatedat=$updatedat"
    }
}