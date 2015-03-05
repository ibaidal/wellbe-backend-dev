package com.axa.server.base.util;

import java.security.MessageDigest;
import java.util.Random;


public final class StringUtil {
	

	private static final Random RND = new Random();
	private static final String CHR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	
	public static String randomString(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = CHR.charAt(RND.nextInt(CHR.length()));
		}
		return new String(chars);
	}
	
	
	public static String sha256(String plain) {
		try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plain.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
	        return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
