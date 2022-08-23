package com.hanghae99.samstargram_be.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class Gcode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String gcode;

	@OneToOne
	private Member member;

	private String token;

	public Gcode(String gcode) {
		this.gcode = gcode;
	}
}
