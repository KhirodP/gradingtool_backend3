package com.pwc.grading.sms.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class sendSMS {

	public static void main(String[] args) {
		String response = sendSms();
		System.out.println(response);
	}
    	public static String sendSms() {
    		try {
    			// Construct data
    			String apiKey = "apikey=" + "LAIfpIKkVaA-xDGaoMu0lFzhHVkRSXRT8XOQoZS3co";
    			String message = "&message=" + "Hi there, thank you for sending your first test message from Textlocal. See how you can send effective SMS campaigns here: https://tx.gl/r/2nGVj/";
    			String sender = "&sender=" + "TXTLCL";
    			String numbers = "&numbers=" + "918072248283";
    			
    			// Send data
    			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
    			String data = apiKey + numbers + message + sender;
    			conn.setDoOutput(true);
    			conn.setRequestMethod("POST");
    			conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
    			conn.getOutputStream().write(data.getBytes("UTF-8"));
    			int responseCode = conn.getResponseCode();
    			System.out.println(responseCode);
    			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    			final StringBuffer stringBuffer = new StringBuffer();
    			String line;
    			while ((line = rd.readLine()) != null) {
    				stringBuffer.append(line);
    			}
    			rd.close();
    			
    			return stringBuffer.toString();
    		} catch (Exception e) {
    			System.out.println("Error SMS "+e);
    			return "Error "+e;
    		}
    	}
}
