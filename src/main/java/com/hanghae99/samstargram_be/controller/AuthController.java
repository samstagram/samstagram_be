package com.hanghae99.samstargram_be.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.samstargram_be.model.dto.MemberRequestDto;
import com.hanghae99.samstargram_be.model.dto.MemberResponseDto;
import com.hanghae99.samstargram_be.model.dto.TokenDto;
import com.hanghae99.samstargram_be.model.dto.TokenRequestDto;
import com.hanghae99.samstargram_be.service.AuthService;
import com.hanghae99.samstargram_be.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//**
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final CustomOAuth2UserService customOAuth2UserService;

	@PostMapping("/signup")
	public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto memberRequestDto) {
		return ResponseEntity.ok(authService.signup(memberRequestDto));
	}

	@PostMapping("/login")
	public TokenDto login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse httpServletResponse) {
		TokenDto tokenDto = authService.login(memberRequestDto);
		httpServletResponse.setHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
		Cookie jwt = new Cookie("Bearer ",  tokenDto.getAccessToken());
		jwt.setMaxAge(1000 * 60 * 60 * 12);
		httpServletResponse.addCookie(jwt);
		return tokenDto;
	}

	@PostMapping("/logout")
	private String logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		Cookie jwt = new Cookie("jwt", null);
		jwt.setMaxAge(0);
		httpServletResponse.addCookie(jwt);
		return "로그아웃";
	}

	@PostMapping("/reissue")
	public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
		return ResponseEntity.ok(authService.reissue(tokenRequestDto));
	}

}
