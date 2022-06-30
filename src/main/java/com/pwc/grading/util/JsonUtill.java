package com.pwc.grading.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.pwc.grading.util.exception.JsonUtillException;

/**
 * An utility class for the JSON related operations.
 *
 */
public class JsonUtill {

	/**
	 * Get a string value from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the value if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static String getString(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		String jsonValue = (String) jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}
		return jsonValue;

	}

	/**
	 * Get a long value from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the value if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static Long getLong(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		Object jsonValue = jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}

		Long jsonValuelong = Long.valueOf((long) jsonValue);
		return jsonValuelong;

	}

	/**
	 * Get a int value from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the value if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static int getInt(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		Object jsonValue = jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}

		int jsonValueInt = Integer.parseInt(jsonValue + "");
		return jsonValueInt;

	}

	/**
	 * Get a {@link JSONObject} from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the object if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static JSONObject getJsonObject(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		JSONObject jsonValue = (JSONObject) jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}
		return jsonValue;

	}

	/**
	 * Get a {@link JSONArray} from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the object if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static JSONArray getJsonArray(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		JSONArray jsonValue = (JSONArray) jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found");
		}
		return jsonValue;

	}

	/**
	 * Get a boolean value from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the value if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static boolean getBoolean(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		Object jsonValue = jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}
		boolean jsonValueBool = (boolean) jsonValue;
		return jsonValueBool;

	}
	
	/**
	 * Get a double value from a {@link JSONObject} object.
	 * @param jsonObject the json object.
	 * @param jsonKey the key to fetch its value from the json object.
	 * @return the value if key is found
	 * @throws JsonUtillException if key is not found
	 */
	public static double getDouble(JSONObject jsonObject, String jsonKey) throws JsonUtillException {
		Object jsonValue = jsonObject.get(jsonKey);
		if (jsonValue == null) {
			throw new JsonUtillException("JsonObject [" + jsonKey + "] key not found ");
		}
		double jsonValueDouble = (double) jsonValue;
		return jsonValueDouble;
	}
}
