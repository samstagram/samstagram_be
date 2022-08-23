package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
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

  public List<ArticleResponseDto> readSearchArticleList(String hashtag) {
    List<Article> allByHashtagList = articleRepository.findAll();
    System.out.println("해시태그~"+hashtag);
    List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();

    for (Article article : allByHashtagList){
      for (String tag : article.getHashtagList()){
        if(tag.contains(hashtag)){
          articleResponseDtoList.add(new ArticleResponseDto(article));
        }
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

    String[] content = {"데이터들의 값을 알고 있을 때 사용하면 편리하다 #데이터 #값 #편리 #데이트 #홀리데이",
        "인덱스 넘버는 0부터 시작하기 때문에, 위의 경우 인덱스의 범위는 0부터 4가 된다 #인덱스 #편리 #0 #시작 #범위 #배열",
        "배열의 크기를 수정하였을 때, 일일이 조건식을 변경하기보다는 #배열 #크기 #수정 #조건식 #범위",
        "자바에서는 배열을 간단히 초기화 할 수 있는 방법을 제공한다.#배열 #자바 #수정 #편리함 #초기화 #방법",
        "배열을 한 번 생성하면 그 길이를 변경할 수 없으므로 #배열 #길이 # #자바 #변경 #길이 #크기 #범위",
        "웹브라우저 위에서 자바스크립트를 이용해서 구글로 로그인하기 기능을 구현하는 방법에 대한 수업 #자바스크립트 #수업 #구글 #로그인",
        "관련된 지식의 지도입니다. 지도를 참고해서 스스로 학습 경로를 탐험해보세요. #수업 #방법 #지식 #지도 #구글",
        "구글 소셜로그인을 구현해봤는데요, 구글 소셜 로그인을 하려고 하면 인증 페이지가 다 한글로 구성이 되어 있습니다. #구글 #로그인 #소셜로그인 #인증",
        "구글의 인증 API를 이용하면 보다 자유롭게 인증 시스템을 제어하는 것이 가능합니다. 이번 시간에는 그 방법을 살펴보겠습니다. #인증 #구글 #로그인 #소셜로그인 #방법",
        "분명 같은 구글에서 제공하는건데(크롬과 소스가) 이상하게 오히려 크롬으로 실행하면 안되고 엣지로 실행하면 되네요.#구글 #엣지 #크롬 # #실행 #방법"};

    String[] img = {"https://cdn.pixabay.com/photo/2017/12/15/13/51/polynesia-3021072_960_720.jpg","https://cdn.pixabay.com/photo/2018/03/12/20/07/maldives-3220702__340.jpg",
                    "https://cdn.pixabay.com/photo/2016/11/21/17/44/arches-national-park-1846759__340.jpg","https://cdn.pixabay.com/photo/2019/10/01/21/42/caravansary-4519442__340.jpg",
                    "https://cdn.pixabay.com/photo/2019/07/14/10/48/vineyards-4336787__340.jpg","https://cdn.pixabay.com/photo/2020/06/02/06/29/ryanair-5249631__340.jpg",
                    "https://cdn.pixabay.com/photo/2015/11/27/20/28/cathedral-1066314__340.jpg","https://cdn.pixabay.com/photo/2018/08/19/10/16/nature-3616194__340.jpg",
                    "https://cdn.pixabay.com/photo/2017/04/06/11/24/fashion-2208045__340.jpg","https://cdn.pixabay.com/photo/2020/02/28/21/15/space-4888643__340.jpg",
                    "https://cdn.pixabay.com/photo/2014/08/12/00/01/santorini-416136__340.jpg","https://cdn.pixabay.com/photo/2020/02/28/21/15/space-4888643__340.jpg",
                    "https://cdn.pixabay.com/photo/2015/01/28/23/10/mosque-615415__340.jpg","https://cdn.pixabay.com/photo/2014/09/21/17/56/mountaineering-455338__340.jpg",
                    "https://cdn.pixabay.com/photo/2014/10/23/18/56/tiger-500118__340.jpg","https://cdn.pixabay.com/photo/2014/05/08/15/37/coast-340348__340.jpg",
                    "https://cdn.pixabay.com/photo/2016/11/22/19/25/man-1850181__340.jpg","https://cdn.pixabay.com/photo/2014/08/12/00/01/santorini-416135__340.jpg",
                    "https://cdn.pixabay.com/photo/2016/05/24/18/49/suitcase-1412996__340.jpg","https://cdn.pixabay.com/photo/2019/04/12/11/46/antelope-4121962__340.jpg"};


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
        comment2.setContent(cmt1[(int)(Math.random() * 6)]);
        comment3.setContent(cmt1[(int)(Math.random() * 6)]);

        article.setContent(content[(int)(Math.random() * 10)]);

        article.addImage(img[(int)(Math.random() * 20)]);
        article.addImage(img[(int)(Math.random() * 20)]);
        article.addImage(img[(int)(Math.random() * 20)]);

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



















