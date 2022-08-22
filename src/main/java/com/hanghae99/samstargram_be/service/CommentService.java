package com.hanghae99.samstargram_be.service;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.model.Comment;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.CommentRequestDto;
import com.hanghae99.samstargram_be.model.dto.CommentResponseDto;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import com.hanghae99.samstargram_be.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final ArticleRepository articleRepository;
	private final MemberService memberService;


	public List<CommentResponseDto> readCommentList(Long articleId) {
		List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
		List<Comment> commentList =commentRepository.findAllByArticle_ArticlesId(articleId);
		for (Comment comment:commentList)
			commentResponseDtoList.add(new CommentResponseDto(comment));
		return commentResponseDtoList;
	}

	public Comment createComment(Long articleId, CommentRequestDto commentRequestDto) {
		Member member = memberService.getSinginUser();

		Article article = articleRepository.findById(articleId)
				.orElseThrow(()-> new IllegalArgumentException("아이디가 없습니다"));

		Comment comment = new Comment(commentRequestDto, article, member);
		member.addComment(comment);
		article.addComment(comment);
		return commentRepository.save(comment);
	}


}
