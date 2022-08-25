package com.hanghae99.samstargram_be.service;


import com.hanghae99.samstargram_be.model.Member;
import com.hanghae99.samstargram_be.model.RefreshToken;
import com.hanghae99.samstargram_be.model.dto.MemberNameResponseDto;
import com.hanghae99.samstargram_be.model.dto.MemberRequestDto;
import com.hanghae99.samstargram_be.model.dto.TokenDto;
import com.hanghae99.samstargram_be.model.dto.TokenRequestDto;
import com.hanghae99.samstargram_be.repository.MemberRepository;
import com.hanghae99.samstargram_be.repository.RefreshTokenRepository;
import com.hanghae99.samstargram_be.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//**
@Service
@RequiredArgsConstructor
public class AuthService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public MemberNameResponseDto signup(MemberRequestDto memberRequestDto) {
//		if(!(Pattern.matches("[a-zA-Z0-9]*$",memberRequestDto.getUsername()) && (memberRequestDto.getUsername().length() > 3 && memberRequestDto.getUsername().length() <13)
//				&& Pattern.matches("[a-zA-Z0-9]*$",memberRequestDto.getPassword()) && (memberRequestDto.getPassword().length() > 3 && memberRequestDto.getPassword().length() <33))){
//			throw new IllegalArgumentException("닉네임 혹은 비밀번호 조건을 확인해주세요.");
//		}
		if (memberRepository.existsByUsername(memberRequestDto.getUsername())) {
			throw new IllegalArgumentException("중복된 닉네임입니다.");
		} else if (!memberRequestDto.getPassword().equals(memberRequestDto.getPassword2()))
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		Member member = memberRequestDto.toMember(passwordEncoder);
		switch (memberRequestDto.getUsername()) {
			case "신짱구":
				member.setUserprofile("https://yt3.ggpht.com/gc_XZe30hmiTt7s7GkyJ0iOkAwrbGrrzr1FRCllWBqmzLaDL9Jw8Ni552Hy3t1HVGNt5GWg7DQ=s900-c-k-c0x00ffffff-no-rj");
				break;
			case "김철수":
				member.setUserprofile("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQXj5l2HfjGrKZvdBEAfK6w2Gqqx6UoL840AQ&usqp=CAU");
				break;
			case "한유리":
				member.setUserprofile("https://img1.daumcdn.net/thumb/R1280x0.fjpg/?fname=http://t1.daumcdn.net/brunch/service/user/5vqW/image/eNklVq6Y6rtkTOe44XwHQRakiV0.jpg");
				break;
			case "이훈이":
				member.setUserprofile("https://m1.daumcdn.net/cfile273/image/9930ED335A23DC81030387");
				break;
			case "맹구":
				member.setUserprofile("https://mblogthumb-phinf.pstatic.net/MjAyMDAxMDNfMTc3/MDAxNTc4MDUwOTc5Mjkz.53foc2K793cQned5VD-mHS0LMvgf5YGtadk09ElulBAg.o3WemJa6S_nawhNga5MGaeJHOxFMbCXrPrd_JoPLpckg.JPEG.minsuk2468/3.jpg?type=w800");
				break;
			case "한수지":
				member.setUserprofile("https://obj.thewiki.kr/thecloud/temp/ec8898eca78020ecb488ecb0bdeab8b02e6a7067.jpg");
				break;
			case "신짱아":
				member.setUserprofile("https://w.namu.la/s/3be9e6af36312b453ca1337b46733dae689ca164fd92c4a28c43e8de8a224d56e49ce18ceea06166626d3af5323d12b656af5e74c294134261f6ca4ec8ac55df6ad05a74e6858973b471b157998d768a6e5f186463b5b316af4554265a500b5a");
				break;
			case "신형만":
				member.setUserprofile("https://static.wikia.nocookie.net/shinchan/images/6/64/Hiroshi.JPG/revision/latest/scale-to-width-down/250?cb=20131020032448&path-prefix=ko");
				break;
			case "봉미선":
				member.setUserprofile("https://mblogthumb-phinf.pstatic.net/20120713_271/dh_0_1342184437820kBnK7_JPEG/%B6%A1.JPG?type=w2");
				break;
			case "흰둥이":
				member.setUserprofile("https://t3.daumcdn.net/thumb/R720x0.fjpg/?fname=http://t1.daumcdn.net/brunch/service/user/cnoC/image/MVXKXCbEwk6GIO7bzjVpafxjSaA.jpg");
				break;
		}
		return MemberNameResponseDto.of(memberRepository.save(member));
	}

	@Transactional
	public TokenDto login(MemberRequestDto memberRequestDto) {
		UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

		// 4. RefreshToken 저장
		RefreshToken refreshToken = RefreshToken.builder()
				.key(authentication.getName())
				.value(tokenDto.getRefreshToken())
				.build();

		refreshTokenRepository.save(refreshToken);

		return tokenDto;
	}

	@Transactional
	public TokenDto reissue(TokenRequestDto tokenRequestDto) {
		if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
			throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
		}

		Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

		RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
				.orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

		if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
			throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
		}

		TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

		RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
		refreshTokenRepository.save(newRefreshToken);

		return tokenDto;
	}
}















