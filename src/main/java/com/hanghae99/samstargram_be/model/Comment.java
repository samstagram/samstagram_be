package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
	private Long id;

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

}
