package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Article;
import lombok.Getter;

import java.util.List;

@Getter
public class ArticleSearchDto {
	private Long articlesId;
	private String username;
	private String useremail;
	private String userprofile;
	private String content;
	private List<String> hashtagList;

	public ArticleSearchDto(Article article) {
		this.articlesId = article.getArticlesId();
		this.username = article.getUsername();
		this.useremail = article.getUseremail();
		this.userprofile = article.getUserprofile();
		this.content = article.getContent();
		this.hashtagList = article.getHashtagList();
	}
}
