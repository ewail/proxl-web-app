package org.yeastrc.xlink.www.actions;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dao.ProjectSearchDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 *  Marks record in project_search as deleted
 *
 */
public class DeleteSearchAction extends Action {

	private static final Logger log = Logger.getLogger(DeleteSearchAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			int projectSearchId = Integer.parseInt( request.getParameter( "projectSearchId" ) );
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromProjectSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isSearchDeleteAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			
			//  Auth complete
			
//			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			AuthUserDTO authUserDTO = userSessionObject.getUserDBObject().getAuthUser();
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
//			SearchDAO.getInstance().deleteSearch( searchId );
			ProjectSearchDAO.getInstance().markAsDeleted( projectSearchId, authUserDTO.getId() );
			
			try {
				String msg = "Project Search id " + projectSearchId 
						+ " successfully deleted by user (username: " +  authUserDTO.getUsername() 
						+ ", user id: " + authUserDTO.getId() 
						+ "), IP of request: " + request.getRemoteAddr()
						+ ", Search path: " + search.getPath()
						+ ", Search name: " + search.getName();
				log.warn( msg );
			} catch ( Exception e ) {
				log.warn( "Error logging delete search message", e );
			}
			ActionForward actionForward =  mapping.findForward( "Success" );
			String actionPath = actionForward.getPath();
			String redirectURL = CurrentContext.getCurrentWebAppContext() + actionPath 
					+ "?" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId;
			response.sendRedirect( redirectURL );
			
			return null;  // nothing to forward to since setting redirect here
			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
