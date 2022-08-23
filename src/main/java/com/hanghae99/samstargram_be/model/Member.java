package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae99.samstargram_be.service.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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
	private Long memberId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

//	@Column(unique = true)
	private String useremail = "sparta@gmail.com";

	private String userprofile = "https://mblogthumb-phinf.pstatic.net/MjAyMDExMjRfOSAg/MDAxNjA2MjA1MDI5MzE1.FqSl8OtJxZxJm1IYKtRXrhFNum6Qfu5wMq7MAiZwhFgg.9RMA4C4GmAp8XFc04eqM6zuRfxrCcU1y7Z8fA2_NA38g.JPEG.sosohan_n/IMG_5374.JPG?type=w800";

	@Column(unique = true)
	private String socialId;


	@Enumerated(EnumType.STRING)
	private Authority authority;

	@OneToMany(mappedBy = "member")
	private List<Article> articleList = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Comment> commentList = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Heart> heartList = new ArrayList<>();

	@OneToOne
	private Gcode gcode;

	@Column
	@ElementCollection
	private List<Long> followingList = new ArrayList<>();

	@Column
	@ElementCollection
	private List<Long> followersList = new ArrayList<>();

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
		this.authority = Authority.ROLE_USER;
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

	public void addFolloing(Long folloingId){
		this.followingList.add(folloingId);
		this.followingCnt = this.followingList.size();
	}
	public void removeFolloing(Long folloingId){
		this.followingList.remove(folloingId);
		this.followingCnt = this.followingList.size();
	}
	public void addFollower(Long followerId){
		this.followersList.add(followerId);
		this.followersCnt = this.followersList.size();
	}
	public void removeFollower(Long followerId){
		this.followersList.remove(followerId);
		this.followersCnt = this.followersList.size();
	}
	public void addComment(Comment comment) {
		this.commentList.add(comment);
	}
	public void removeComment(Comment comment) {
		this.commentList.remove(comment);
	}

	public void addHeart(Heart heart){
		this.heartList.add(heart);
	}
	public void removeHeart(Heart heart){
		this.heartList.remove(heart);
	}

	public void setGcode(Gcode gcode) {
		this.gcode = gcode;
	}
}
