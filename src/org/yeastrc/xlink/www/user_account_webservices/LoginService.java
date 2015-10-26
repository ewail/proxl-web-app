package org.yeastrc.xlink.www.user_account_webservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.hash_password.HashedPasswordProcessing;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.LoginResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;



@Path("/user")
public class LoginService {

	private static final Logger log = Logger.getLogger(LoginService.class);
	
	

	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login") 
	public Response loginService(   
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@Context HttpServletRequest request )
	throws Exception {
		
		
		LoginResult loginResult = loginServiceLocal( username, password, request );
		
//		.cookie(new NewCookie("name", "Hello, world!"))
		
		return Response.ok(loginResult).build();
	}
	
	
	
	private LoginResult loginServiceLocal(   
			String username,
			String password,
			HttpServletRequest request ) {

		LoginResult loginResult = new LoginResult();


		if ( StringUtils.isEmpty( username ) ) {

			log.warn( "LoginService:  username empty: " + username );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		if ( StringUtils.isEmpty( password ) ) {

			log.warn( "LoginService:  password empty: " + password );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}

		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();

			
			// Make sure this username exists!		
			XLinkUserDTO userDatabaseRecord;
			userDatabaseRecord = XLinkUserDAO.getInstance().getXLinkUserDTOForUsername( username );


			if ( userDatabaseRecord == null ) {
				
		        loginResult.setInvalidUserOrPassword(true);
				
				return loginResult;  //  Early Exit
			}


			
			String userDatabasePasswordHashed = AuthUserDAO.getInstance().getPasswordHashedForId( userDatabaseRecord.getAuthUser().getId() );

			if ( ! HashedPasswordProcessing.getInstance().comparePasswordToHashedPasswordHex( password, userDatabasePasswordHashed ) ) {
				// Invalid password
				
		        loginResult.setInvalidUserOrPassword(true);
				
				return loginResult;  //  Early Exit
			}
			
			
			
			AuthUserDTO authUserDTO = userDatabaseRecord.getAuthUser();
			
			if ( ! authUserDTO.isEnabled() ) {
				
		        loginResult.setDisabledUser(true);
				
				return loginResult;  //  Early Exit
				
			}

			// Save the login info in the user.
			userDatabaseRecord.getAuthUser().setLastLogin(new java.util.Date());
			userDatabaseRecord.getAuthUser().setLastLoginIP( request.getRemoteAddr() );

			XLinkUserDAO.getInstance().updateLastLogin( userDatabaseRecord.getAuthUser().getId(),  request.getRemoteAddr() );

//			LastLoginUpdaterObject lastLoginUpdaterObject = new LastLoginUpdaterObject();
//			
//			lastLoginUpdaterObject.setAuthUserDTO( authUserDTO );
//			
//			LastLoginUpdaterQueue.addLastLoginUpdaterObject(lastLoginUpdaterObject);
			
			

			UserSessionObject userSessionObject = new UserSessionObject();

			userSessionObject.setUserDBObject( userDatabaseRecord );

			session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );

			
	        loginResult.setStatus(true);
			
			return loginResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
				
	}
	


}
