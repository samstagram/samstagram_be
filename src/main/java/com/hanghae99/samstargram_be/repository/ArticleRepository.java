package com.hanghae99.samstargram_be.repository;

import com.hanghae99.samstargram_be.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {
	@Override
	Page<Article> findAll(Pageable pageable);
}
