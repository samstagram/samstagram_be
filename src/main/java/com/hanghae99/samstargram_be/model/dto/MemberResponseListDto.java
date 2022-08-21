package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Member;

public class MemberResponseListDto {
	private Long memberId;
	private String username;
	private String useremail;
	private String userprofile;
	private Boolean isFollow = false;

	public MemberResponseListDto(Member member) {
		this.memberId = memberId;
		this.username = username;
		this.useremail = useremail;
		this.userprofile = userprofile;
		this.isFollow = isFollow;
	}
}
