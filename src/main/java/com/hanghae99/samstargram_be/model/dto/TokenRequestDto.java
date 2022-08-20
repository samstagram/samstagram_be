package com.hanghae99.samstargram_be.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//**
@Getter
@NoArgsConstructor
public class TokenRequestDto {
	private String accessToken;
	private String refreshToken;
}
