package com.owl.test.api.members.dto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RequestJoin(
	//@formatter:off
	@NotBlank @Email
	String email,
	
	@NotBlank
	String password,
	
	@NotBlank
	String confirmPassword,
	
	@NotBlank
	String name,
	
	@NotBlank
	String mobile,
	
	@AssertTrue
	boolean agree
	//@formatter:on
) {

}
