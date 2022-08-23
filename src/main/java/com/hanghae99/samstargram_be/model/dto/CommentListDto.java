package com.hanghae99.samstargram_be.model.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentListDto {
	private Long articlesId;
	private List<CommentResponseDto> commentsList = new ArrayList<>();

	public CommentListDto(List<CommentResponseDto> commentList) {
		this.articlesId = commentList.get(0).getArticlesId();
		this.commentsList = commentList;
	}
}
