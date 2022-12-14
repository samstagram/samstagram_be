package com.hanghae99.samstargram_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.dto.*;
import com.hanghae99.samstargram_be.repository.GcodeRepository;
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
	private final GcodeRepository gcodeRepository;
	private final HttpSession httpSession;
	private final TokenProvider tokenProvider;

	public Boolean googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {

		if(!gcodeRepository.existsByGcode(code)){
			System.out.println("?????? ????????? 1??? ??????");
			String accessToken = getAccessToken(code);

//		 2. ????????????????????? ???????????? ????????????
			System.out.println("?????? ????????? 2??? ??????");
			SocialUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
//
			// 3. ???????????? & ????????????
			System.out.println("?????? ????????? 3??? ??????");
			Member foundUser = getUser(googleUserInfo);
//
			// 4. ???????????? ?????? ?????????
			System.out.println("?????? ????????? 4??? ??????");
			Authentication authentication = securityLogin(foundUser);

			// 5. jwt ?????? ??????
			System.out.println("?????? ????????? 5??? ??????");
			String jwt = jwtToken(authentication, response);
			return true;
		}
		return true;
	}


	// 1. ??????????????? ??????????????? ????????????
	private String getAccessToken(String code) throws JsonProcessingException {
		System.out.println("13::" + code);

		// ????????? Content-type ??????
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// ????????? ????????? ?????? ??????
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id" , googleClientId);
		body.add("client_secret", googleClientSecret);
		body.add("code", code);
		body.add("redirect_uri", googleRedirectUri);
		body.add("grant_type", "authorization_code");

		System.out.println("-------??????--------");
		System.out.println("?????? Content-type: "+headers.get("Content-type"));
		System.out.println("?????? client_id: "+body.get("client_id"));
		System.out.println("?????? client_secret: "+body.get("client_secret"));
		System.out.println("?????? code: "+body.get("code"));
		System.out.println("?????? redirect_uri: "+body.get("redirect_uri"));
		System.out.println("?????? grant_type: "+body.get("grant_type"));

		// POST ?????? ?????????
		HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				"https://oauth2.googleapis.com/token",
				HttpMethod.POST,
				googleToken,
				String.class
		);

		// response?????? accessToken ????????????
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseToken = objectMapper.readTree(responseBody);

		System.out.println("?????? : "+responseToken);

		return responseToken.get("access_token").asText();
	}


	// 2. ????????????????????? ???????????? ????????????
	private SocialUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
		// ????????? ??????????????? ??????, Content-type ??????
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		System.out.println("getGoogleUserInfo + ??????????????? ?????? ?????? : " +headers);  //#


		// POST ?????? ?????????
		HttpEntity<MultiValueMap<String, String>> googleUser = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				"https://openidconnect.googleapis.com/v1/userinfo",
				HttpMethod.POST, googleUser,
				String.class
		);

		// response?????? ???????????? ????????????
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(responseBody);

		System.out.println(jsonNode);

		String socialId = jsonNode.get("sub").asText();
		String useremail = jsonNode.get("email").asText();

//		String username = "G" + "_" + rdNick;
		String username = jsonNode.get("name").asText();

		// ???????????? ????????? ????????????
		String userprofile = jsonNode.get("picture").asText();
		String googleDefaultImg = "https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/defaultImage.png";
		String defaultImage = "https://hanghae99-8d-tm.s3.ap-northeast-2.amazonaws.com/defaultImage.png";
		if (userprofile==null || userprofile.equals(googleDefaultImg))
			userprofile = defaultImage; // ?????? ????????? ?????? ?????????

		return new SocialUserInfoDto(socialId, username, useremail, userprofile);
	}

	// 3. ???????????? & ????????????
	private Member getUser(SocialUserInfoDto googleUserInfo) {
		// ?????? ????????????????????? ???????????? ????????? ?????? ????????? ?????????. ?????? ??????????????? ?????? ???????????? ??????. ????????? ?????????????????? ???????????????
		String googleEmail = googleUserInfo.getUseremail();
		String googleSocialID = googleUserInfo.getSocialId();
		Member googleUser = memberRepository.findBySocialId(googleSocialID)
				.orElse(null);

		if (googleUser == null) {  // ????????????
			String username = googleUserInfo.getUsername();
			String socialId = googleUserInfo.getSocialId();
			String password = UUID.randomUUID().toString();
			String userprofile = googleUserInfo.getUserprofile();

			googleUser = new Member(googleEmail, username, password, userprofile, socialId);

			memberRepository.save(googleUser);
		}

		return googleUser;
	}

	// 4. ???????????? ?????? ?????????
	private Authentication securityLogin(Member findUser) {
		UserDetails userDetails = new UserDetailsImpl(findUser);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//???????????? ????????? ???????????? ??????, ?????? ??????????????? ???????????? ???
		return authentication;
	}

	// 5. jwt ?????? ??????
	private String jwtToken(Authentication authentication, HttpServletResponse response) {
		//???????????? ?????? ???????????? ????????????
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



	/*------------------------------------?????? ?????? ??????----------------------------------------------*/

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
