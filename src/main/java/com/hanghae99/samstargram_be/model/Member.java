package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

//**
@Getter
@NoArgsConstructor
@Table(name = "member")
@Entity
public class Member {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@Column(nullable = false, unique = true)
	private String useremail;

	private String userprofile;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@OneToMany(mappedBy = "member")
	private List<Article> articleList;

	@OneToMany(mappedBy = "member")
	private List<Comment> commentList;

	@OneToMany(mappedBy = "member")
	private List<Heart> heartList;

	@Column
	@ElementCollection
	private List<Long> followList;

	@Column
	@ElementCollection
	private List<Long> followerList;

	@Builder
	public Member(String username, String password, Authority authority) {
		this.username = username;
		this.password = password;
		this.authority = authority;
	}
}
