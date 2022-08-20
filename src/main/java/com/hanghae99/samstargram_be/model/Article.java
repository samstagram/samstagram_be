package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "article")
@Entity
public class Article {
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
	private List<String> image;

	@ManyToOne
	@JsonBackReference
	private Member member;

	@OneToMany(cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Comment> commentList;

	@OneToMany(cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Heart> heartList;

	private Long heartCnt;

	private Boolean isLike = false;

	private Boolean isMyArticles = false;


}
