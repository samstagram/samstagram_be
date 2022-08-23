package com.hanghae99.samstargram_be.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae99.samstargram_be.model.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH시 mm분", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;
	private Long commentsId;
	private Long articlesId;
	private String username;
	private String useremail;
	private String userprofile;
	private String content;

	public CommentResponseDto(Comment comment) {
		this.createdAt = comment.getCreatedAt();
		this.articlesId = comment.getArticle().getArticlesId();
		this.commentsId = comment.getCommentsId();
		this.username = comment.getUsername();
		this.useremail = comment.getUseremail();
		this.userprofile = comment.getUserprofile();
		this.content = comment.getContent();
	}
}
