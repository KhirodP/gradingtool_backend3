package com.pwc.grading.util;

import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An utility class for the properties related operations.
 *
 */
public class PropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	/**
	 * Read the properties from the resources.
	 * @param fileName the file to be read.
	 * @return {@link Properties} containing all that properties contained.
	 */
	public static Properties getPropertiesFromResoures(String fileName) {
		logger.debug(
				" inside getPropertiesFromResoures method of PropertiesUtil  loading properties from .. " + fileName);
		Properties properties = new Properties();
		try {
			InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
			properties.load(stream);
		} catch (Exception e) {
			logger.error("Exception is : "+e);
			logger.error("unable to load " + fileName + " file from resources");
		}
		return properties;
	}

}
