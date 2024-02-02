package com.owl.test.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.owl.test.entities.Member;

@Mapper
public interface MemberMapper {

	Optional<Member> findByEmail(String email);
	
	boolean exists(String email);

	int save(Member member);
	
}
