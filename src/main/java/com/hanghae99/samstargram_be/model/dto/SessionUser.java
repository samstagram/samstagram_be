package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

	private String username;
	private String useremail;
	private String userprofile;

	public SessionUser(Member member){
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
	}
}
