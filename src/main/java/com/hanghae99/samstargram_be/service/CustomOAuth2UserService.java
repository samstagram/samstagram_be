package com.hanghae99.samstargram_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.*;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import com.hanghae99.samstargram_be.security.UserDetailsImpl;
import com.hanghae99.samstargram_be.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

//**/*
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	String googleClientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	String googleClientSecret;
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	String googleRedirectUri;
	private final MemberRepository memberRepository;
	private final HttpSession httpSession;
	private final TokenProvider tokenProvider;

	public MemberResponseDto googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {

		// 1. 인가코드로 엑세스토큰 가져오기
		System.out.println("구글 로그인 1번 접근");
		String accessToken = getAccessToken(code);

//		 2. 엑세스토큰으로 유저정보 가져오기
		System.out.println("구글 로그인 2번 접근");
		SocialUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
//
		// 3. 유저확인 & 회원가입
		System.out.println("구글 로그인 3번 접근");
		Member foundUser = getUser(googleUserInfo);
//
		// 4. 시큐리티 강제 로그인
		System.out.println("구글 로그인 4번 접근");
		Authentication authentication = securityLogin(foundUser);

		// 5. jwt 토큰 발급
		System.out.println("구글 로그인 5번 접근");
		String jwt = jwtToken(authentication, response);

		return dto(foundUser, jwt);
	}


	// 1. 인가코드로 엑세스토큰 가져오기
	private String getAccessToken(String code) throws JsonProcessingException {
		// 헤더에 Content-type 지정
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// 바디에 필요한 정보 담기
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id" , googleClientId);
		body.add("client_secret", googleClientSecret);
		body.add("code", code);
		body.add("redirect_uri", googleRedirectUri);
		body.add("grant_type", "authorization_code");

		System.out.println("코드가 머죵?");
		System.out.println(code);
		System.out.println("코드가 머죵?");

		// POST 요청 보내기
		HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				"https://oauth2.googleapis.com/token",
				HttpMethod.POST,
				googleToken,
				String.class
		);

		// response에서 accessToken 가져오기
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseToken = objectMapper.readTree(responseBody);
		System.out.println("-----1-----");
		System.out.println(responseToken);
		System.out.println("-----1-----");
		return responseToken.get("access_token").asText();
	}


	// 2. 엑세스토큰으로 유저정보 가져오기
	private SocialUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
		// 헤더에 엑세스토큰 담기, Content-type 지정
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		System.out.println("getGoogleUserInfo + 헤더까지는 받음 헤더 : " +headers);  //#

		System.out.println("----2----");
		System.out.println(accessToken);
		System.out.println("----2----");

		// POST 요청 보내기
		HttpEntity<MultiValueMap<String, String>> googleUser = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				"https://openidconnect.googleapis.com/v1/userinfo",
				HttpMethod.POST, googleUser,
				String.class
		);

		// response에서 유저정보 가져오기
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(responseBody);

		System.out.println(jsonNode);

		String socialId = jsonNode.get("sub").asText();
		String useremail = jsonNode.get("email").asText();

		System.out.println("----3----");
		System.out.println(socialId);
		System.out.println(useremail);
		System.out.println("----3----");

		//username 랜덤
		Random rnd = new Random();
		String rdNick="";
		for (int i = 0; i < 8; i++) {
			rdNick += String.valueOf(rnd.nextInt(10));
		}
//		String username = "G" + "_" + rdNick;
		String username = jsonNode.get("name").asText();

		System.out.println("----4----");
		System.out.println(username);
		System.out.println("----4----");

		// 구글에서 이미지 가져오기
		String userprofile = jsonNode.get("picture").asText();
		String googleDefaultImg = "https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/defaultImage.png";
		String defaultImage = "https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/defaultImage.png";
		if (userprofile==null || userprofile.equals(googleDefaultImg))
			userprofile = defaultImage; // 우리 사이트 기본 이미지

		System.out.println("----5----");
		System.out.println(userprofile);
		System.out.println(googleDefaultImg);
		System.out.println(defaultImage);
		System.out.println("----5----");

		return new SocialUserInfoDto(socialId, username, useremail, userprofile);
	}

	// 3. 유저확인 & 회원가입
	private Member getUser(SocialUserInfoDto googleUserInfo) {
		// 다른 소셜로그인이랑 이메일이 겹쳐서 잘못 로그인 될까봐. 다른 사용자인줄 알고 로그인이 된다. 그래서 소셜아이디로 구분해보자
		String googleEmail = googleUserInfo.getUseremail();
		String googleSocialID = googleUserInfo.getSocialId();
		Member googleUser = memberRepository.findBySocialId(googleSocialID)
				.orElse(null);

		if (googleUser == null) {  // 회원가입
			String username = googleUserInfo.getUsername();
			String socialId = googleUserInfo.getSocialId();
			String password = UUID.randomUUID().toString();
			String userprofile = googleUserInfo.getUserprofile();

			System.out.println("--0---");
			System.out.println(socialId);
			System.out.println("---0--");

			googleUser = new Member(googleEmail, username, password, userprofile, socialId);

			System.out.println("여기" + googleUser.getSocialId());
			memberRepository.save(googleUser);
		}

		return googleUser;
	}

	// 4. 시큐리티 강제 로그인
	private Authentication securityLogin(Member findUser) {
		UserDetails userDetails = new UserDetailsImpl(findUser);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//여기까진 평범한 로그인과 같음, 구글 강제로그인 시도까지 함
		return authentication;
	}

	// 5. jwt 토큰 발급
	private String jwtToken(Authentication authentication, HttpServletResponse response) {
		//여기부터 토큰 프론트에 넘기는것
		UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
		TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
		String token = tokenDto.getAccessToken();
		System.out.println(token);
		response.addHeader("Authorization", "Bearer " + token);
		return "Bearer " + token;
	}

	private MemberResponseDto dto(Member member, String jwt){
		return new MemberResponseDto(member, jwt);
	}



	/*------------------------------------책에 나온 내용----------------------------------------------*/

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
				.getUserInfoEndpoint().getUserNameAttributeName();

		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

		Member member = saveOrUpdate(attributes);
		httpSession.setAttribute("member", new SessionUser(member));

		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				attributes.getAttributes(),
				attributes.getNameAttributeKey());
	}


	private Member saveOrUpdate(OAuthAttributes attributes) {
		Member member = memberRepository.findByUseremail(attributes.getUseremail())
				.map(entity->entity.update(attributes.getUsername(), attributes.getUserprofile()))
				.orElse(attributes.toEntity());

		return memberRepository.save(member);
	}
}
