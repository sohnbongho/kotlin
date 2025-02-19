package dev.fastcampus.mvc.repository

import dev.fastcampus.mvc.model.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: JpaRepository<Article, Long> {
    fun findAllByTitleContains(title: String): List<Article>
}