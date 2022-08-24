package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.*;
import com.hanghae99.samstargram_be.model.dto.*;
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

    String[] stringList = content.split(" "); // 띄어쓰기 기준으로 자름

    for (String tag : stringList) {
      if (tag.charAt(0) == '#' && tag.length() > 1) {
        article.addHashtag(tag);
      }
    }

    member.addArticle(article);

//    article.setHeartCnt((int)(Math.random() * 8999+1000));

      articleRepository.save(article);


       ArticleResponseDto articleResponseDto = new ArticleResponseDto(article);
       articleResponseDto.setMyArticles(true);
    return articleResponseDto;
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

  public List<ArticleSearchDto> readTagArticleList() {
    List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();
    List<ArticleSearchDto> articleSearchDtoList = new ArrayList<>();

    for (Article article : articleList){
      articleSearchDtoList.add(new ArticleSearchDto(article));
    }
    return articleSearchDtoList;
  }

  public Set<ArticleResponseDto> readSearchArticleList(String hashtag) {
    List<Article> allByHashtagList = articleRepository.findAll();
    System.out.println("해시태그~"+hashtag);
    Set<ArticleResponseDto> articleResponseDtoList = new HashSet<>();

    for (Article article : allByHashtagList){
      for (String tag : article.getHashtagList()){
        if(tag.contains(hashtag)){
          articleResponseDtoList.add(new ArticleResponseDto(article));}
        System.out.println(article.getArticlesId());
      }
    }
    return articleResponseDtoList;
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

  @Transactional
  public IsLikeResponseDto articleHeart(IsLikeRequestDto isLikeRequestDto) {

    Member member = memberService.getSinginUser();

    Article article = articleRepository.findById(isLikeRequestDto.getArticlesId())
        .orElseThrow(()-> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

    IsLikeResponseDto isLike = new IsLikeResponseDto(article);

    Heart heart;
    if (isLikeRequestDto.getIsLike()){
      heart = new Heart(member, article);
      member.addHeart(heart);
      article.addHeart(heart);
      heartRepository.save(heart);
    }else {
      heart = heartRepository.findByMemberAndArticle(member, article);
      member.removeHeart(heart);
      article.removeHeart(heart);
      heartRepository.delete(heart);
    }
    isLike.setIsLike(isLikeRequestDto.getIsLike());
    isLike.setLikeCnt(article.getHeartList().size());
    return isLike;
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


  public void testArticleData() {

    FakeData fakeData = new FakeData();

      List<String> tagList = Arrays.asList(fakeData.getTag().split(" "));

      for(int i=0; i<20; i++){
        Member member = memberRepository.findById((long)(Math.random() * 10+1))
            .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member member2 = memberRepository.findById((long)(Math.random() * 10+1))
            .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member member3 = memberRepository.findById((long)(Math.random() * 10+1))
            .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member member4 = memberRepository.findById((long)(Math.random() * 10+1))
            .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member member5 = memberRepository.findById((long)(Math.random() * 10+1))
            .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Article article = new Article(member);

        String[] cmt1 = {"퍼가요~♡","@"+member2.getUsername()+" 이거 봐봐!!","비밀 댓글 입니다.","좌표 찍고 갑니다.","성지순례 왔습니다.","ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ"};
        String[] cmt3 = {"퍼가요~♡","@"+member2.getUsername()+" 이거 봐봐!!","비밀 댓글 입니다.","좌표 찍고 갑니다.","성지순례 왔습니다.","윗댓 빠놀이 지겹네요"};
        String[] cmt2 = {"1빠", "무플 방지 위원회"};

        Comment comment1 = new Comment(article, member3);
        Comment comment2 = new Comment(article, member4);
        Comment comment3 = new Comment(article, member5);

        comment1.setContent(cmt2[(int)(Math.random() * 2)]);
        comment2.setContent(cmt3[(int)(Math.random() * 6)]);
        comment3.setContent(cmt1[(int)(Math.random() * 6)]);

        article.setContent(fakeData.getContent()[(int)(Math.random() * 10)]+" "+tagList.get((int)(Math.random() * 350))+" "+tagList.get((int)(Math.random() * 350))+" "+tagList.get((int)(Math.random() * 350))+" "+tagList.get((int)(Math.random() * 350))+" "+tagList.get((int)(Math.random() * 350)));

        article.addImage(fakeData.getImg()[(int)(Math.random() * 20)]);
        article.addImage(fakeData.getImg()[(int)(Math.random() * 20)]);
        article.addImage(fakeData.getImg()[(int)(Math.random() * 20)]);

        article.addComment(comment1);
        article.addComment(comment2);
        article.addComment(comment3);

        String[] stringList = article.getContent().split(" "); // 띄어쓰기 기준으로 자름

        for (String tag : stringList) {
          if (tag.charAt(0) == '#' && tag.length() > 1) {
            article.addHashtag(tag);
          }
        }

        articleRepository.save(article);
    }
  }


}












