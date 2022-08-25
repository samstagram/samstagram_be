package com.hanghae99.samstargram_be.controller;

import com.hanghae99.samstargram_be.model.dto.MemberResponseDto;
import com.hanghae99.samstargram_be.model.dto.MemberResponseListDto;
import com.hanghae99.samstargram_be.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", exposedHeaders = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	@GetMapping("/profiles")
	public MemberResponseDto readProfiles(){
		return new MemberResponseDto(memberService.getSinginUser());
	}

	@GetMapping("/members")
	public List<MemberResponseListDto> readMemberList(){
		return memberService.readMemberList();
	}

	@PostMapping("/members/{membersId}")
	public MemberResponseListDto followMember(@PathVariable Long membersId){
		return memberService.followMember(membersId);
	}
}
