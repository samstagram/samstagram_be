package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
import com.hanghae99.samstargram_be.model.Heart;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ArticleResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private String username;
    private String useremail;
    private String userprofile;
    private String content;
    private List<String> image;
    private List<Comment> comment;
    private List<Heart> heartList;
    private Long heartCnt;
    //private List<String> hashtagList;
    private Boolean isLike;
    private Boolean isMyArticles;

    public ArticleResponseDto(Article article){
        this.id = article.getId();
        //this.createdAt = article.getcreatedAt;
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
