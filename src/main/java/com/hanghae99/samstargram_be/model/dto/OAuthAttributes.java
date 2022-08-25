package com.hanghae99.samstargram_be.model.dto;

import com.hanghae99.samstargram_be.model.Authority;
import com.hanghae99.samstargram_be.model.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String username;
	private String useremail;
	private String userprofile;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.username = name;
		this.useremail = email;
		this.userprofile = picture;
	}

	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes) {
		System.out.println("------------");
		System.out.println(registrationId);
		System.out.println("------------");

		return ofGoogle(userNameAttributeName, attributes);
	}

	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
				.name((String) attributes.get("name"))
				.email((String) attributes.get("email"))
				.picture((String) attributes.get("picture"))
				.attributes(attributes)
				.nameAttributeKey(userNameAttributeName)
				.build();
	}


	public Member toEntity(){
		return Member.builder()
				.username(username)
				.useremail(useremail)
				.userprofile(userprofile)
				.authority(Authority.ROLE_USER)
				.build();
	}
}
