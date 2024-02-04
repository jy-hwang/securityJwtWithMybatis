package com.owl.test.config.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.owl.test.config.redis.RedisService;
import com.owl.test.dto.AuthDto;
import com.owl.test.models.member.MemberInfo;
import com.owl.test.models.member.MemberInfoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional(readOnly = true)
public class TokenProvider implements InitializingBean {

  private final RedisService redisService;

  // private static final String AUTHOIRIES_KEY = "auth";
  private static final String AUTHORITIES_KEY = "role";
  private static final String EMAIL_KEY = "email";
  private static final String url = "http://localhost:9590";

  
  private final String secret;

  private final long accessTokenValidityInMilliseconds;
  private final long refreshTokenValidityInMilliseconds;

  private Key signingKey;

  @Autowired
  private MemberInfoService memberInfoService;

  public TokenProvider(RedisService redisService,@Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
      @Value("${jwt.refresh-token-validity-in-seconds}")Long refreshTokenValidityInMilliseconds) {
    this.secret = secret;
    this.redisService = redisService;
    this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
    this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
  }

  // 시크릿 키 설정
  @Override
  public void afterPropertiesSet() throws Exception {
    byte[] keyByets = Decoders.BASE64.decode(secret);
    signingKey = Keys.hmacShaKeyFor(keyByets);

  }

  public AuthDto.TokenDto createToken(String email, String authorities) {
    // String authorities = authentication.getAuthorities().stream()
    // .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    // Date validity = new Date(now + this.tokenValidityInSeconds * 1000);
    //@formatter:off
		 String accessToken = Jwts.builder()
		     .setHeaderParam("typ", "JWT")
             .setHeaderParam("alg", "HS512")
             .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
			 .setSubject("access-token")
             .claim(url, true)
             .claim(EMAIL_KEY, email)
             .claim(AUTHORITIES_KEY, authorities)
			 .signWith(signingKey, SignatureAlgorithm.HS512)
 			 .compact();
		 
		 String refreshToken = Jwts.builder()
		     .setHeaderParam("typ", "JWT")
		     .setHeaderParam("alg", "HS512")
		     .setSubject("refresh-token")
		     .signWith(signingKey, SignatureAlgorithm.HS512)
		     .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
		     .compact();
		//@formatter:on

    return new AuthDto.TokenDto(accessToken, refreshToken);
  }

  /*
   * 토큰을 받아서 클레임을 생성 클레임에서 권한 정보를 가져와서 시큐리티 UserDetails 객체를 만들고 Authentication 객체 반환
   * 
   * @param token
   * 
   * @return Authentication
   */
  // == 토큰으로부터 정보 추출 == //

  public Claims getClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(token).getPayload();
    } catch (ExpiredJwtException e) { // Access Token
      return e.getClaims();
    }
  }


  public Authentication getAuthentication(String token) {

    List<? extends GrantedAuthority> authorities =
    //@formatter:off
			Arrays.stream(getClaims(token).get(AUTHORITIES_KEY).toString().split(","))
			      .map(SimpleGrantedAuthority::new)
			      .collect(Collectors.toList());
			//@formatter:on

    MemberInfo memberInfo =
        (MemberInfo) memberInfoService.loadUserByUsername(getClaims(token).get(EMAIL_KEY).toString());
    memberInfo.setAuthorities(authorities);

    return new UsernamePasswordAuthenticationToken(memberInfo, token, authorities);

  }

  public long getTokenExpirationTime(String token) {
    return getClaims(token).getExpiration().getTime();
  }

  /**
   * 토큰 유효성 체크
   *
   * @param token
   * @return
   */
  public boolean validateRefreshToken(String refreshToken) {
    try {
      if (redisService.getValues(refreshToken).equals("delete")) { // 회원 탈퇴했을 경우
        return false;
      }
      Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(refreshToken);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
      e.printStackTrace();
    }

    return false;
  }

  // Filter에서 사용
  public boolean validateAccessToken(String accessToken) {
    try {
      if (redisService.getValues(accessToken) != null // NPE 방지
          && redisService.getValues(accessToken).equals("logout")) { // 로그아웃 했을 경우
        return false;
      }
      Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(accessToken);
      return true;
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 재발급 검증 API에서 사용
  public boolean validateAccessTokenOnlyExpired(String accessToken) {
    try {
      return getClaims(accessToken).getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
