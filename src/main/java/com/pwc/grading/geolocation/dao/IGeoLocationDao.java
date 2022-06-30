package com.pwc.grading.geolocation.dao;

import com.pwc.grading.geolocation.dao.exception.GeoLocationDAOException;

/**
 * An interface class which is used to perform all
 * geo location related database operations.
 *
 */
public interface IGeoLocationDao {
	/**
	 * This method is used to calculate distance between the given latitude, longitude and tcId.
	 * @param databaseName the database name.
	 * @param tcId the id of the training center.
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @return the distance in dobule.
	 * @throws GeoLocationDAOException if any exception occurs while performing this operation.
	 */
	public double calculateDistance(String databaseName, String tcId, double latitude, double longitude) throws GeoLocationDAOException;
}