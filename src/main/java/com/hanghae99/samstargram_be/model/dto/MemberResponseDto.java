package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//**
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
	private String username;

	public static MemberResponseDto of(Member member) {
		return new MemberResponseDto(member.getUsername());
	}
}