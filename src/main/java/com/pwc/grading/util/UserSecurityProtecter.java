package com.pwc.grading.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class which is used to encrypt the user data.
 *
 */
public class UserSecurityProtecter {

	private static final Logger logger = LoggerFactory.getLogger(UserSecurityProtecter.class);

//	private static final String INSTANCE_TYPE = "MD5";

	/**
	 * This method will encrypt the data in MD5 encryption algorithm.
	 * @param data the data to be encrypted.
	 * @return the encrypted data.
	 */
	public static String protectUserData(String data) {
		logger.debug(" inside protectUserData of UserSecurityProtecer");
		try {
//			String encrypted = DigestUtils.md5Hex(data);
//			logger.debug("%%% Data ["+data+"], Encrypted MD5 ["+encrypted+"] %%%");
			
			String encrypted = DigestUtils.sha256Hex(data);
			logger.debug("%%% Data ["+StringEscapeUtils.escapeJava(data)+"], Encrypted SHA-256 ["+StringEscapeUtils.escapeJava(encrypted)+"] %%%");
			return encrypted;
		} catch (Exception e) {
//			logger.error("Exception occured when converting MD5: "+e);
			logger.error("Exception occured when converting SHA-256: "+e);
			throw new RuntimeException(e);
		}
	}
	
//	public static void main(String args[]) {
//		String pass = protectUserData("Shiv");
//		System.out.println("encrypted password of shiv is:"+ pass);
//	}
	
//	public static String protectUserData(String data) {
//		logger.debug(" inside protectUserData ");
//		try {
//			MessageDigest md = MessageDigest.getInstance(INSTANCE_TYPE);
//			logger.debug(" data :: " + data);
//			byte[] messageDigest = md.digest(data.getBytes());
//			BigInteger no = new BigInteger(1, messageDigest);
//			String encrpted = no.toString(16);
//			logger.debug("encrpted String is :: " + encrpted);
//			return encrpted;
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException(e);
//		}
//	}

}
