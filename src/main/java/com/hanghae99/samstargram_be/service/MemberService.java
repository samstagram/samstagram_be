package com.hanghae99.samstargram_be.service;


import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.MemberResponseDto;
import com.hanghae99.samstargram_be.model.dto.MemberResponseListDto;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import com.hanghae99.samstargram_be.security.SecurityUtil;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
				.orElseThrow(()-> new IllegalArgumentException("유저를 찾지 못했습니다."));
	}


	public List<MemberResponseListDto> readMemberList() {
		Member singinUser = getSinginUser();
		List<Member> memberList = memberRepository.findAll();
		List<MemberResponseListDto> memberResponseListDtoList = new ArrayList<>();

		for(Member member : memberList){
			if (!member.getMemberId().equals(getSigninUserId())){
				MemberResponseListDto memberResponseListDto = new MemberResponseListDto(member);
				if(singinUser.getFollowingList().contains(member.getMemberId())){
					memberResponseListDto.setFollow(true);
				}
				memberResponseListDtoList.add(memberResponseListDto);
			}
		}
		return memberResponseListDtoList;
	}

	public MemberResponseListDto followMember(Long membersId) {
		Member singinUser = getSinginUser();
		Member target = memberRepository.findById(membersId).orElseThrow(() -> new IllegalArgumentException("유저를 찾지 못했습니다."));

		if(singinUser.getMemberId() != target.getMemberId()){
			if(!singinUser.getFollowingList().contains(target.getMemberId())){
				singinUser.addFolloing(target.getMemberId());
				target.addFollower(singinUser.getMemberId());

			}else {
				singinUser.removeFolloing(target.getMemberId());
				target.removeFollower(singinUser.getMemberId());
			}

			memberRepository.save(singinUser);
			memberRepository.save(target);

			MemberResponseListDto memberResponseListDto = new MemberResponseListDto(target);
			memberResponseListDto.setFollow(singinUser.getFollowingList().contains(target.getMemberId()));
			return memberResponseListDto;
		}else {
			return new MemberResponseListDto(singinUser);
		}
	}
}
