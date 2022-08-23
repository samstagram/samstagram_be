package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Article;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsLikeResponseDto {
	private Long articlesId;
	private Boolean isLike = false;
	private int likeCnt;

	public IsLikeResponseDto(Article article) {
		this.articlesId = article.getArticlesId();
	}
}
