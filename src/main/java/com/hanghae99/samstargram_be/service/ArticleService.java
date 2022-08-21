package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.ArticleRequestDto;
import com.hanghae99.samstargram_be.model.dto.ArticleResponseDto;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Article article = new Article(articleRequestDto, member);
        member.addArticle(article);
        articleRepository.save(article);
        return article;
    }

    public List<ArticleResponseDto> readArticleList(int page, int size){
      Sort.Direction direction = Sort.Direction.DESC;
      Sort sort = Sort.by(direction, "creatAt");
      Pageable pageable = PageRequest.of(page, size, sort);
      Page<Article> articleList = articleRepository.findAll(pageable);
      List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
      for (Article article : articleList)
            articleResponseDtoList.add(new ArticleResponseDto(article));
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
