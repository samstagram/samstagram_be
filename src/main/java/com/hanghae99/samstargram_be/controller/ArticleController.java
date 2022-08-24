package com.hanghae99.samstargram_be.controller;


import com.hanghae99.samstargram_be.model.dto.*;
import com.hanghae99.samstargram_be.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*", exposedHeaders = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ArticleResponseDto createArticle(@Valid @RequestPart(value = "dto") ArticleRequestDto articleRequestDto, @RequestPart(required = false) List<MultipartFile> multipartFile) throws IOException {
        return articleService.createArticle(articleRequestDto, multipartFile);
    }

    @GetMapping("")
    public List<ArticleResponseDto> readArticleList(@RequestParam("page") int page, @RequestParam("size") int size){
        page = page-1;
        return articleService.readArticleList(page, size);
    }

    @GetMapping("/{articleId}")
    public ArticleResponseDto readArticle(@PathVariable Long articleId){
        return articleService.readArticle(articleId);
    }

    @GetMapping("/hashtag")
    public List<ArticleSearchDto> readTagArticleList(){
        return articleService.readTagArticleList();
    }

    @GetMapping("/search")
    public Set<ArticleResponseDto> readSearchArticleList(@RequestParam("hashtag") String hashtag){
        return articleService.readSearchArticleList(hashtag);
    }


    @PatchMapping("/{articleId}")
    public Long update(@PathVariable Long articleId,@RequestBody ArticleRequestDto articleRequestDto){
        return articleService.update(articleId,articleRequestDto);
    }

    @PatchMapping("")
    private IsLikeResponseDto articleHeart(@RequestBody IsLikeRequestDto isLikeRequestDto){
        return articleService.articleHeart(isLikeRequestDto);
    }

    @DeleteMapping("/{articleId}")
    public Long delete(@PathVariable Long articleId){
        return articleService.deleteArticle(articleId);
    }

    /*--------------------------------------------------------*/

    @PostMapping("/test/data/samstar")
    public String testArticleData(){
        articleService.testArticleData();
        return "테스트 데이터 생성";
    }
}




















