package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.DimerDTO;

public class DimerDAO {
	
	private static final Logger log = Logger.getLogger( DimerDAO.class );

	private DimerDAO() { }
	public static DimerDAO getInstance() { return new DimerDAO(); }



	/**
	 * 	 * Gets a random DimerDTO for psmId
	 * 
	 * Do Not Use the Protein data from this 
	 * since there may be other dimer records with different protein id and protein position data  
	 * 
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public DimerDTO getARandomDimerDTOForPsmId( int psmId ) throws Exception {
		
		
		DimerDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM dimer WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				result = new DimerDTO();
				
				result.setId( rs.getInt( "id" ) );
				
				result.setPeptide1Id( rs.getInt( "peptide_1_id" ) );
//				result.setProtein1Id( rs.getInt( "nrseq_id_1" ) );

				result.setPeptide2Id( rs.getInt( "peptide_2_id" ) );
//				result.setProtein2Id( rs.getInt( "nrseq_id_2" ) );
				
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


	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private DimerDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		DimerDTO result = new DimerDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptide1Id( rs.getInt( "peptide_1_id" ) );
//		result.setProtein1Id( rs.getInt( "nrseq_id_1" ) );

		result.setPeptide2Id( rs.getInt( "peptide_2_id" ) );
//		result.setProtein2Id( rs.getInt( "nrseq_id_2" ) );
		
		return result;
	}
	
	
	
	
}
