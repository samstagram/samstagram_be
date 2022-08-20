package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae99.samstargram_be.service.Timestamped;
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
public class Member extends Timestamped {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@Column(unique = true)
	private String useremail;

	private String userprofile;

	private String socialId;


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
	private List<Long> followingList;

	@Column
	@ElementCollection
	private List<Long> followersList;

	private int followingCnt;
	private int followersCnt;

	@Builder
	public Member(String username, String password, String useremail, String userprofile, Authority authority) {
		this.username = username;
		this.password = password;
		this.useremail = useremail;
		this.userprofile = userprofile;
		this.authority = authority;
	}

	@Builder
	public Member(String username, String password, Authority authority) {
		this.username = username;
		this.password = password;
		this.authority = authority;
	}

	public Member(String googleEmail, String username, String encodedPassword, String userprofile, String socialId) {
		this.socialId = socialId;
		this.username = username;
		this.password = encodedPassword;
		this.useremail = googleEmail;
		this.userprofile = userprofile;

	}

	public void addArticle(Article article){
		this.articleList.add(article);
	}

	public void removeArticle(Article article) {
		this.articleList.remove(article);
	}

	public Member update(String username, String userprofile){
		this.username = username;
		this.userprofile = userprofile;

		return this;
	}
}
