package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.objects.ImageViewerData;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/imageViewer")
public class ViewerCrosslinkService {

	private static final Logger log = Logger.getLogger(ViewerCrosslinkService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkData") 
	public ImageViewerData getViewerData( @QueryParam( "searchIds" ) List<Integer> searchIds,
										  @QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
										  @QueryParam( "peptideQValueCutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
										  @QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
										  @QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
										  @QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
										  @Context HttpServletRequest request )
	throws Exception {

		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIds;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		

		try {

			// Get the session first.  
			HttpSession session = request.getSession();


			if ( searchIds.isEmpty() ) {
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

					msg += searchId + ", ";
				}				
				log.error( msg );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			//  Test access to the project id

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);

			}



			ImageViewerData ivd = new ImageViewerData();

			if( psmQValueCutoff == null )
				psmQValueCutoff = 0.01;
			
			if( peptideQValueCutoff == null )
				peptideQValueCutoff = 0.01;
			
			if( excludeTaxonomy == null ) 
				excludeTaxonomy = new ArrayList<Integer>();


			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			for( int searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				searches.add( search );
			}



			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;

			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;

			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;

			
			List<MergedSearchProteinCrosslink> crosslinks = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );

			// Filter out links if requested
			if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide 
					|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
				
				List<MergedSearchProteinCrosslink> linksCopy = new ArrayList<MergedSearchProteinCrosslink>();
				linksCopy.addAll( crosslinks );

				for( MergedSearchProteinCrosslink link : linksCopy ) {
					
//					int proteinId1 = link.getProtein1().getNrProtein().getNrseqId();
//					int proteinId2 = link.getProtein2().getNrProtein().getNrseqId();
//					
//					if ( proteinId1 == 23980491 || proteinId2 == 23980492 ) {
//						
//						int z = 0;
//					}
					
					
					

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							crosslinks.remove( link );
							continue;
						}
					}
//
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {
						
						boolean foundSearchWithMoreThanOnePSM = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int psmCountForSearchId = searchProteinCrosslink.getNumPsms();

							if ( psmCountForSearchId > 1 ) {

								foundSearchWithMoreThanOnePSM = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOnePSM ) {
							crosslinks.remove( link );
							continue;
						}

					}
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						boolean foundSearchWithMoreThanOneReportedPeptide = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int peptideCountForSearchId = searchProteinCrosslink.getNumLinkedPeptides();

							if ( peptideCountForSearchId > 1 ) {

								foundSearchWithMoreThanOneReportedPeptide = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
							crosslinks.remove( link );
							continue;
						}
					}
					

					// did they request removal of certain taxonomy IDs?
					if( excludeTaxonomy != null && excludeTaxonomy.size() > 0 ) {
						
						for( int tid : excludeTaxonomy ) {
							
							if( link.getProtein1().getNrProtein().getTaxonomyId() == tid ||
									link.getProtein2().getNrProtein().getTaxonomyId() == tid ) {
								crosslinks.remove( link );
								continue;
							}
						}
					}
				}
			}

			// build the JSON data structure for crosslinks
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLinkPositions = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>>();
			for( MergedSearchProteinCrosslink link : crosslinks ) {

				int fromId = link.getProtein1().getNrProtein().getNrseqId();
				int toId = link.getProtein2().getNrProtein().getNrseqId();
				
				int from = link.getProtein1Position();
				int to = link.getProtein2Position();
				
				Set<Integer> searchIdSet = new HashSet<Integer>();
				for( SearchDTO search : link.getSearches() ) {
					searchIdSet.add( search.getId() );
				}
				
				if( !proteinLinkPositions.containsKey( fromId) )
					proteinLinkPositions.put( fromId, new HashMap<Integer, Map<Integer, Map<Integer, Set<Integer>>>>() );

				if( !proteinLinkPositions.containsKey( toId ) )
					proteinLinkPositions.put( toId, new HashMap<Integer, Map<Integer, Map<Integer, Set<Integer>>>>() );
				
				Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> pMap = proteinLinkPositions.get( fromId );
				if( !pMap.containsKey( toId ) )
					pMap.put( toId, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
				
				if( !pMap.get( toId ).containsKey( from ) )
					pMap.get( toId ).put( from, new HashMap<Integer, Set<Integer>>() );
				
				pMap.get( toId ).get( from ).put( to, searchIdSet );
				
				fromId = link.getProtein2().getNrProtein().getNrseqId();
				toId = link.getProtein1().getNrProtein().getNrseqId();
				
				from = link.getProtein2Position();
				to = link.getProtein1Position();
				
				
				if( !proteinLinkPositions.containsKey( fromId ) )
					proteinLinkPositions.put( fromId, new HashMap<Integer, Map<Integer, Map<Integer, Set<Integer>>>>() );
				
				pMap = proteinLinkPositions.get( fromId );
				if( !pMap.containsKey( toId ) )
					pMap.put( toId, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
				
				if( !pMap.get( toId ).containsKey( from ) )
					pMap.get( toId ).put( from, new HashMap<Integer, Set<Integer>>() );
				
				pMap.get( toId ).get( from ).put( to, searchIdSet );	
			}
			
			ivd.setProteinLinkPositions( proteinLinkPositions );


			return ivd;

			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}


	}
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkPSMCounts") 
	public ImageViewerData getPSMCounts( @QueryParam( "searchIds" ) List<Integer> searchIds,
										  @QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
										  @QueryParam( "peptideQValueCutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
										  @QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
										  @QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
										  @QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
										  @Context HttpServletRequest request )
	throws Exception {

		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIds;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		

		try {

			// Get the session first.  
			HttpSession session = request.getSession();


			if ( searchIds.isEmpty() ) {
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

					msg += searchId + ", ";
				}				
				log.error( msg );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			//  Test access to the project id

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);

			}



			ImageViewerData ivd = new ImageViewerData();

			if( psmQValueCutoff == null )
				psmQValueCutoff = 0.01;
			
			if( peptideQValueCutoff == null )
				peptideQValueCutoff = 0.01;
			
			if( excludeTaxonomy == null ) 
				excludeTaxonomy = new ArrayList<Integer>();

			

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			for( int searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				searches.add( search );
			}



			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;

			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;

			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;

			
			List<MergedSearchProteinCrosslink> crosslinks = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );

			// Filter out links if requested
			if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide 
					|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
				
				List<MergedSearchProteinCrosslink> linksCopy = new ArrayList<MergedSearchProteinCrosslink>();
				linksCopy.addAll( crosslinks );

				for( MergedSearchProteinCrosslink link : linksCopy ) {
					
//					int proteinId1 = link.getProtein1().getNrProtein().getNrseqId();
//					int proteinId2 = link.getProtein2().getNrProtein().getNrseqId();
//					
//					if ( proteinId1 == 23980491 || proteinId2 == 23980492 ) {
//						
//						int z = 0;
//					}
					
					
					

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							crosslinks.remove( link );
							continue;
						}
					}
