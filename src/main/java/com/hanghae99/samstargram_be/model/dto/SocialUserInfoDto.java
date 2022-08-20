package com.hanghae99.samstargram_be.model.dto;


import com.hanghae99.samstargram_be.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfoDto {

	private String socialId;
	private String username;
	private String useremail;
	private String userprofile;

	public SocialUserInfoDto(Member member) {
		this.socialId = member.getSocialId();
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.useremail = member.getUserprofile();
	}
}