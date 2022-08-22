package com.hanghae99.samstargram_be.service;


import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.MemberResponseDto;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import com.hanghae99.samstargram_be.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//**
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public Long getSigninUserId(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			return Long.parseLong(userId);
		}catch (Exception e){
			Optional<Member> byUsername = memberRepository.findByUsername(userId);
			return byUsername.get().getMemberId();
		}
	}
	public Member getSinginUser(){
		return memberRepository.findById(getSigninUserId())
				.orElseThrow(()-> new RuntimeException("유저를 찾지 못했습니다."));
	}
}
