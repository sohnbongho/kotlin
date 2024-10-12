package dev.fastcampus.webflux.repository

import dev.fastcampus.webflux.model.Article
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : CoroutineCrudRepository<Article, Long>{

    // Flow로 받아야 비동기로 받는다.
    suspend fun findAllByTitleContains(title: String): Flow<Article>

}