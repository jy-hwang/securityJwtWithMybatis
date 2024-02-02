package com.owl.test.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String header;
	private String secret;
	private Long accessTokenValidityInSeconds;

}
