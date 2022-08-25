package com.hanghae99.samstargram_be.repository;

import com.hanghae99.samstargram_be.model.Gcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GcodeRepository extends JpaRepository<Gcode,Long> {

	boolean existsByGcode(Gcode code);

	boolean existsByGcode(String code);
}
