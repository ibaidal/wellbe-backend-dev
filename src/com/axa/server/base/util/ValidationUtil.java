package com.axa.server.base.util;

import java.util.List;
import java.util.regex.Pattern;

import com.axa.server.base.pods.Goal;


public final class ValidationUtil {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	
	public static boolean anyEmpty(String... strs) {		
		for (String str : strs) {
			if (isEmpty(str)) {
				return true;
			}
		}
		
		return false;
	}

	
	public static boolean anyEmpty(List<String> goals, String... strs) {
		boolean empty = goals.isEmpty();
		
		for (String str : strs) {
			if (isEmpty(str)) {
				empty = true;
			}
		}
		
		return empty;
	}


	public static boolean validatePhone(String phone) {
		return !isEmpty(phone) && phone.length() > 4;
	}


	public static boolean validateEmail(String email) {
		return !isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
	}

}
