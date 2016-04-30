package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCrosslinkBestPSMValueGenericLookupDTO;


/**
 * table search_crosslink_best_psm_value_generic_lookup
 *
 */
public class DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO.class);

	private DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO() { }
	public static DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO getInstance() { return new DB_Insert_SearchCrosslinkBestPSMValueGenericLookupDAO(); }
	

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchCrosslinkBestPSMValueGenericLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO search_crosslink_best_psm_value_generic_lookup "

				+ " ( search_crosslink_generic_lookup_id, search_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
				+   " annotation_type_id, best_psm_value_for_ann_type_id, best_psm_value_string_for_ann_type_id )"

				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchCrosslinkGenericLookup() );
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId1() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId2() );
			counter++;
			pstmt.setInt( counter,  item.getProtein1Position() );
			counter++;
			pstmt.setInt( counter,  item.getProtein2Position() );
			
			counter++;
			pstmt.setInt( counter,  item.getAnnotationTypeId() );
			counter++;
			pstmt.setDouble( counter,  item.getBestPsmValueForAnnTypeId() );
			counter++;
			pstmt.setString( counter,  item.getBestPsmValueStringForAnnTypeId() );
			
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert record..." );
			
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
	}
}
