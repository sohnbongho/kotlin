package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.repository.ArticleRepository
import dev.fastcampus.webflux.service.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFieldsExcept
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

val logger = KotlinLogging.logger{}

@SpringBootTest
class ArticleServiceTest(
    private val service: ArticleService,
    private val repository: ArticleRepository,
    private val txManager: TransactionalOperator
): StringSpec ({

    runBlocking {
        repository.deleteAll()
    }

    suspend fun<T> rollback(f:suspend (ReactiveTransaction)->T ):T {
        txManager.executeAndAwait { tx->
            tx.setRollbackOnly()
            f.invoke(tx)
        }
    }


    "create & get"{
        rollback {
            val req = ReqCreate("title 1", "blabl1 1", 1234)
            val id = service.create(req).id
            service.get(id).let {
                req.title shouldBe  it.title
                req.body shouldBe it.body
                req.authorId shouldBe it.authorId
            }
        }
    }
    "get - not found"{
        assertThrows<NotFoundException> { service.get(99999999) }
        runCatching {
            logger.debug { runBlocking { ">> count : ${repository.count()}" } }
        }
    }
    "get all " {

        rollback{
            service.create(ReqCreate("title1", "blabl1 1", 1234))
            service.create(ReqCreate("title2", "blabl1 1", 1234))
            service.create(ReqCreate("title matched", "blabl1 1", 1234))

            service.getAll().count () shouldBe 3
            service.getAll().count ("matched") shouldBe 1
            service.getAll().count ("none") shouldBe 0
        }
    }

    "update" {
        rollback {
            val req = ReqCreate("title 1", "blabl1 1", 9999)
            val new = service.create(req)
            service.update(new.id, ReqUpdate(title = "updated"))
            service.get(new.id).authorId shouldBe 9999
        }
    }

    "delete" {
        rollback {
            val prevCnt = repository.count()
            val created = service.create(ReqCreate("title 1", "blabl1 1", 1234))

            repository.count() shouldBe prevCnt + 1
            service.delete(created.id)
            repository.count() shouldBe prevCnt
        }
    }
})
