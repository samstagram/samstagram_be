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
    private int commentCnt;
    private int likeCnt;
    private List<String> hashtagList;
    private Boolean isLike = false;
    private Boolean isMyArticles = false;

    public ArticleResponseDto(Article article){
        this.articlesId = article.getId();
        this.createdAt = article.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        this.username = article.getUsername();
        this.useremail = article.getUseremail();
        this.userprofile = article.getUserprofile();
        this.content = article.getContent();
        this.image = article.getImageList();
        this.commentCnt = article.getCommentList().size();
        this.likeCnt = article.getHeartList().size();
        this.hashtagList = article.getHashtagList();
    }

    public void setLike(Boolean like) {
        isLike = like;
    }

    public void setMyArticles(Boolean myArticles) {
        isMyArticles = myArticles;
    }
}
