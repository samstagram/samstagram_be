package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.ArticleRequestDto;
import com.hanghae99.samstargram_be.model.dto.ArticleResponseDto;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import com.hanghae99.samstargram_be.repository.HeartRepository;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

 //   private final S3Uploader s3Uploader;
  private final ArticleRepository articleRepository;
  private final HeartRepository heartRepository;
  private final MemberService memberService;
  private final S3Uploader s3Uploader;
  private final MemberRepository memberRepository;

  public ArticleResponseDto createArticle(ArticleRequestDto articleRequestDto, List<MultipartFile> multipartFile) throws IOException {

      Member member = memberService.getSinginUser();

      Article article = new Article(articleRequestDto, member);

    if(multipartFile != null && multipartFile.size() < 5){
      List<String> stringList = s3Uploader.upload(multipartFile, "img");
      article.setImage(stringList);
    }

      article.setMyArticles(true);

    String content = articleRequestDto.getContent();

    List<String> stringList = Arrays.asList(content.split(" ")); // 띄어쓰기 기준으로 자름

    for (String tag : stringList) {
      if (tag.charAt(0) == '#') {
        article.addHashtag(tag);
      }
    }

    member.addArticle(article);

    article.addImage("https://cdn.pixabay.com/photo/2022/06/18/12/40/muenster-7269726_960_720.jpg");
    article.addImage("https://cdn.pixabay.com/photo/2019/12/15/17/10/building-4697597_960_720.jpg");
    article.addImage("https://cdn.pixabay.com/photo/2022/08/12/10/27/crows-7381423_960_720.jpg");

//    article.setHeartCnt((int)(Math.random() * 8999+1000));

      articleRepository.save(article);


      return new ArticleResponseDto(article);
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
        if (heartRepository.findByMemberAndArticle(member, article) != null){
          articleResponseDtoList.add(new ArticleResponseDto(article, true, true));
        }else articleResponseDtoList.add(new ArticleResponseDto(article, true));
      }else {
        if (heartRepository.findByMemberAndArticle(member, article) != null){
          articleResponseDtoList.add(new ArticleResponseDto(article, false, true));
        }else articleResponseDtoList.add(new ArticleResponseDto(article));
      }
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
    if(heartRepository.findByMemberAndArticle(member, article) != null){
      articleResponseDto.setLike(true);
    }

   return articleResponseDto;
  }

  @Transactional
  public Long update(Long articleId, ArticleRequestDto articleRequestDto){
      Article article = articleRepository.findById(articleId)
              .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
    Member member = memberService.getSinginUser();

    if(member.getUsername().equals(article.getUsername())) {
      article.update(articleRequestDto);
      return articleId;
    }else return 0L;
  }

  public Long deleteArticle (Long articleId){
      Article article = articleRepository.findById(articleId)
              .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
      Member member = memberService.getSinginUser();

    if(member.getUsername().equals(article.getUsername())) {
      member.removeArticle(article);
      articleRepository.delete(article);
      return articleId;
    }else return 0L;
  }

  /*--------------------------------------------------------*/

  public void testData() {
    Random random = new Random();
    for(int i=0; i<100; i++){
      Member member = memberRepository.findById(1L)
          .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
      Article article = new Article(member);
      article.addImage("https://cdn.pixabay.com/photo/2022/08/01/10/36/tulips-7357877_960_720.jpg");
      article.addImage("https://cdn.pixabay.com/photo/2022/06/12/22/48/futuristic-7258997_960_720.png");
      article.addImage("https://cdn.pixabay.com/photo/2017/03/12/09/38/cat-2136663_960_720.jpg");
      articleRepository.save(article);
    }
  }
}



















