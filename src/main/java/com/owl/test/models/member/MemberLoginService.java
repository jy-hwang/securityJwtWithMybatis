package com.owl.test.models.member;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.owl.test.api.members.dto.ResponseLogin;
import com.owl.test.config.jwt.TokenProvider;
import com.owl.test.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

	private final TokenProvider tokenProvider;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	
	private final MemberMapper mapper;
	
	public ResponseLogin authenticate(String email, String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		
		// 인증 정보를 가지고 JWT AccessToken 발급
		
		String accessToken = tokenProvider.createToken(authentication);
		
		return ResponseLogin.builder()
				.accessToken(accessToken)
				.build();
	}
	
}
