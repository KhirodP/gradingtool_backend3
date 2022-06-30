package com.pwc.grading.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for building queries with Sql IN clause.
 *
 */
public class InQueryBuilderUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(InQueryBuilderUtil.class);
	
	public static String buildPlaceHolders(String originalQuery, int items) {
		LOGGER.debug(".inside buildPlaceHolders method of InQueryBuilderUtil.");
		LOGGER.debug("Original Query: "+originalQuery);
		LOGGER.debug("PlaceHolder size: "+items);
		if(items==0) {
			LOGGER.debug("Items length is zero.");
			String query = originalQuery+"'')";
			LOGGER.debug("Builded Query: "+query);
			return query;
		}
		StringBuilder queryBuilder = new StringBuilder(originalQuery);
		for( int i = 0; i< items; i++){
			queryBuilder.append(" ?");
			if(i != items -1) queryBuilder.append(",");
		}
		queryBuilder.append(")");
		String buildedQuery = queryBuilder.toString();
		LOGGER.debug("Builded Query: "+buildedQuery);
		return buildedQuery;
	}
}
