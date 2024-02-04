package com.owl.test.models.member;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import com.owl.test.api.members.dto.ResponseLogin;
import com.owl.test.config.jwt.TokenProvider;
import com.owl.test.config.redis.RedisService;
import com.owl.test.dto.AuthDto;
import com.owl.test.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

	private final TokenProvider tokenProvider;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	
    private final RedisService redisService;

    private final String SERVER = "Server";
	
	private final MemberMapper mapper;
	
	public ResponseLogin authenticate(String email, String password) {
//		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
		
	//	Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		
		//SecurityContextHolder.getContext().setAuthentication(authentication);

	    //    return generateToken(SERVER, authentication.getName(), getAuthorities(authentication));
		// 인증 정보를 가지고 JWT AccessToken 발급
		AuthDto.TokenDto tokenDto = tokenProvider.createToken(email, password);
		
		return ResponseLogin.builder()
				.accessToken(tokenDto.getAccessToken())
				.build();
	}
	
}
