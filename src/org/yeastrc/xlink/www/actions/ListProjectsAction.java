package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dto.AuthUserDTO;
//import org.yeastrc.xlink.searchers.ProjectSearcher;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
//import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
//import org.yeastrc.xlink.www.web_utils.RefreshAllowedReadAccessProjectIds;
//import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
//import org.yeastrc.xlink.www.dto.ProjectDTO;

/**
 * 
 *
 */
public class ListProjectsAction extends Action {

	private static final Logger log = Logger.getLogger(ListProjectsAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		try {


			// Get their session first.  
			HttpSession session = request.getSession();



			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request, response );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			//  Test access to application no project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			
			if ( userSessionObject.getUserDBObject() == null || userSessionObject.getUserDBObject().getAuthUser() == null  ) {

				//  No Access Allowed since not a logged in user

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			
			AuthUserDTO authUser = userSessionObject.getUserDBObject().getAuthUser();
			
			if ( ! authUser.isEnabled() ) {
				
				//  No Access Allowed since user is disabled

				return mapping.findForward( StrutsGlobalForwardNames.ACCOUNT_DISABLED );

			}
			
			
			
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			

//			List<ProjectDTO> projects = null;
//			
//			AuthUserDTO authUser = null;
//			
//			if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null ) {
//				
//				authUser = userSessionObject.getUserDBObject().getAuthUser();
//				
//				///  Refresh with latest
//				
//				authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( authUser.getId() );
//				
//				userSessionObject.getUserDBObject().setAuthUser( authUser );
//				
//
//			}
//			
//			if ( authUser != null 
//					&& authUser.getUserAccessLevel() != null
//					&& authUser.getUserAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
//				
//				projects = ProjectSearcher.getInstance().getAllProjects();
//				
//			} else if ( authUser != null 
//				&& authUser.getUserAccessLevel() != null
//				&& authUser.getUserAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_NONE) {
//			
//
//				request.setAttribute( "noAccess", true );
//			
//			} else {
//				
//				Integer authUserId = null;
//				
//				if ( authUser != null ) {
//					
//					authUserId = authUser.getId();
//				}
//				
//				
//				RefreshAllowedReadAccessProjectIds.refreshAllowedReadAccessProjectIds( userSessionObject );
//
//
//				Set<Integer> allowedProjectIds = userSessionObject.getAllowedReadAccessProjectIds();
//
//
//				if ( authUserId == null && ( allowedProjectIds == null || allowedProjectIds.isEmpty() ) ) {
//
//					projects = new ArrayList<ProjectDTO>();
//
//				} else {
//
//					projects = ProjectSearcher.getInstance().getProjectsForAuthUserIdORAllowedProjectIds( authUserId, allowedProjectIds );
//				}
//			}
//			
//			request.setAttribute( "projects", projects );


			return mapping.findForward( "Success" );


		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}

		
}
