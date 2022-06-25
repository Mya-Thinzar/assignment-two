package com.jdc.project.test.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

public class CommonUtils {

	static int integer(String str) {
		if (StringUtils.hasLength(str)) {
			return Integer.parseInt(str);
		}

		return 0;
	}

	static LocalDate localDate(String str) {
		
		if (StringUtils.hasLength(str)) {
			if(isValidPattern(str)) return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));
			else return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		return null;
	}

	static boolean isValidPattern(String dateStr) {
		try {
			LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}
}
