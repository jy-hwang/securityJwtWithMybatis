package com.owl.test.models.member;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.owl.test.commons.constants.MemberType;
import com.owl.test.entities.Member;
import com.owl.test.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService{

	private final MemberMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = mapper.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username));
		
		MemberType type = Objects.requireNonNullElse(member.getType(), MemberType.USER);
		
		List<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(type.name()));
		
		return MemberInfo.builder()
				.email(member.getEmail())
				.name(member.getName())
				.member(member)
				.authorities(authorities)
				.build();
	}
	
}
