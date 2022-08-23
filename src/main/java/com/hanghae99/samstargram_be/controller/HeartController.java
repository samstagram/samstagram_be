package com.hanghae99.samstargram_be.controller;

import com.hanghae99.samstargram_be.model.dto.IsLike;
import com.hanghae99.samstargram_be.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class HeartController {
	private final HeartService heartService;

	@PostMapping("/api/article/heart/{articleId}")
	private IsLike articleHeart(@PathVariable Long articleId){
		return heartService.articleHeart(articleId);
	}
}