//
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {
						
						boolean foundSearchWithMoreThanOnePSM = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int psmCountForSearchId = searchProteinCrosslink.getNumPsms();

							if ( psmCountForSearchId > 1 ) {

								foundSearchWithMoreThanOnePSM = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOnePSM ) {
							crosslinks.remove( link );
							continue;
						}

					}
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						boolean foundSearchWithMoreThanOneReportedPeptide = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int peptideCountForSearchId = searchProteinCrosslink.getNumLinkedPeptides();

							if ( peptideCountForSearchId > 1 ) {

								foundSearchWithMoreThanOneReportedPeptide = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
							crosslinks.remove( link );
							continue;
						}
					}
					

					// did they request removal of certain taxonomy IDs?
					if( excludeTaxonomy != null && excludeTaxonomy.size() > 0 ) {
						
						for( int tid : excludeTaxonomy ) {
							
							if( link.getProtein1().getNrProtein().getTaxonomyId() == tid ||
									link.getProtein2().getNrProtein().getTaxonomyId() == tid ) {
								crosslinks.remove( link );
								continue;
							}
						}
					}
				}
			}

			// build the JSON data structure for crosslinks
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> proteinLinkPositions = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>>();
			for( MergedSearchProteinCrosslink link : crosslinks ) {

				int fromId = link.getProtein1().getNrProtein().getNrseqId();
				int toId = link.getProtein2().getNrProtein().getNrseqId();
				
				int from = link.getProtein1Position();
				int to = link.getProtein2Position();
				
				int numPsms = link.getNumPsms();
				
				if( !proteinLinkPositions.containsKey( fromId) )
					proteinLinkPositions.put( fromId, new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>() );

				if( !proteinLinkPositions.containsKey( toId ) )
					proteinLinkPositions.put( toId, new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>() );
				
				Map<Integer, Map<Integer, Map<Integer, Integer>>> pMap = proteinLinkPositions.get( fromId );
				if( !pMap.containsKey( toId ) )
					pMap.put( toId, new HashMap<Integer, Map<Integer, Integer>>() );
				
				if( !pMap.get( toId ).containsKey( from ) )
					pMap.get( toId ).put( from, new HashMap<Integer, Integer>() );
				
				pMap.get( toId ).get( from ).put( to, numPsms );
				
				fromId = link.getProtein2().getNrProtein().getNrseqId();
				toId = link.getProtein1().getNrProtein().getNrseqId();
				
				from = link.getProtein2Position();
				to = link.getProtein1Position();
				
				
				if( !proteinLinkPositions.containsKey( fromId ) )
					proteinLinkPositions.put( fromId, new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>() );
				
				pMap = proteinLinkPositions.get( fromId );
				if( !pMap.containsKey( toId ) )
					pMap.put( toId, new HashMap<Integer, Map<Integer, Integer>>() );
				
				if( !pMap.get( toId ).containsKey( from ) )
					pMap.get( toId ).put( from, new HashMap<Integer, Integer>() );
				
				pMap.get( toId ).get( from ).put( to, numPsms );	
			}
			
			ivd.setCrosslinkPSMCounts( proteinLinkPositions );


			return ivd;

			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}


	}
	
	
}
