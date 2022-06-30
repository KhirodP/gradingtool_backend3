package com.pwc.grading.reportingdb.util;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.pwc.grading.reportingdb.service.exp.JSONValidationFailedException;

public class JsonValidationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonValidationUtil.class);
	
	/**
	 * This method is used to validate the given JSON against a given JSON Schema.
	 * @param jsonString the JSON to be validated.
	 * @param jsonSchema the inputStream of the JSON Schema.
	 * @return true if the JSON is valid against the schema, false if not valid.
	 * @throws JSONValidationFailedException of invalid json is passed.
	 */
	public static boolean validateJsonAgainstSchema(String jsonString, InputStream jsonSchema) throws JSONValidationFailedException {
		LOGGER.debug(".inside validateJsonAgainstSchema method of JsonUtil class.");
		LOGGER.debug("Input JSON: "+StringEscapeUtils.escapeJava(jsonString));
		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
		JsonSchema schema =  factory.getSchema(jsonSchema);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode;
		try {
			jsonNode = mapper.readTree(jsonString);
		} catch (Exception e) {
			LOGGER.error("Cannot validate the JSON, error occured",e);
			throw new JSONValidationFailedException("Validation of the given JSON against the Schema is Failed, "+e.getMessage(),e);
		} 
		Set<ValidationMessage> validate = schema.validate(jsonNode);
		LOGGER.debug("validate: "+validate);
		if(validate.size()==0) {
			return true;
		}
		else
			return false;
	}
	
	public static <T> T getObject(String jsonString, Class<T> objectType) throws JSONValidationFailedException {
		LOGGER.debug(".inside getObject method of JsonUtil class.");
		ObjectMapper mapper = new ObjectMapper();
		try {
			T convertedObj = mapper.readValue(jsonString, objectType);
			return convertedObj;
		} catch (Exception e) {
			LOGGER.error("Cannot convert to the JSON String to the object of requiredType");
			throw new JSONValidationFailedException("Cannot convert to the JSON String to the object of requiredType, "+e.getMessage(),e);
		} 
	}
	
	public static <T> String getJSONStringFromObject(T object) throws JSONValidationFailedException {
		LOGGER.debug(".inside getObjectToString method of JsonUtil class.");
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writeValueAsString(object);
			return jsonString;
		} catch (Exception e) {
			LOGGER.error("Cannot convert to the Object to the JSON String");
			throw new JSONValidationFailedException("Cannot convert to the Object to the JSON String, "+e.getMessage(),e);
		} 
	}
}
