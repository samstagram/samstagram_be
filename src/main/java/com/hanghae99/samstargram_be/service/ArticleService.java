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

    article.setHeartCnt((int)(Math.random() * 8999+1000));
      article.addImage("https://cdn.pixabay.com/photo/2022/08/12/10/27/crows-7381423_960_720.jpg");
      article.addImage("https://cdn.pixabay.com/photo/2022/07/15/20/13/strawberries-7323943_960_720.jpg");
      article.addImage("https://cdn.pixabay.com/photo/2022/07/23/10/48/daisies-7339735_960_720.jpg");
      article.addImage("https://cdn.pixabay.com/photo/2022/08/16/04/52/jewel-7389356_960_720.jpg");

      articleRepository.save(article);
      return article;
  }

  public List<ArticleResponseDto> readArticleList(int page, int size){
    Member member = memberService.getSinginUser();

    Sort.Direction direction = Sort.Direction.DESC;
    Sort sort = Sort.by(direction, "createdAt");
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Article> articleList = articleRepository.findAll(pageable);
    List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
    for (Article article : articleList){

      if(member.getUsername().equals(article.getUsername())){
        article.setMyArticles(true);
      }

      articleResponseDtoList.add(new ArticleResponseDto(article));
    }

      return articleResponseDtoList;
  }

  public ArticleResponseDto readArticle(Long articleId) {
    Article article = articleRepository.findById(articleId)
        .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
    Member member = memberService.getSinginUser();

    ArticleResponseDto articleResponseDto = new ArticleResponseDto(article);

    if(member.getUsername().equals(articleResponseDto.getUsername())){
      articleResponseDto.setMyArticles(true);
    }

   return articleResponseDto;
  }

  @Transactional
  public void update(Long articleId, ArticleRequestDto articleRequestDto){
      Article article = articleRepository.findById(articleId)
              .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
      article.update(articleRequestDto);
  }

  public void deleteArticle (Long articleId){
      Article article = articleRepository.findById(articleId)
              .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
      Member member = memberService.getSinginUser();

      member.removeArticle(article);
      articleRepository.delete(article);
  }
}
