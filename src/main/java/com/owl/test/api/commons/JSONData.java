package com.owl.test.api.commons;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class JSONData<T> {
	private boolean success = true;

	@NonNull
	private T data;

	private String message;
	private HttpStatus status = HttpStatus.OK;

}
