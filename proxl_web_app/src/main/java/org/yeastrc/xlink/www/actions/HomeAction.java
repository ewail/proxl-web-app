package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * Home page action
 *
 */
public class HomeAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( HomeAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to application no project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( authAccessLevel == null ) {
				//  No Access Level provided
				String msg = "No Access Level provided (authAccessLevel == null), throwing exception";
				log.warn( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed 
				return mapping.findForward( StrutsGlobalForwardNames.LOGIN );
//				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			boolean accountLoggedIn = false; 

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			// testIsUserSignedIn(...) method from A_TestUserLoggedInBaseAction
			if ( TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				accountLoggedIn = true;
				request.setAttribute( "accountLoggedIn", true );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}
	}
}
