package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Return a list of users in the database for a shared object id
 *
 * This is not in Auth_Library since it uses access level constants in the Crosslinking web app
 *
 */
public class UsersForSharedObjectIdSearcher {

	private static final Logger log = Logger.getLogger(UsersForSharedObjectIdSearcher.class);
	
	private UsersForSharedObjectIdSearcher() { }
	private static final UsersForSharedObjectIdSearcher _INSTANCE = new UsersForSharedObjectIdSearcher();
	public static UsersForSharedObjectIdSearcher getInstance() { return _INSTANCE; }
	
	
	

	
	/**
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getAuthUserIdsExcludeGlobalNoAccessDisabledAccountsForSharedObjectId( int sharedObjectId ) throws Exception {
		

		
//		CREATE TABLE IF NOT EXISTS crosslinks.auth_shared_object (
//				  shared_object_id INT UNSIGNED NOT NULL,
//				  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
//				  public_access_code VARCHAR(255) NULL,

//		CREATE TABLE IF NOT EXISTS auth_shared_object_users (
//				  shared_object_id INT UNSIGNED NOT NULL,
//				  user_id INT UNSIGNED NOT NULL,
//				  access_level SMALLINT UNSIGNED NOT NULL,
			
		
		List<Integer> userIds = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT auth_shared_object_users.user_id FROM "
				+ " auth_shared_object  "
				+ " INNER JOIN auth_shared_object_users ON auth_shared_object.shared_object_id = auth_shared_object_users.shared_object_id "
				+ " WHERE  auth_shared_object.shared_object_id = ? "

		 		+ "  AND auth_shared_object_users.user_id NOT IN "
		
		 		+    " ( "
		
		 		+     " SELECT DISTINCT auth_user.id FROM auth_user"
		
				+      " WHERE "
				+      "     auth_user.user_access_level = " + AuthAccessLevelConstants.ACCESS_LEVEL_NONE
				+      " OR  auth_user.enabled = 0" 
				+     " ) ";
 		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, sharedObjectId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {

				userIds.add( rs.getInt( 1 ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getAuthUserIdForProjectId(), sql: " + sql;
			
			log.error( msg, e );
			
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
		
		
		
		return userIds;
	}
	
		
	
}