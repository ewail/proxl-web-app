package org.yeastrc.xlink.www.linked_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher.ReturnOnlyReportedPeptidesWithMonolinks;
import org.yeastrc.xlink.www.searcher.SearchPeptideLooplink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionLooplinkSearcher;

/**
 *  Build lists of various objects for looplink data from linked positions tables
 *  
 *  Objects of classes SearchProteinLooplinkWrapper
 *
 */
public class LooplinkLinkedPositions {
	
	private static final Logger log = Logger.getLogger(LooplinkLinkedPositions.class);

	private LooplinkLinkedPositions() { }
	private static final LooplinkLinkedPositions _INSTANCE = new LooplinkLinkedPositions();
	public static LooplinkLinkedPositions getInstance() { return _INSTANCE; }
	
	private static class RepPept_Stage_1_Wrapper {
		
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = new ArrayList<>();;
	}
	
	private static enum PeptidePsm { PEPTIDE, PSM }
	
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public List<SearchProteinLooplinkWrapper> getSearchProteinLooplinkWrapperList( SearchDTO search, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
	
		int searchId = search.getId();
		
		String[] linkTypesLooplink = { PeptideViewLinkTypesConstants.LOOPLINK_PSM };

		List<WebReportedPeptideWrapper> wrappedPeptidelinks =
				PeptideWebPageSearcher.getInstance()
				.searchOnSearchIdPsmCutoffPeptideCutoff( search, searcherCutoffValuesSearchLevel, linkTypesLooplink, null /* modMassSelections */, ReturnOnlyReportedPeptidesWithMonolinks.NO );
	
		
		//  Build a structure of SrchRepPeptProtSeqIdPosDTO
		//  Mapped on Reported Peptide Id, searchReportedPeptidepeptideId (PK table srch_rep_pept__peptide)
		
		SearchReportedPeptideProteinSequencePositionLooplinkSearcher searchReportedPeptideProteinSequencePositionLooplinkSearcher =
				SearchReportedPeptideProteinSequencePositionLooplinkSearcher.getInstance();
		
		//  Process into Map of protein, position 1, position 2 objects
		
		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//
		
		Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedProtPos1Pos2 = new HashMap<>();
		
		
		for ( WebReportedPeptideWrapper wrappedPeptidelink : wrappedPeptidelinks ) {
			
			WebReportedPeptide webReportedPeptide = wrappedPeptidelink.getWebReportedPeptide();
			
			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			List<SrchRepPeptProtSeqIdPosLooplinkDTO> srchRepPeptProtSeqIdPosLooplinkDTOList = searchReportedPeptideProteinSequencePositionLooplinkSearcher.getSrchRepPeptProtSeqIdPosLooplinkDTOList( searchId, reportedPeptideId );
			
			for ( SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO : srchRepPeptProtSeqIdPosLooplinkDTOList ) {
			

				//  Process into Map of protein, position 1, position 2 objects

				// Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedProtPos1Pos2 = new HashMap<>();
				

				Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedPos1Pos2 =
						repPept_Stage_1_Wrapper_MappedProtPos1Pos2.get( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceId() );

				if ( repPept_Stage_1_Wrapper_MappedPos1Pos2 == null ) {

					repPept_Stage_1_Wrapper_MappedPos1Pos2 = new HashMap<>();
					repPept_Stage_1_Wrapper_MappedProtPos1Pos2.put( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceId(), repPept_Stage_1_Wrapper_MappedPos1Pos2 );
				}

				
				
				Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2 =
						repPept_Stage_1_Wrapper_MappedPos1Pos2.get( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_1() );

				if ( repPept_Stage_1_Wrapper_MappedPos2 == null ) {

					repPept_Stage_1_Wrapper_MappedPos2 = new HashMap<>();
					repPept_Stage_1_Wrapper_MappedPos1Pos2.put( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_1(), repPept_Stage_1_Wrapper_MappedPos2 );
				}


				RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = repPept_Stage_1_Wrapper_MappedPos2.get( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_2() );

				if ( repPept_Stage_1_Wrapper == null ) {

					repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
					repPept_Stage_1_Wrapper_MappedPos2.put( srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_2(), repPept_Stage_1_Wrapper );
				}

				boolean reportedPeptideIdAlreadyInList = false;

				for ( WebReportedPeptideWrapper itemInList : repPept_Stage_1_Wrapper.webReportedPeptideWrapperList ) {

					if ( itemInList.getWebReportedPeptide().getReportedPeptideId() == reportedPeptideId.intValue() ) {

						reportedPeptideIdAlreadyInList = true;
					}
				}

				if ( reportedPeptideIdAlreadyInList ) {

					continue;  //  EARLY CONTINUE    skip since this reported peptide is already in this list
				}

				repPept_Stage_1_Wrapper.webReportedPeptideWrapperList.add( wrappedPeptidelink );
				
			}
		}
		
		
		////////////////////////////
		
		Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map = new HashMap<>();
		


		List<SearchProteinLooplinkWrapper> wrappedLinks = new ArrayList<>();

		
		
		//  Process Map of protein, position 1, position 2 objects

		//     The innermost Map contains a RepPept_Stage_1_Wrapper object 
		//              which currently contains List<WebReportedPeptideWrapper> webReportedPeptideWrapperList
		//

		// Map<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedProtPos1Pos2 = new HashMap<>();
		
		for ( Map.Entry<Integer,Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>>> repPept_Stage_1_Wrapper_MappedProtPos1Pos2_Entry :
			repPept_Stage_1_Wrapper_MappedProtPos1Pos2.entrySet() ) {
		
			Integer proteinId = repPept_Stage_1_Wrapper_MappedProtPos1Pos2_Entry.getKey();
			
			Map<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedPos1Pos2 =
					repPept_Stage_1_Wrapper_MappedProtPos1Pos2_Entry.getValue();

			for ( Map.Entry<Integer,Map<Integer,RepPept_Stage_1_Wrapper>> repPept_Stage_1_Wrapper_MappedPos1Pos2_Entry :
				repPept_Stage_1_Wrapper_MappedPos1Pos2.entrySet() ) {

				Integer proteinPosition_1 = repPept_Stage_1_Wrapper_MappedPos1Pos2_Entry.getKey();

				Map<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2 =
						repPept_Stage_1_Wrapper_MappedPos1Pos2_Entry.getValue();

				for ( Map.Entry<Integer,RepPept_Stage_1_Wrapper> repPept_Stage_1_Wrapper_MappedPos2_Entry :
					repPept_Stage_1_Wrapper_MappedPos2.entrySet() ) {

					Integer proteinPosition_2 = repPept_Stage_1_Wrapper_MappedPos2_Entry.getKey();

					RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper =
							repPept_Stage_1_Wrapper_MappedPos2_Entry.getValue();


					SearchProteinLooplinkWrapper searchProteinLooplinkWrapper =

							populateSearchProteinLooplinkWrapper(
									search, 
									searcherCutoffValuesSearchLevel,
									proteinId, 
									proteinPosition_1, 
									proteinPosition_2, 
									searchProtein_KeyOn_PROT_SEQ_ID_Map,
									repPept_Stage_1_Wrapper );


					if ( searchProteinLooplinkWrapper == null ) {

						//  !!!!!!!   This isn't really a Protein that meets the cutoffs

						continue;  //  EARY LOOP ENTRY EXIT
					}

					wrappedLinks.add( searchProteinLooplinkWrapper );
					
				}
			}
		}
		

		return wrappedLinks;
	}
	
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein1
	 * @param protein2
	 * @param position1
	 * @param position2
	 * @return
	 * @throws Exception
	 */
	public SearchProteinLooplinkWrapper getSearchProteinLooplinkWrapperForSearchCutoffsProtIdsPositions( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			ProteinSequenceObject protein, 
			int position1, 
			int position2 ) throws Exception {
		

		Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map = new HashMap<>();
		
		RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper = new RepPept_Stage_1_Wrapper();
		
		List<SearchPeptideLooplinkAnnDataWrapper> searchPeptideLooplinkAnnDataWrapper_List = 
				SearchPeptideLooplink_LinkedPosition_Searcher.getInstance()
				.searchOnSearchProteinLooplink( 
						search.getId(), 
						searcherCutoffValuesSearchLevel, 
						protein.getProteinSequenceId(), 
						position1, 
						position2 );

		for ( SearchPeptideLooplinkAnnDataWrapper searchPeptideLooplinkAnnDataWrapper : searchPeptideLooplinkAnnDataWrapper_List ) {
		
			SearchPeptideLooplink searchPeptideLooplink = searchPeptideLooplinkAnnDataWrapper.getSearchPeptideLooplink();
			
			WebReportedPeptide webReportedPeptide = new WebReportedPeptide();
			
			webReportedPeptide.setSearch( search );
			webReportedPeptide.setSearchId( search.getId() );
			webReportedPeptide.setReportedPeptideId( searchPeptideLooplink.getReportedPeptideId() );
			
			webReportedPeptide.setNumPsms( searchPeptideLooplink.getNumPsms() );
			
			webReportedPeptide.setSearchPeptideLooplink( searchPeptideLooplink );
			
			
			WebReportedPeptideWrapper wrappedPeptidelink = new WebReportedPeptideWrapper();
			
			wrappedPeptidelink.setWebReportedPeptide( webReportedPeptide );
			
			wrappedPeptidelink.setPeptideAnnotationDTOMap( searchPeptideLooplinkAnnDataWrapper.getPeptideAnnotationDTOMap() );
			wrappedPeptidelink.setPsmAnnotationDTOMap( searchPeptideLooplinkAnnDataWrapper.getPsmAnnotationDTOMap() );

			repPept_Stage_1_Wrapper.webReportedPeptideWrapperList.add( wrappedPeptidelink );

		}
		
		SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = 
				populateSearchProteinLooplinkWrapper(
						search, 
						searcherCutoffValuesSearchLevel, 
						protein.getProteinSequenceId(), 
						position1, 
						position2, 
						searchProtein_KeyOn_PROT_SEQ_ID_Map, 
						repPept_Stage_1_Wrapper );
		
		return searchProteinLooplinkWrapper;
	}


	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param searchId
	 * @param searchProtein_KeyOn_PROT_SEQ_ID_Map
	 * @param proteinId
	 * @param proteinPosition_1
	 * @param proteinPosition_2
	 * @param repPept_Stage_1_Wrapper
	 * @return
	 * @throws Exception
	 */
	private SearchProteinLooplinkWrapper populateSearchProteinLooplinkWrapper(
			SearchDTO search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			Integer proteinId, 
			Integer proteinPosition_1,
			Integer proteinPosition_2,
			Map<Integer, SearchProtein> searchProtein_KeyOn_PROT_SEQ_ID_Map,
			RepPept_Stage_1_Wrapper repPept_Stage_1_Wrapper) throws Exception {
		
		
		List<WebReportedPeptideWrapper> webReportedPeptideWrapperList = repPept_Stage_1_Wrapper.webReportedPeptideWrapperList;

		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOMap = new HashMap<>();
		Map<Integer, AnnotationDataBaseDTO> bestPeptideAnnotationDTOMap = new HashMap<>();
		
		int numPsms = 0;
		int numLinkedPeptides = 0;
		int numUniqueLinkedPeptides = 0;
		
		Set<Integer> reportedPeptideIds = new HashSet<>();
		Set<Integer> reportedPeptideIdsRelatedPeptidesUnique = new HashSet<>();
		
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : webReportedPeptideWrapperList ) {
			
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();

			Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			numLinkedPeptides++;
			
			reportedPeptideIds.add( reportedPeptideId );
			
			boolean areRelatedPeptidesUnique =
					Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher.getInstance()
					.get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId( search.getId(), reportedPeptideId );
			
			if ( areRelatedPeptidesUnique ) {

				numUniqueLinkedPeptides++;
				
				reportedPeptideIdsRelatedPeptidesUnique.add( reportedPeptideId );
			}
			
			numPsms += webReportedPeptide.getNumPsms();

			updateBestAnnotationValues( 
					bestPsmAnnotationDTOMap, 
					webReportedPeptideWrapper.getPsmAnnotationDTOMap(), 
					PeptidePsm.PSM,
					searcherCutoffValuesSearchLevel );

			updateBestAnnotationValues( 
					bestPeptideAnnotationDTOMap, 
					webReportedPeptideWrapper.getPeptideAnnotationDTOMap(),
					PeptidePsm.PEPTIDE,
					searcherCutoffValuesSearchLevel );
		}
		
		

		SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = new SearchProteinLooplinkWrapper();

		SearchProteinLooplink searchProteinLooplink = new SearchProteinLooplink();
		searchProteinLooplinkWrapper.setSearchProteinLooplink( searchProteinLooplink );

		searchProteinLooplinkWrapper.setPsmAnnotationDTOMap( bestPsmAnnotationDTOMap );
		searchProteinLooplinkWrapper.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOMap );

		
		
		searchProteinLooplink.setSearch( search );
		searchProteinLooplink.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );


		SearchProtein searchProtein = searchProtein_KeyOn_PROT_SEQ_ID_Map.get( proteinId );


		if ( searchProtein == null ) {

			searchProtein = new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( proteinId ) );

			searchProtein_KeyOn_PROT_SEQ_ID_Map.put( proteinId, searchProtein );
		}

		searchProteinLooplink.setProtein( searchProtein );


		searchProteinLooplink.setProteinPosition1( proteinPosition_1 );
		searchProteinLooplink.setProteinPosition2( proteinPosition_2 );


		searchProteinLooplink.setNumPsms( numPsms );
		searchProteinLooplink.setNumPeptides( numLinkedPeptides );
		searchProteinLooplink.setNumUniquePeptides( numUniqueLinkedPeptides );
		
		searchProteinLooplink.setAssociatedReportedPeptideIds( reportedPeptideIds );
		searchProteinLooplink.setAssociatedReportedPeptideIdsRelatedPeptidesUnique( reportedPeptideIdsRelatedPeptidesUnique );


		if ( searchProteinLooplink.getNumPsms() <= 0 ) {

			//  !!!!!!!   Number of PSMs is zero this this isn't really a Protein that meets the cutoffs

			return null;  //  EARY EXIT
		}

		
		return searchProteinLooplinkWrapper;
	}
	

	private void updateBestAnnotationValues( 
			Map<Integer, AnnotationDataBaseDTO> bestAnnotationDTOMap, 
			Map<Integer, AnnotationDataBaseDTO> entryAnnotationDTOMap, 
			PeptidePsm peptidePsm,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws ProxlWebappDataException {

		
		for ( Map.Entry<Integer, AnnotationDataBaseDTO> entryAnnotationDTOMap_Entry : entryAnnotationDTOMap.entrySet() ) {
		
			Integer annotationTypeId = entryAnnotationDTOMap_Entry.getKey();
			AnnotationDataBaseDTO entryAnnotationDTO = entryAnnotationDTOMap_Entry.getValue();
			
			
			//  Reformat value string to look like what went into best fields in DB
			
			entryAnnotationDTO.setValueString( Double.toString( entryAnnotationDTO.getValueDouble() ) );
			
			
			AnnotationDataBaseDTO bestAnnotationDTO = bestAnnotationDTOMap.get( annotationTypeId );
			
			if ( bestAnnotationDTO == null ) {
				
				bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
				
			} else {
			
				SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel = null;
				
				if ( peptidePsm == PeptidePsm.PEPTIDE ) {
					searcherCutoffValuesAnnotationLevel = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffs( annotationTypeId );
				} else {
					searcherCutoffValuesAnnotationLevel = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffs( annotationTypeId );
				}

				if ( searcherCutoffValuesAnnotationLevel == null ) {
					
					String msg = "searcherCutoffValuesAnnotationLevel == null for annotationTypeId: " + annotationTypeId
							+ ", peptidePsm: " + peptidePsm;
					log.error(msg);
					throw new ProxlWebappDataException(msg);
				}
				
				AnnotationTypeDTO annotationTypeDTO = searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO();
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();

				if ( annotationTypeFilterableDTO == null ) {
					
					String msg = "annotationTypeFilterableDTO == null for annotationTypeId: " + annotationTypeId;
					log.error(msg);
					throw new ProxlWebappDataException(msg);
				}
				
				FilterDirectionType filterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();

				if ( filterDirectionType == FilterDirectionType.ABOVE ) {
					
					if ( entryAnnotationDTO.getValueDouble() > bestAnnotationDTO.getValueDouble() ) {
						
						//  entry has a better value than best so replace best with entry

						bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
					}
				} else {

					if ( entryAnnotationDTO.getValueDouble() < bestAnnotationDTO.getValueDouble() ) {
						
						//  entry has a better value than best so replace best with entry

						bestAnnotationDTOMap.put( annotationTypeId, entryAnnotationDTO );
					}
				}
			}
		}
					
	}
	
}