package com.hanghae99.samstargram_be.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
import com.hanghae99.samstargram_be.model.Heart;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ArticleResponseDto {
    private Long articlesId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH시 mm분", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String username;
    private String useremail;
    private String userprofile;
    private String content;
    private List<String> image;
    private int commentCnt;
    private int likeCnt;
    private List<String> hashtagList;
    private Boolean isLike = false;
    private Boolean isMyArticles = false;


    public ArticleResponseDto(Article article){
        this.articlesId = article.getArticlesId();
        this.createdAt = article.getCreatedAt();
        this.username = article.getUsername();
        this.useremail = article.getUseremail();
        this.userprofile = article.getUserprofile();
        this.content = article.getContent();
        this.image = article.getImage();
        this.commentCnt = article.getCommentList().size();
//        this.likeCnt = article.getHeartList().size();
        this.likeCnt = article.getLikeCnt();
        this.hashtagList = article.getHashtagList();
    }

    public ArticleResponseDto(Article article, Boolean isMyArticles){
        this.articlesId = article.getArticlesId();
        this.createdAt = article.getCreatedAt();
        this.username = article.getUsername();
        this.useremail = article.getUseremail();
        this.userprofile = article.getUserprofile();
        this.content = article.getContent();
        this.image = article.getImage();
        this.commentCnt = article.getCommentList().size();
//        this.likeCnt = article.getHeartList().size();
        this.likeCnt = article.getLikeCnt();
        this.hashtagList = article.getHashtagList();
        this.isMyArticles = isMyArticles;
    }
    public ArticleResponseDto(Article article, Boolean isMyArticles, Boolean isLike){
        this.articlesId = article.getArticlesId();
        this.createdAt = article.getCreatedAt();
        this.username = article.getUsername();
        this.useremail = article.getUseremail();
        this.userprofile = article.getUserprofile();
        this.content = article.getContent();
        this.image = article.getImage();
        this.commentCnt = article.getCommentList().size();
//        this.likeCnt = article.getHeartList().size();
        this.likeCnt = article.getLikeCnt();
        this.hashtagList = article.getHashtagList();
        this.isMyArticles = isMyArticles;
        this.isLike = isLike;
    }

    public void setLike(Boolean like) {
        isLike = like;
    }

    public void setMyArticles(Boolean myArticles) {
        isMyArticles = myArticles;
    }
}
