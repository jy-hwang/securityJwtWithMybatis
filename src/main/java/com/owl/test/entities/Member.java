package com.owl.test.entities;

import java.time.LocalDateTime;

import com.owl.test.commons.constants.MemberType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member{

	private Long seq;
	private String email;
	private String password;
	private String name;
	private String mobile;
	private MemberType type = MemberType.USER;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

}
