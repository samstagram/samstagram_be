package com.hanghae99.samstargram_be.model.dto;


import com.hanghae99.samstargram_be.model.Authority;
import com.hanghae99.samstargram_be.model.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;


//**
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

	private String username;
	private String password;
	private String password2;
	private String useremail;
	private String userprofile;

	public Member toMember(PasswordEncoder passwordEncoder) {
		return Member.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.useremail("G-"+(int)(Math.random() * 8999999+1000000)+"@gmail.com")
				.userprofile("https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/default_profile.png")
				.authority(Authority.ROLE_USER)
				.build();
	}

	public UsernamePasswordAuthenticationToken toAuthentication() {
		return new UsernamePasswordAuthenticationToken(username, password);
	}
}
