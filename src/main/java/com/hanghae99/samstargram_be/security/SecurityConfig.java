package com.hanghae99.samstargram_be.security;



import com.hanghae99.samstargram_be.security.jwt.JwtAccessDeniedHandler;
import com.hanghae99.samstargram_be.security.jwt.JwtAuthenticationEntryPoint;
import com.hanghae99.samstargram_be.security.jwt.JwtSecurityConfig;
import com.hanghae99.samstargram_be.security.jwt.TokenProvider;
import com.hanghae99.samstargram_be.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//**
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
				.antMatchers("/h2-console/**", "/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()

				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)

				.and()
				.headers()
				.frameOptions()
				.sameOrigin()

				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.authorizeRequests()
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated()

				.and()
				.apply(new JwtSecurityConfig(tokenProvider))

				.and()
				.oauth2Login()
				.userInfoEndpoint()
				.userService(customOAuth2UserService);
	}
}
