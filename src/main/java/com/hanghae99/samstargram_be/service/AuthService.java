package com.hanghae99.samstargram_be.service;



import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.RefreshToken;
import com.hanghae99.samstargram_be.model.dto.*;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import com.hanghae99.samstargram_be.repository.RefreshTokenRepository;
import com.hanghae99.samstargram_be.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

//**
@Service
@RequiredArgsConstructor
public class AuthService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public MemberNameResponseDto signup(MemberRequestDto memberRequestDto) {
		if(!(Pattern.matches("[a-zA-Z0-9]*$",memberRequestDto.getUsername()) && (memberRequestDto.getUsername().length() > 3 && memberRequestDto.getUsername().length() <13)
				&& Pattern.matches("[a-zA-Z0-9]*$",memberRequestDto.getPassword()) && (memberRequestDto.getPassword().length() > 3 && memberRequestDto.getPassword().length() <33))){
			throw new IllegalArgumentException("닉네임 혹은 비밀번호 조건을 확인해주세요.");
		}
		if (memberRepository.existsByUsername(memberRequestDto.getUsername())) {
			throw new IllegalArgumentException("중복된 닉네임입니다.");
		} else if (!memberRequestDto.getPassword().equals(memberRequestDto.getPassword2()))
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		Member member = memberRequestDto.toMember(passwordEncoder);
		return MemberNameResponseDto.of(memberRepository.save(member));
	}

	@Transactional
	public TokenDto login(MemberRequestDto memberRequestDto) {
		UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

		// 4. RefreshToken 저장
		RefreshToken refreshToken = RefreshToken.builder()
				.key(authentication.getName())
				.value(tokenDto.getRefreshToken())
				.build();

		refreshTokenRepository.save(refreshToken);

		return tokenDto;
	}

	@Transactional
	public TokenDto reissue(TokenRequestDto tokenRequestDto) {
		if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
			throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
		}

		Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

		RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
				.orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

		if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
			throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
		}

		TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

		RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
		refreshTokenRepository.save(newRefreshToken);

		return tokenDto;
	}
}