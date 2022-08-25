package com.hanghae99.samstargram_be.model.dto;

import lombok.Getter;

@Getter
public class IsLikeRequestDto {
	private Long articlesId;
	private Boolean isLike;
}
