package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.ArticleRequestDto;
import com.hanghae99.samstargram_be.model.dto.ArticleResponseDto;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

 //   private final S3Uploader s3Uploader;
    private final ArticleRepository articleRepository;

    private final MemberService memberService;

    public Article createArticle(ArticleRequestDto articleRequestDto){
        Member member = memberService.getSinginUser();
        Article article = new Article(articleRequestDto,member);
        member.addArticle(article);
        articleRepository.save(article);
        return article;
    }

    public List<ArticleResponseDto> getArticle(){
        List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
        List<Article> article = articleRepository.findAll();
        for (Article article1 : article)
            articleResponseDtoList.add(new ArticleResponseDto(article1));
        return articleResponseDtoList;
    }

    @Transactional
    public void update(Long articleId, ArticleRequestDto articleRequestDto){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()-> new IllegalArgumentException("아이디가 없음"));
        article.update(articleRequestDto);
    }

    public void deleteArticle (Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 없음"));
        Member member = memberService.getSinginUser();

        member.removeArticle(article);
        articleRepository.delete(article);
    }

}
