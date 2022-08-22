package com.hanghae99.samstargram_be.repository;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Heart;
import com.hanghae99.samstargram_be.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart,Long> {
	Heart findByMemberAndArticle(Member member, Article article);
}
