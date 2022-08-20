package com.hanghae99.samstargram_be.controller;


import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.dto.ArticleRequestDto;
import com.hanghae99.samstargram_be.model.dto.ArticleResponseDto;
import com.hanghae99.samstargram_be.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public Article createArticle(@RequestBody ArticleRequestDto articleRequestDto){
        return articleService.createArticle(articleRequestDto);
    }

    @GetMapping("/api/articles")
    public List<ArticleResponseDto> getArticle(){
        return articleService.getArticle();
    }

    @PutMapping("/api/articles/{articleId}")
    public Long update(@PathVariable Long articleId,@RequestBody ArticleRequestDto articleRequestDto){
        articleService.update(articleId,articleRequestDto);
        return articleId;
    }
    @DeleteMapping("/api/articles/{articleId}")
    public Long delete(@PathVariable Long articleId){
        articleService.deleteArticle(articleId);
        return articleId;
    }


}
