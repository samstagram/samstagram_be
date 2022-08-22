package com.hanghae99.samstargram_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hanghae99.samstargram_be.service.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "heart")
@Entity
public class Heart extends Timestamped {

	@Id
	@Column(name = "heart_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonBackReference
	private Member member;

	@ManyToOne
	@JsonBackReference
	private Article article;


	public Heart(Member member, Article article) {
		this.member = member;
		this.article = article;
	}
}
