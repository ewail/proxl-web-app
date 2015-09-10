package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.SearchWebLinksDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchWebLinksDTO;
import org.yeastrc.xlink.dto.SearchDTO;

public class SearchWebLinksSearcher {

	private SearchWebLinksSearcher() { }
	private static final SearchWebLinksSearcher _INSTANCE = new SearchWebLinksSearcher();
	public static SearchWebLinksSearcher getInstance() { return _INSTANCE; }
	
	public List<SearchWebLinksDTO> getWebLinksForSearch( SearchDTO search ) throws Exception {
		List<SearchWebLinksDTO> comments = new ArrayList<SearchWebLinksDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT id FROM search_web_links WHERE search_id = ? ORDER BY link_label";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, search.getId() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				comments.add( SearchWebLinksDAO.getInstance().load( rs.getInt( 1 ) ) );
			}
			
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
		
		return comments;
	}
	
}
