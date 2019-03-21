package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetCutoffsAppliedOnImport;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * QC data page - Single and Multiple searches
 *
 */
public class ViewMergedSearchQCAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchQCAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm) actionForm;
			// Get the session first.  
//			HttpSession session = request.getSession();
			int[] projectSearchIdsFromForm = form.getProjectSearchId();
			if ( projectSearchIdsFromForm.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdsFromForm ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIds: ";
				for ( int projectSearchId : projectSearchIdsFromForm ) {
					msg += projectSearchId + ", ";
				}
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 
			request.setAttribute( "project_id", projectId );
			///////////////////////
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			if ( projectSearchIdsListDeduppedSorted.size() == 1 ) {
				int onlySingleProjectSearchId = projectSearchIdsListDeduppedSorted.get( 0 );
				request.setAttribute( "onlySingleProjectSearchId", onlySingleProjectSearchId );	
			}

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			boolean anySearchesHaveScanData = true;
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();

			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			
			for ( int projectSearchId : projectSearchIdsFromForm ) {
				if ( projectSearchIdsProcessedFromForm.add( projectSearchId ) ) {
					//  Haven't processed this projectSearchId yet in this loop so process it now
					SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
					if ( search == null ) {
						String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
						log.warn( msg );
						//  Search not found, the data on the page they are requesting does not exist.
						//  The data on the user's previous page no longer reflects what is in the database.
						//  Take the user to the home page
						return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
					}
					searches.add( search );
					searchesMapOnSearchId.put( search.getSearchId(), search );

					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
					
					if ( ! search.isHasScanData() ) {
						anySearchesHaveScanData = false;
					}
				}
			}
			
			if ( ! PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {

				// Sort searches list
				Collections.sort( searches, new Comparator<SearchDTO>() {
					@Override
					public int compare(SearchDTO o1, SearchDTO o2) {
						return o1.getSearchId() - o2.getSearchId();
					}
				});
			}

			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			//  Create projectSearchIdsList in the same order as the searches
			List<Integer> projectSearchIdsList = new ArrayList<Integer>();

			Collection<Integer> searchIds = new HashSet<>();
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
			
			for ( SearchDTO search : searches ) {
				searchIds.add( search.getSearchId() );
				searchIdsListDeduppedSorted.add( search.getSearchId() );
				mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );

				projectSearchIdsList.add( search.getProjectSearchId() );
			}

			request.setAttribute( "projectSearchIds", projectSearchIdsList );

			
			request.setAttribute( "anySearchesHaveScanData", anySearchesHaveScanData );
			

			request.setAttribute( "searches", searches );
			
			if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
				request.setAttribute( "userOrderedProjectSearchIds", PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES );
			}
			
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
			
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( searches, request );
			
			boolean showStructureLink = true;
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
			} else {
				//  Public access user:
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			String annotation_data_webservice_base_url = 
					ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY );
			request.setAttribute( "annotation_data_webservice_base_url", annotation_data_webservice_base_url );

			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );
			List<String> modMassStringsList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassStringsList.add( modMassAsString );
			}
			request.setAttribute( "modMassFilterList", modMassStringsList );
			
			if ( searchIdsArray.length == 1 ) {
				String cutoffsAppliedOnImportAllAsString =
						GetCutoffsAppliedOnImport.getInstance().getCutoffsAppliedOnImportAllAsString( searchIdsArray[ 0 ] );
				request.setAttribute( "cutoffsAppliedOnImportAllAsString", cutoffsAppliedOnImportAllAsString );
//			} else {
//				String msg = "Code for cutoffsAppliedOnImportAllAsString currently only supports 1 search id.";
//				log.error( msg );
//				throw new ProxlWebappInternalErrorException(msg);
			}
			
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			CutoffValuesRootLevel cutoffValuesRootLevelCutoffDefaults =
					GetDefaultPsmPeptideCutoffs.getInstance()
					.getDefaultPsmPeptideCutoffs( projectSearchIdsListDeduppedSorted, searchIds, mapProjectSearchIdToSearchId );
			String cutoffValuesRootLevelCutoffDefaultsJSONString = jacksonJSON_Mapper.writeValueAsString( cutoffValuesRootLevelCutoffDefaults );
			request.setAttribute( "cutoffValuesRootLevelCutoffDefaults", cutoffValuesRootLevelCutoffDefaultsJSONString );
			//  This builds an object for the cutoff selection block on the page
			
			//  So certain sections treat this like coverage page.  ie: no selection of annotation data to display
			request.setAttribute( "coveragePageForAnnDispMgmt", true );
			
			if ( searchIdsArray.length == 1 ) {
				return mapping.findForward( "SuccessSingle" );
			}
			return mapping.findForward( "SuccessMerged" );
			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}