package org.yeastrc.xlink.www.user_account;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddOrUpdateProjectAccessExistingUserUsingDBTransactionService;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;

/**
 * 
 *
 */
public class UserInviteProcessCodeAction extends Action {
	
	private static final Logger log = Logger.getLogger(UserInviteProcessCodeAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {


			String userIP = request.getRemoteAddr();

			String inviteTrackingCode = request.getParameter( WebConstants.PARAMETER_INVITE_CODE );

			
			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteTrackingCode );

			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {

				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
	
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( errorMsgKey ) );
				saveErrors( request, messages );
				
				return mapping.findForward("Failure");
			}
			
			
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO =  validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();
			
			authUserInviteTrackingDTO.setUseIP( userIP );
			

			// Get the session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );


			if ( TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {
				

				if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() != null ) {
					
					//  Logged in And a Project Id
					
					
					
					//  update access for this user for this project
					AuthSharedObjectUsersDAO authSharedObjectUsersDAO = AuthSharedObjectUsersDAO.getInstance();
					
					AddOrUpdateProjectAccessExistingUserUsingDBTransactionService addOrUpdateProjectAccessExistingUserUsingDBTransactionService = AddOrUpdateProjectAccessExistingUserUsingDBTransactionService.getInstance();
					
					
					AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();

					authSharedObjectUsersDTO.setUserId( userSessionObject.getUserDBObject().getAuthUser().getId() );
					authSharedObjectUsersDTO.setSharedObjectId( authUserInviteTrackingDTO.getInvitedSharedObjectId() );
					authSharedObjectUsersDTO.setAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );
					
					try {
						
					
						addOrUpdateProjectAccessExistingUserUsingDBTransactionService.updateUserAddAuthSharedObjectUsersDTO( authSharedObjectUsersDTO, authUserInviteTrackingDTO );
					
					} catch ( SQLException sqlException ) {
						
						String exceptionMessage = sqlException.getMessage();
						
						if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {
							
							AuthSharedObjectUsersDTO existingAuthSharedObjectUsersDTO = authSharedObjectUsersDAO.getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( authUserInviteTrackingDTO.getInvitedSharedObjectId(), userSessionObject.getUserDBObject().getAuthUser().getId() );
							
							if ( existingAuthSharedObjectUsersDTO != null ) {
								
								if ( authUserInviteTrackingDTO.getInvitedUserAccessLevel()  < existingAuthSharedObjectUsersDTO.getAccessLevel() ) {
									
									//  New invite has better access level so update the access level
									
									addOrUpdateProjectAccessExistingUserUsingDBTransactionService.updateUserUpdateUserAccessLevel( authSharedObjectUsersDTO , authUserInviteTrackingDTO );
									
								} else {
									
									//  User already has access to this project.  Mark invite complete
									
									String msg = "User already has access to this project.  Mark invite complete: authUserInviteTrackingDTO.getId(): " + authUserInviteTrackingDTO.getId();
									
									log.warn( msg );
									
									int authUserIdUsingInvite = userSessionObject.getUserDBObject().getAuthUser().getId();
									
									AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingDTO.getId(), authUserIdUsingInvite, userIP );
									
									return mapping.findForward("GoToProjectList");

									
//									ActionErrors errors = new ActionErrors();
//									errors.add("username", new ActionMessage("error.invite.existing.user.already.have.access"));
//									saveErrors( request, errors );
//									
//									return mapping.findForward("Failure");
								}
							}
							
						} else {
							
							String msg = "SQL Exception: ";
							
							log.error( msg, sqlException );
							
							throw sqlException;
						}
						
					}
				
					
					return mapping.findForward("GoToProjectList");
				
				} else {
					
					//   Logged in and NO Project Id
					
					//  Do Nothing
					
					//  User already has access to this app.  Mark invite complete
					
					String msg = "User already has access to this App.  Mark invite complete: authUserInviteTrackingDTO.getId(): " + authUserInviteTrackingDTO.getId();
					
					log.warn( msg );
					
					int authUserIdUsingInvite = userSessionObject.getUserDBObject().getAuthUser().getId();
					
					AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingDTO.getId(), authUserIdUsingInvite, userIP );
					
					return mapping.findForward("GoToProjectList");

				}
			
			}   

			//  Not Logged In
			

			if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() != null ) {
			
				//  Not logged in and processing a project id
			
			
				return mapping.findForward("ProjectInviteLandingPage");
			
			}
			
			//  Not Logged in and No project Id 
			
			return mapping.findForward("AddNewUser");
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.invite.process.code.general"));
			saveErrors( request, errors );

			return mapping.findForward("Failure");
		}

	}
	
}
