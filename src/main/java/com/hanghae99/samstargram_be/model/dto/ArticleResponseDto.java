package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
import com.hanghae99.samstargram_be.model.Heart;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ArticleResponseDto {
    private Long articlesId;
    private String createdAt;
    private String username;
    private String useremail;
    private String userprofile;
    private String content;
    private List<String> image;
    private List<Comment> comment;
    private List<Heart> heartList;
    private int heartCnt;
    //private List<String> hashtagList;
    private Boolean isLike;
    private Boolean isMyArticles;

    public ArticleResponseDto(Article article){
        this.articlesId = article.getId();
        this.createdAt = article.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        this.username = article.getUsername();
        this.useremail = article.getUseremail();
        this.userprofile = article.getUserprofile();
        this.content = article.getContent();
        this.image = article.getImage();
        this.comment = article.getCommentList();
        this.heartList = article.getHeartList();
        this.heartCnt = article.getHeartCnt();
        //this.hashtagList = article
        this.isLike = article.getIsLike();
        this.isMyArticles = article.getIsMyArticles();
    }
}
