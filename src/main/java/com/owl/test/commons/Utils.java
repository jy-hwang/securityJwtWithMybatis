package com.owl.test.commons;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.springframework.validation.Errors;

public class Utils {

	private static ResourceBundle validationsBundle;

	private static ResourceBundle errorsBundle;

	static {
		validationsBundle = ResourceBundle.getBundle("messages.validations");
		errorsBundle = ResourceBundle.getBundle("messages.errors");
	}

	public static String getMessage(String code, String bundleType) {
		bundleType = Objects.requireNonNullElse(bundleType, "validation");
		ResourceBundle bundle = bundleType.equals("error") ? errorsBundle : validationsBundle;
		try {
			return bundle.getString(code);

		} catch (Exception e) {
			return null;
		}
	}

	public static List<String> getMessages(Errors errors) {
		return errors.getFieldErrors().stream()
				.flatMap(f -> Arrays.stream(f.getCodes()).sorted(Comparator.reverseOrder())
						.map(c -> getMessage(c, "validation")))
				.filter(s -> s != null && !s.isBlank()).toList();
	}

}
