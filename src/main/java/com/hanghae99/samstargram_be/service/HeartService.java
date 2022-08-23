package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Heart;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.IsLikeRequestDto;
import com.hanghae99.samstargram_be.model.dto.IsLikeResponseDto;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import com.hanghae99.samstargram_be.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class HeartService {
	private final HeartRepository heartRepository;
	private final ArticleRepository articleRepository;
	private final MemberService memberService;

	@Transactional
	public IsLikeResponseDto articleHeart(IsLikeRequestDto isLikeRequestDto) {
		Member member = memberService.getSinginUser();
		Article article = articleRepository.findById(isLikeRequestDto.getArticlesId())
				.orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
		IsLikeResponseDto isLike = new IsLikeResponseDto(article);

		if (isLikeRequestDto.getIsLike()){
			Heart heart = new Heart(member,article);
			member.addHeart(heart);
			article.addHeart(heart);
			heartRepository.save(heart);
			isLike.setIsLike(isLikeRequestDto.getIsLike());
			isLike.setLikeCnt(article.getHeartList().size());
			return isLike;
		}else {
			Heart heart = heartRepository.findByMemberAndArticle(member, article);
			member.removeHeart(heart);
			article.removeHeart(heart);
			heartRepository.delete(heart);
			isLike.setIsLike(isLikeRequestDto.getIsLike());
			isLike.setLikeCnt(article.getHeartList().size());
			return isLike;
		}
	}
}
