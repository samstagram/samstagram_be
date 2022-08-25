package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Member;
import lombok.Getter;

@Getter
public class MemberResponseListDto {
	private Long memberId;
	private String username;
	private String useremail;
	private String userprofile;
	private Boolean isFollow = false;

	public MemberResponseListDto(Member member) {
		this.memberId = member.getMemberId();
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
	}

	public void setFollow(Boolean follow) {
		isFollow = follow;
	}
}
