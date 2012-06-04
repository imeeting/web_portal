package com.richitec.util;

import java.util.Random;
import java.util.UUID;

public class RandomString {
	public static String genRandomChars(int pwd_len) {
		// 35是因为数组是�?�?��的，26个字�?10个数�?
		final int maxNum = 36;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random(UUID.randomUUID().getMostSignificantBits());
		while (count < pwd_len) {
			// 生成随机数，取绝对�?，防止生成负数，

			i = Math.abs(r.nextInt(maxNum)); // 生成的数�?���?6-1

			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}

		return pwd.toString();
	}

	public static String genRandomNum(int pwd_len) {
		// 35是因为数组是�?�?��的，26个字�?10个数�?
		final int maxNum = 10;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random(UUID.randomUUID().getMostSignificantBits());
		while (count < pwd_len) {
			// 生成随机数，取绝对�?，防止生成负数，

			i = Math.abs(r.nextInt(maxNum)); // 生成的数�?��为maxNum - 1

			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}

		return pwd.toString();

	}

	/**
	 * 生成6位阿拉伯数字组成的注册验证码
	 * @return
	 */
	public static String validateCode(){
		return genRandomNum(6);
	}
	
	/**
	 * 获得随即密码
	 * @return
	 */
	public static String getPassword(){
		return genRandomChars(6);
	}
	
	/**
	 * 生成10位阿拉伯数字的登录Token
	 * @return
	 */
	public static String loginToken(){
		return genRandomNum(10);
	}
	
	/**
	 * 生成16位数字组成的计费Transaction Id�?
	 * @return
	 */
	public static String billTransactionId(){
		return genRandomNum(16);
	}
	
	/**
	 * 生成6位数字组成的文档共享ID
	 * @return
	 */
	public static String genDSSessionID() {
		return genRandomNum(6);
	}
}
