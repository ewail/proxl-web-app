package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LooplinkDTO;

/**
 * 
 *
 */
public class LooplinkDAO {
	
	private static final Logger log = Logger.getLogger(LooplinkDAO.class);

	private LooplinkDAO() { }
	public static LooplinkDAO getInstance() { return new LooplinkDAO(); }

	


	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LooplinkDTO getLooplinkDTOById( int id ) throws Exception {
		
		
		LooplinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM looplink WHERE id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = populateFromResultSet(rs);
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
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public LooplinkDTO getLooplinkDTOByPsmId( int psmId ) throws Exception {
		
		
		LooplinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM looplink WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = populateFromResultSet(rs);
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
	private LooplinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		LooplinkDTO result = new LooplinkDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );

		result.setPeptidePosition1( rs.getInt( "peptide_position_1" ) );
		result.setProteinPosition1( rs.getInt( "protein_position_1" ) );

		result.setPeptidePosition2( rs.getInt( "peptide_position_2" ) );
		result.setProteinPosition2( rs.getInt( "protein_position_2" ) );
		
		result.setLinkerMass(  rs.getBigDecimal( "linker_mass" ) );
		
		return result;
	}
	

	
	
}
