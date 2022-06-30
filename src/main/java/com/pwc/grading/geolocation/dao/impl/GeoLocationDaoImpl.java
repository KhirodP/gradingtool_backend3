package com.pwc.grading.geolocation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.inject.Singleton;

import com.pwc.grading.db.MSSqlServerUtill;
import com.pwc.grading.geolocation.dao.GeoLocationQueryConstants;
import com.pwc.grading.geolocation.dao.IGeoLocationDao;
import com.pwc.grading.geolocation.dao.exception.GeoLocationDAOException;

/**
 * Implementation class for {@link IGeoLocationDao} 
 *
 */
@Singleton
public class GeoLocationDaoImpl implements IGeoLocationDao {

	/**
	 * This method is used to calculate distance between the given latitude, longitude and tcId.
	 * @param databaseName the database name.
	 * @param tcId the id of the training center.
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @return the distance in dobule.
	 * @throws GeoLocationDAOException if any exception occurs while performing this operation.
	 */
	@Override
	public double calculateDistance(String databaseName, String tcId, double latitude, double longitude)
			throws GeoLocationDAOException {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = MSSqlServerUtill.getConnection();
			pstmt = connection.prepareStatement(GeoLocationQueryConstants.CALCULATE_DST_BTW_TC_AND_USER
					.replace(GeoLocationQueryConstants.DATABASE_PLACE_HOLDER, databaseName));
			pstmt.setString(1, tcId);
			pstmt.setDouble(2, latitude);
			pstmt.setDouble(3, longitude);
			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				double distanceBtw = resultSet.getDouble(GeoLocationQueryConstants.DISTANCE);
				return distanceBtw;
			}

		} catch (Exception e) {
			throw new GeoLocationDAOException("Cannot calculate the distance: " + e.getMessage());
		} finally {
			MSSqlServerUtill.close(resultSet, pstmt, connection);
		}
		return 0.0;
	}

}
