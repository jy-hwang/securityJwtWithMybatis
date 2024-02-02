package com.owl.test.models.member;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.owl.test.api.members.dto.RequestJoin;
import com.owl.test.api.members.validator.JoinValidator;
import com.owl.test.commons.constants.MemberType;
import com.owl.test.entities.Member;
import com.owl.test.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberJoinService {

	private final MemberMapper mapper;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JoinValidator validator;
	
	public void save(RequestJoin join, Errors errors) {
		validator.validate(join,  errors);;
		if(errors.hasErrors()) {
			return;
		}
		
		save(join);
	}
	
	public void save(RequestJoin join) {
		String encodedPassword = passwordEncoder.encode(join.password());
		LocalDateTime createdAt = LocalDateTime.now();
		Member member = Member.builder()
				.email(join.email())
				.password(encodedPassword)
				.name(join.name())
				.mobile(join.mobile())
				.type(MemberType.USER)
				.createdAt(createdAt)
				.build();
		save(member);
	}
	
	
	public void save(Member member) {
		mapper.save(member);
	}
}
