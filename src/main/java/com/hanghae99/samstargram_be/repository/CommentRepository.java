package com.hanghae99.samstargram_be.repository;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllByArticle_ArticlesId(Long articleId);
}
