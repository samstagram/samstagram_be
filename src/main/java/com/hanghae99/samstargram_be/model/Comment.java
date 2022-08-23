package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hanghae99.samstargram_be.model.dto.CommentRequestDto;
import com.hanghae99.samstargram_be.service.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "comment")
@Entity
public class Comment extends Timestamped {

	@Id
	@Column(name = "comment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@Column(nullable = false)
	private String username;

	private String useremail;

	private String userprofile;

	@Column(nullable = false)
	private String content;

	@ManyToOne
	@JsonBackReference
	private Member member;

	@ManyToOne
	@JsonBackReference
	private Article article;

	public Comment (CommentRequestDto commentRequestDto, Article article, Member member){
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
		this.content = commentRequestDto.getContent();
		this.member = member;
		this.article = article;
	}
	public Comment (Article article, Member member){
		this.username = member.getUsername();
		this.useremail = member.getUseremail();
		this.userprofile = member.getUserprofile();
		this.content = "좋아요~♡";
		this.member = member;
		this.article = article;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
