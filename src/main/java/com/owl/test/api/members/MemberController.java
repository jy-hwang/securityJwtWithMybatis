package com.owl.test.api.members;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.owl.test.api.commons.JSONData;
import com.owl.test.api.members.dto.RequestJoin;
import com.owl.test.api.members.dto.RequestLogin;
import com.owl.test.api.members.dto.ResponseLogin;
import com.owl.test.commons.Utils;
import com.owl.test.commons.exceptions.BadRequestException;
import com.owl.test.config.jwt.CustomJwtFilter;
import com.owl.test.models.member.MemberInfoService;
import com.owl.test.models.member.MemberJoinService;
import com.owl.test.models.member.MemberLoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberLoginService loginService;
	private final MemberInfoService infoService;
	private final MemberJoinService joinService;

	/**
	 * accessToken 발급
	 * 
	 */
	@PostMapping("/token")
	public ResponseEntity<JSONData<ResponseLogin>> authorize(@Valid @RequestBody RequestLogin requestLogin,
			Errors errors) {
		// 유효성 검사 처리
		errorProcess(errors);

		ResponseLogin token = loginService.authenticate(requestLogin.email(), requestLogin.password());

		HttpHeaders headers = new HttpHeaders();

		headers.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

		JSONData<ResponseLogin> data = new JSONData<>(token);

		return ResponseEntity.status(data.getStatus()).headers(headers).body(data);
	}

	@PostMapping
	public ResponseEntity<JSONData<Object>> join(@RequestBody @Valid RequestJoin form, Errors errors) {
		joinService.save(form, errors);

		errorProcess(errors);

		HttpStatus status = HttpStatus.CREATED;

		JSONData<Object> data = new JSONData<>();
		data.setSuccess(true);
		data.setStatus(status);

		return ResponseEntity.status(status).body(data);
	}

	private void errorProcess(Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = Utils.getMessages(errors);
			throw new BadRequestException(errorMessages.stream().collect(Collectors.joining("||")));
		}
	}

	@GetMapping("/member_only")
	public void MemberOnlyUrl() {
		log.info("회원 전용 URL 접근 테스트");
	}

	@GetMapping("/admin_only")
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	public void adminOnlyUrl() {
		log.info("관리자 전용 URL 접근 테스트");
	}
}
