package com.hanghae99.samstargram_be.controller;

import com.hanghae99.samstargram_be.model.Comment;
import com.hanghae99.samstargram_be.model.dto.CommentListDto;
import com.hanghae99.samstargram_be.model.dto.CommentRequestDto;
import com.hanghae99.samstargram_be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", exposedHeaders = "*", allowedHeaders = "*")
@RestController
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/{articleId}")
	public CommentListDto readCommentList(@PathVariable Long articleId){
		return commentService.readCommentList(articleId);
	}

	@PostMapping("/{articleId}")
	public Comment createComment(@PathVariable Long articleId, @RequestBody CommentRequestDto commentRequestDto){
		return commentService.createComment(articleId,commentRequestDto);
	}

}
