package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.psm_level_data.main.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs;
import org.yeastrc.xlink.www.qc_data.psm_level_data.main.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
import org.yeastrc.xlink.www.qc_data.utils.QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/qc/dataPage")
public class QC_PeptideLengthVsPSMCountService {

	private static final Logger log = LoggerFactory.getLogger( QC_PeptideLengthVsPSMCountService.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/peptideLengthVsPSMCount") 
	public WebserviceResult_getQC_PeptideLengthsHistogram
		getQC_PeptideLengthsHistogram( 
				byte[] requestJSONBytes,
				@Context HttpServletRequest request )
	throws Exception {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
		try {
			qcPageRequestJSONRoot =
					QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.getInstance().deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONBytes );
		} catch ( Exception e ) {
			String msg = "parse request failed";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}

		List<Integer> projectSearchIdList = qcPageRequestJSONRoot.getProjectSearchIds();
		QCPageQueryJSONRoot qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();

		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( qcPageQueryJSONRoot == null ) {
			String msg = "qcPageQueryJSONRoot is null or qcPageQueryJSONRoot is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		
		int projectSearchId = projectSearchIdList.get( 0 );  // Since only 1 is allowed

		try {
			//   Get the project id for this search
			//   Get the project id for these searches
			
			
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				@SuppressWarnings("unchecked")
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdList );
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
				log.warn( msg );
				//  Search not found, the data on the page they are requesting does not exist.
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);			
			}
			
			PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response methodResponse =
					PeptideLengthVsPSMCount_For_PSMPeptideCutoffs.getInstance()
					.getPeptideLengthVsPSMCount_For_PSMPeptideCutoffs( 
							ForDownload_Enum.NO,
							qcPageQueryJSONRoot, search );

			PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults =
					methodResponse.getPeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults();
				
			//  Get PSMs for cutoffs and other data
			WebserviceResult_getQC_PeptideLengthsHistogram serviceResult = new WebserviceResult_getQC_PeptideLengthsHistogram();
			
			serviceResult.peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults = peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
			
			return serviceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
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
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResult_getQC_PeptideLengthsHistogram {
		private PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;

		public PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults getPeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults() {
			return peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		}

		public void setPeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults(
				PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults) {
			this.peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults = peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		}


	}
}
