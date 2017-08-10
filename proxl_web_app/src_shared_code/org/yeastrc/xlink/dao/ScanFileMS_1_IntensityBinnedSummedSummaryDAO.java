package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedSummaryDTO;

/**
 * table scan_file_ms1_intensity_binned_summed_summary
 *
 */
public class ScanFileMS_1_IntensityBinnedSummedSummaryDAO {
	
	private static final Logger log = Logger.getLogger(ScanFileMS_1_IntensityBinnedSummedSummaryDAO.class);
	

	/**
	 * use to determine if a record exists
	 * @param scanFileId
	 * @return
	 * @throws Exception
	 */
	public static Integer getScanFileIdFromScanFileId( int scanFileId ) throws Exception {

		Integer returnItem = null;

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Our SQL statement
		final String sqlStr =  "SELECT scan_file_id "
			+ " FROM scan_file_ms1_intensity_binned_summed_summary "
			+ " WHERE scan_file_id = ?  " ;

		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( sqlStr );

			pstmt.setInt( 1, scanFileId );

			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {
				returnItem = rs.getInt( "scan_file_id" );
			}
			
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
			throw e;
		}
		
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return returnItem;
	}
	
	
	
	/**
	 * @param scanFileId
	 * @return
	 * @throws Exception
	 */
	public static ScanFileMS_1_IntensityBinnedSummedSummaryDTO getFromScanFileId( int scanFileId ) throws Exception {

		ScanFileMS_1_IntensityBinnedSummedSummaryDTO returnItem = null;

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Our SQL statement
		final String sqlStr =  "SELECT scan_file_id, retention_time_max_bin_minus_min_bin_plus_1,  mz_max_bin_minus_min_bin_plus_1, intensity_max_minus_min"
			+ " FROM scan_file_ms1_intensity_binned_summed_summary "
			+ " WHERE scan_file_id = ?  " ;

		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( sqlStr );

			pstmt.setInt( 1, scanFileId );

			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				returnItem = new ScanFileMS_1_IntensityBinnedSummedSummaryDTO();

				returnItem.setScanFileId( rs.getInt( "scan_file_id" ) );
				
				returnItem.setRetentionTimeMaxBinMinusMinBinPlusOne( rs.getLong( "retention_time_max_bin_minus_min_bin_plus_1" ) );
				returnItem.setMzMaxBinMinusMinBinPlusOne( rs.getLong( "mz_max_bin_minus_min_bin_plus_1" ) );
				returnItem.setIntensityMaxBinMinusMinBin( rs.getDouble( "intensity_max_minus_min" ) );
			}
			
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
			throw e;
		}
		
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return returnItem;
	}
	
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public static void save( ScanFileMS_1_IntensityBinnedSummedSummaryDTO item ) throws Exception {

		Connection dbConnection = null;

		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, dbConnection );
			
		} finally {

			// be sure database handles are closed

			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}

		}
		
	}


	private static String insertSQL = "INSERT INTO scan_file_ms1_intensity_binned_summed_summary "
			+ "(scan_file_id, retention_time_max_bin_minus_min_bin_plus_1, mz_max_bin_minus_min_bin_plus_1, intensity_max_minus_min )"
			+ " VALUES ( ?, ?, ?, ? )";

	/**
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static void save( ScanFileMS_1_IntensityBinnedSummedSummaryDTO item, Connection dbConnection ) throws Exception {

		PreparedStatement pstmtSave = null;
		
//		ResultSet rsGenKeys = null;

		try {
			
//			pstmtSave = dbConnection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );
			pstmtSave = dbConnection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmtSave.setInt( counter, item.getScanFileId() );
			counter++;
			pstmtSave.setLong( counter, item.getRetentionTimeMaxBinMinusMinBinPlusOne() );
			counter++;
			pstmtSave.setLong( counter, item.getMzMaxBinMinusMinBinPlusOne() );
			counter++;
			pstmtSave.setDouble( counter, item.getIntensityMaxBinMinusMinBin() );

			int rowsUpdated = pstmtSave.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

//			rsGenKeys = pstmtSave.getGeneratedKeys();
//
//			if ( rsGenKeys.next() ) {
//
//				item.setId( rsGenKeys.getInt( 1 ) );
//			}


		} catch (Exception sqlEx) {
			
			String msg = "save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL;
			
			log.error( msg , sqlEx);

			throw sqlEx;

		} finally {

//			if (rsGenKeys != null) {
//				try {
//					rsGenKeys.close();
//				} catch (Exception ex) {
//					// ignore
//				}
//			}

			if (pstmtSave != null) {
				try {
					pstmtSave.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}


	}

}