package com.hanghae99.samstargram_be.security.jwt;

import com.hanghae99.samstargram_be.model.Code;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//**
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";
	public static final String COOKIE_HEADER = "Cookie";
	public static final String JWT_PREFIX = "jwt=";

	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		String jwt = resolveToken(request);

		if(jwt == null){
			request.setAttribute("exception", Code.UNKNOWN_ERROR.getCode());
		}

		try {
			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				Authentication authentication = tokenProvider.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e){
			request.setAttribute("exception", Code.EXPIRED_TOKEN.getCode());
		} catch (MalformedJwtException | SignatureException e){
			request.setAttribute("exception", Code.WRONG_TYPE_TOKEN.getCode());
		} catch(JwtException e){
			request.setAttribute("exception", Code.UNKNOWN_ERROR);
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		String cookie = request.getHeader(COOKIE_HEADER);

		if (bearerToken != null){
			if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
				return bearerToken.substring(BEARER_PREFIX.length());
			}
		}

		if (StringUtils.hasText(cookie) && cookie.startsWith(JWT_PREFIX)) {
			return cookie.substring(JWT_PREFIX.length());
		}

		return null;
	}
}