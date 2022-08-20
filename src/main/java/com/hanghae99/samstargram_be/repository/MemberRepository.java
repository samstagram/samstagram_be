package com.hanghae99.samstargram_be.repository;

import com.hanghae99.samstargram_be.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//**
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);
	boolean existsByUsername(String username);
}
