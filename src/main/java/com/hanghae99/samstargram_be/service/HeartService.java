package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Heart;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.IsLike;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import com.hanghae99.samstargram_be.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartService {
	private final HeartRepository heartRepository;
	private final ArticleRepository articleRepository;
	private final MemberService memberService;

	public IsLike articleHeart(Long articleId) {
		Member member = memberService.getSinginUser();
		Article article = articleRepository.findById(articleId)
				.orElseThrow(()-> new NullPointerException("해당 게시물이 존재하지 않습니다."));
		IsLike isLike = new IsLike();

		if (heartRepository.findByMemberAndArticle(member, article) == null){
			Heart heart = new Heart(member,article);
			member.addHeart(heart);
			article.addHeart(heart);
			heartRepository.save(heart);
			isLike.setIsLike(true);
			return isLike;
		}else {
			Heart heart = heartRepository.findByMemberAndArticle(member, article);
			member.removeHeart(heart);
			article.removeHeart(heart);
			heartRepository.delete(heart);
			isLike.setIsLike(false);
			return isLike;
		}

	}
}
