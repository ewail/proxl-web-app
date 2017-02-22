package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * Table search
 *
 */
public class SearchDAO {
	
	private static final Logger log = Logger.getLogger(SearchDAO.class);
	
	private SearchDAO() { }
	public static SearchDAO getInstance() { return new SearchDAO(); }
	
	private static final String GET_FROM_PROJECT_SEARCH_ID_SQL =
			"SELECT project_search.search_id, "
			+ " search.path, search.directory_name, "
			+ " search.load_time, search.fasta_filename, search.has_scan_data, "
			+ " project_search.search_name, project_search.project_id, project_search.search_display_order "
			+ " FROM project_search INNER JOIN search ON project_search.search_id = search.id "
			+ " WHERE project_search.id = ?";
	
	/**
	 * Get the given Search from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchDTO getSearchFromProjectSearchId( int id ) throws Exception {
		
		SearchDTO search = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = GET_FROM_PROJECT_SEARCH_ID_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				search = new SearchDTO();
				search.setProjectSearchId( id );
				search.setSearchId( rs.getInt( "search_id" ) );
				search.setFastaFilename( rs.getString( "fasta_filename" ) );
				search.setPath( rs.getString( "path" ) );
				search.setDirectoryName( rs.getString( "directory_name" ) );
				search.setLoad_time( new DateTime( rs.getTimestamp( "load_time" ) ) );
				search.setName( rs.getString( "search_name" ) );
				search.setProjectId( rs.getInt( "project_id" ) );
				int hasScanDataInt = rs.getInt( "has_scan_data" );
				if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == hasScanDataInt ) {
					search.setHasScanData( false );
				} else {
					search.setHasScanData( true );
				}
				search.setDisplayOrder( rs.getInt( "search_display_order" ) );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return search;
	}

	/**
	 * Get the project id for the search id from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getProjectIdFromProjectSearchId( int id ) throws Exception {
		
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT project_id FROM project_search WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = rs.getInt( "project_id" );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return result;
	}
	
	
	///  Removed since field 'project_id' no longer on 'search' table
//	/**
//	 * Get the project id for the search id from the database
//	 * 
//	 * @param id
//	 * @return
//	 * @throws Exception
//	 */
//	public Integer getSearchProjectId( int id ) throws Exception {
//		
//		Integer result = null;
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		String sql = "SELECT project_id FROM search WHERE id = ?";
//		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setInt( 1, id );
//			rs = pstmt.executeQuery();
//			if( rs.next() ) {
//				result = rs.getInt( "project_id" );
//			}
//		} catch ( Exception e ) {
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
//			throw e;
//		} finally {
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//		}
//		return result;
//	}
	

}
