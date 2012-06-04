package com.richitec.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	/**
	 * calculate MD5 digest for the text
	 * @param text
	 * @return md5 digest string
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException 
	 */
	public static String md5(String text) {
		String digestText = "";
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] digest = digester.digest(text.getBytes("UTF-8"));
			digestText = HexUtils.convert(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return digestText;
	}
}
