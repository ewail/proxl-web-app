package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.internal_services.UpdateAuthUserUserAccessLevelEnabled;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;
import org.yeastrc.xlink.www.web_utils.GetProjectListForCurrentLoggedInUser;

@Path("/project")
public class ProjectListForCurrentUserService {

	private static final Logger log = LoggerFactory.getLogger( ProjectListForCurrentUserService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listForCurrentUser")
	public ProjectListForCurrentUserServiceResponse listProjects( 
			@Context HttpServletRequest request ) throws Exception {
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( userSessionObject.getUserDBObject() == null || userSessionObject.getUserDBObject().getAuthUser() == null  ) {
				//  No Access Allowed since not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			List<ProjectTblSubPartsForProjectLists> projects = GetProjectListForCurrentLoggedInUser.getInstance().getProjectListForCurrentLoggedInUser( request );
			AuthUserDTO authUser = userSessionObject.getUserDBObject().getAuthUser();
			///  Refresh with latest
			UpdateAuthUserUserAccessLevelEnabled.getInstance().updateAuthUserUserAccessLevelEnabled( authUser );
			if ( authUser != null ) {
				//  Add projects user invited to
			}
			List<ProjectWithUserAccessLevel> projectList = new ArrayList<ProjectWithUserAccessLevel>( projects.size() );
			for ( ProjectTblSubPartsForProjectLists project : projects ) {
				ProjectWithUserAccessLevel projectWithUserAccessLevel = new ProjectWithUserAccessLevel();
				projectList.add( projectWithUserAccessLevel );
				projectWithUserAccessLevel.setProject( project );
				//  Test access to the project id
				AuthAccessLevel authAccessLevelPerProject = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, project.getId() );
				if ( authAccessLevelPerProject.isProjectOwnerAllowed() && ! project.isProjectLocked() ) {
					//  Delete access allowed to Project Owner or Admin
					projectWithUserAccessLevel.setCanDelete(true);
					//  Upload access allowed to Project Owner or Admin
					projectWithUserAccessLevel.setCanUpload(true);
				}
			}
			ProjectListForCurrentUserServiceResponse projectListForCurrentUserServiceResponse = new ProjectListForCurrentUserServiceResponse();
			projectListForCurrentUserServiceResponse.projectList = projectList;
			return projectListForCurrentUserServiceResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	public static class ProjectListForCurrentUserServiceResponse {
		List<ProjectWithUserAccessLevel> projectList;
		public List<ProjectWithUserAccessLevel> getProjectList() {
			return projectList;
		}
		public void setProjectList(List<ProjectWithUserAccessLevel> projectList) {
			this.projectList = projectList;
		}
	}
	
	public static class ProjectWithUserAccessLevel {
		private ProjectTblSubPartsForProjectLists project;
		private boolean canDelete;
		private boolean canUpload;
		public ProjectTblSubPartsForProjectLists getProject() {
			return project;
		}
		public void setProject(ProjectTblSubPartsForProjectLists project) {
			this.project = project;
		}
		public boolean isCanDelete() {
			return canDelete;
		}
		public void setCanDelete(boolean canDelete) {
			this.canDelete = canDelete;
		}
		public boolean isCanUpload() {
			return canUpload;
		}
		public void setCanUpload(boolean canUpload) {
			this.canUpload = canUpload;
		}
	}
}