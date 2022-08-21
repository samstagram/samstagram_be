package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hanghae99.samstargram_be.model.dto.ArticleRequestDto;
import com.hanghae99.samstargram_be.service.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "article")
@Entity
public class Article extends Timestamped {
	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	private String useremail;

	private String userprofile;

	@Column(nullable = false)
	private String content;

	@Column
	@ElementCollection
	private List<String> imageList = new ArrayList<>();

	@ManyToOne
	@JsonBackReference
	private Member member;

	@OneToMany(cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Comment> commentList = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Heart> heartList = new ArrayList<>();

	@Column
	@ElementCollection
	private List<String> hashtagList = new ArrayList<>();

	private int commentCnt;
	private int heartCnt;

	private Boolean isLike = false;

	private Boolean isMyArticles = false;

	public Article(ArticleRequestDto articleRequestDto, Member member){
		this.username = member.getUsername();
		this.imageList = articleRequestDto.getImage();
		this.content = articleRequestDto.getContent();
	}

	public void update(ArticleRequestDto articleRequestDto) {
		this.imageList = articleRequestDto.getImage();
		this.content = articleRequestDto.getContent();
	}

	public void addImage(String image){
		this.imageList.add(image);
	}

	public void setHeartCnt(int heartCnt) {
		this.heartCnt = heartCnt;
	}

	public void setLike(Boolean like) {
		isLike = like;
	}

	public void setMyArticles(Boolean myArticles) {
		isMyArticles = myArticles;
	}
}
