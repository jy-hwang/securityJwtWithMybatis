package com.owl.test.api.members.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RequestLogin(
	//@formatter:off
	@NotBlank @Email
	String email,
	
	@NotBlank
	String password
	//@formatter:on
	) {

}
