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
	private Long memberId;
	private String username;
	private String useremail;
	private String userprofile;
	private String socialId;
	private int followingCnt;
	private int followersCnt;

	private String jwt;


	public MemberResponseDto(Member member, String jwt) {
		this.memberId = member.getMemberId();
		this.socialId = member.getSocialId();
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
		this.followingCnt = member.getFollowingCnt();
		this.followersCnt = member.getFollowersCnt();
		this.jwt = jwt;
	}

	public MemberResponseDto(Member member) {
		this.memberId = member.getMemberId();
		this.socialId = member.getSocialId();
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
		this.followingCnt = member.getFollowingCnt();
		this.followersCnt = member.getFollowersCnt();
	}
}