package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForChargeValue;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForSearchId;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher.PSM_DistinctChargeStatesResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ChargeStateCounts_Merged {

	private static final Logger log = Logger.getLogger(ChargeStateCounts_Merged.class);

	/**
	 * private constructor
	 */
	private ChargeStateCounts_Merged(){}
	public static ChargeStateCounts_Merged getInstance( ) throws Exception {
		ChargeStateCounts_Merged instance = new ChargeStateCounts_Merged();
		return instance;
	}
		
	/**
	 * @param filterCriteriaJSON
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 */
	public ChargeStateCounts_Merged_Results getChargeStateCounts_Merged( 			
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		try {
			mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( filterCriteriaJSON, MergedPeptideQueryJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonParseException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonMappingException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', IOException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		}
		

		List<String> linkTypesList = new ArrayList<String>( mergedPeptideQueryJSONRoot.getLinkTypes().length );

		for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
			String linkType = null;
			if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.CROSS_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.LOOP_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;
			} else {
				String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
				log.error( msg );
				throw new Exception( msg );
			}
			linkTypesList.add( linkType );
		}

		//  Get Maps of Charge values mapped to count, mapped by search id then link type
		//  Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
		Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType(
						searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel);

		ChargeStateCounts_Merged_Results results =
				getPerChartData_KeyedOnLinkType( 
						allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType, 
						linkTypesList, 
						searchIdsListDeduppedSorted );
		
		return results;
	}

	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searchIdsListDeduppedSorted
	 * @return
	 */
	private ChargeStateCounts_Merged_Results getPerChartData_KeyedOnLinkType( 
			// Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
			Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType,
			List<String> linkTypesList,
			List<Integer> searchIdsListDeduppedSorted ) {
		
		List<ChargeStateCountsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );
		boolean foundData = false;

		for ( String linkType : linkTypesList ) {
			Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId =
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId == null ) {
				ChargeStateCountsResultsForLinkType resultForLinkType =  new ChargeStateCountsResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				ChargeStateCountsResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId, searchIdsListDeduppedSorted );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
				foundData = true;
			}
		}
		
		ChargeStateCounts_Merged_Results results = new ChargeStateCounts_Merged_Results();
		results.setResultsPerLinkTypeList( dataForChartPerLinkTypeList );
		results.setSearchIds( searchIdsListDeduppedSorted );
		results.setFoundData( foundData );
		
		return results;
	}
	
	/**
	 * @param allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId
	 * @param searchIdsListDeduppedSorted
	 * @return
	 */
	private ChargeStateCountsResultsForLinkType getSingleChartData_ForLinkType( 
			// Map<[search id],Map<[charge value],[count of charge value]>>
			Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId,
			List<Integer> searchIdsListDeduppedSorted ) {
		
		//  First, reprocess maps into  Map<[charge value],Map<[search id],[count of charge value]>>
		Map<Integer,Map<Integer,Long>> allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue = new HashMap<>();
		
		for ( Map.Entry<Integer,Map<Integer,Long>> entryKeySearchId : allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId.entrySet() ) {
			Integer searchId = entryKeySearchId.getKey();
			Map<Integer,Long> allSearchesCombinedChargeValueCountMap = entryKeySearchId.getValue();
			for ( Map.Entry<Integer,Long> entryKeyChargeValue : allSearchesCombinedChargeValueCountMap.entrySet() ) {
				Integer chargeValue = entryKeyChargeValue.getKey();
				Long count = entryKeyChargeValue.getValue();
				
				Map<Integer,Long> allSearchesCombinedSearchIdCountMap = allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.get( chargeValue );
				if ( allSearchesCombinedSearchIdCountMap == null ) {
					allSearchesCombinedSearchIdCountMap = new HashMap<>();
					allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.put( chargeValue, allSearchesCombinedSearchIdCountMap );
				}
				allSearchesCombinedSearchIdCountMap.put( searchId, count );
			}
		}
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		List<ChargeStateCountsResultsForChargeValue> dataForChartPerChargeValueList = new ArrayList<>( allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.size() );
		
		for ( Map.Entry<Integer,Map<Integer,Long>> allSearchesCombinedSearchIdCountMapEntry : allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.entrySet() ) {
			
			ChargeStateCountsResultsForChargeValue chargeStateCountsResultsForChargeValue = new ChargeStateCountsResultsForChargeValue();
			dataForChartPerChargeValueList.add( chargeStateCountsResultsForChargeValue );
			chargeStateCountsResultsForChargeValue.setCharge( allSearchesCombinedSearchIdCountMapEntry.getKey() );
			
			List<ChargeStateCountsResultsForSearchId> countPerSearchIdList = new ArrayList<>( searchIdsListDeduppedSorted.size() );
			chargeStateCountsResultsForChargeValue.setCountPerSearchIdList( countPerSearchIdList );
			
			Map<Integer,Long> allSearchesCombinedSearchIdCount = allSearchesCombinedSearchIdCountMapEntry.getValue();
			for ( Integer searchId : searchIdsListDeduppedSorted ) {
				Long chargeCount = allSearchesCombinedSearchIdCount.get( searchId );
				ChargeStateCountsResultsForSearchId resultForSearchId = new ChargeStateCountsResultsForSearchId();
				resultForSearchId.setSearchId( searchId );
				if ( chargeCount == null ) {
					resultForSearchId.setCount( 0 );
				} else {
					resultForSearchId.setCount( chargeCount );
					dataFound = true;
				}
				countPerSearchIdList.add( resultForSearchId );
			}
		};
		
		// Sort in charge value order
		Collections.sort( dataForChartPerChargeValueList, new Comparator<ChargeStateCountsResultsForChargeValue>() {
			@Override
			public int compare(ChargeStateCountsResultsForChargeValue o1, ChargeStateCountsResultsForChargeValue o2) {
				return o1.getCharge() - o2.getCharge();
			}
		});
		
		ChargeStateCountsResultsForLinkType result = new ChargeStateCountsResultsForLinkType();
		result.setDataForChartPerChargeValueList( dataForChartPerChargeValueList );
		result.setDataFound( dataFound );
		
		return result;
	}
	
	
	/////////////////////////////////////
	
	
	/**
	 * Return Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,Map<Integer,Long>>> getAllSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType (
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) throws ProxlWebappDataException, Exception {
	
		//  Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
		Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			
			//  Get cutoffs for this project search id
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			PSM_DistinctChargeStatesResult psm_DistinctChargeStatesResult = 
					PSM_DistinctChargeStatesSearcher.getInstance()
					.getPSM_DistinctChargeStates( searchId, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery );
			
			/**
			 * Map <{Link Type},Map<{Charge Value},{count}>>
			 */
			Map<String,Map<Integer,Long>> chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue =
					psm_DistinctChargeStatesResult.getResultsChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue();
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerCountsIntoUnlinkedCounts( chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
			
			//  Copy into overall map
			
			for ( Map.Entry<String,Map<Integer,Long>> entry : chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.entrySet() ) {
				Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId = 
						allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.get( entry.getKey() );
				if ( allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId == null ) {
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.put( entry.getKey(), allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId );
				}
				allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId.put( searchId, entry.getValue() );
			}
			
		}
		
		return allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType;
	}

	
	/**
	 * Combine Dimer Counts Into Unlinked Counts
	 * @param chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineDimerCountsIntoUnlinkedCounts( Map<String,Map<Integer,Long>> chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue ) {
		
		Map<Integer,Long> dimerValuesMap = chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerValuesMap == null ) {
			//  No Dimer values so skip
			return;  //  EARLY EXIT
		}
		
		Map<Integer,Long> unlinkedValuesMap = chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( XLinkUtils.UNLINKED_TYPE_STRING );
		if ( unlinkedValuesMap == null ) {
			//  No Unlinked values so simply copy dimer to unlinked and remove dimer
			chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerValuesMap );
			chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.remove( XLinkUtils.DIMER_TYPE_STRING );
			return;  //  EARLY EXIT
		}
		
		Map<Integer,Long> unlinkedDimerCombinedValuesMap = 
				combineChargeCountsIntoUnlinkedCounts( unlinkedValuesMap, dimerValuesMap );
		chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( XLinkUtils.UNLINKED_TYPE_STRING, unlinkedDimerCombinedValuesMap );
		chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.remove( XLinkUtils.DIMER_TYPE_STRING );
		
	}
	

	/**
	 * Returns combined counts.
	 * @param chargeCountMap_KeyedOnChargeValue_1
	 * @param chargeCountMap_KeyedOnChargeValue_2
	 * @return combined counts
	 */
	private Map<Integer,Long> combineChargeCountsIntoUnlinkedCounts( 
			Map<Integer,Long> chargeCountMap_KeyedOnChargeValue_1,
			Map<Integer,Long> chargeCountMap_KeyedOnChargeValue_2 ) {
		
		Map<Integer,Long> resultCountMap_KeyedOnChargeValue = new HashMap<>();
		
		Set<Integer> chargeCountKeysCopy_2 = new HashSet<>( chargeCountMap_KeyedOnChargeValue_2.keySet() );
		
		//  First process unlinkedValuesMap
		for ( Map.Entry<Integer,Long> chargeValueEntry_1 : chargeCountMap_KeyedOnChargeValue_1.entrySet() ) {
			Long newChargeCount = chargeValueEntry_1.getValue();
			Long chargeCountEntry_2 = chargeCountMap_KeyedOnChargeValue_2.get( chargeValueEntry_1.getKey() );
			if ( chargeCountEntry_2 != null ) {
				//  Add chargeCountEntry_2 count to chargeValueEntry_1 count
				newChargeCount = chargeValueEntry_1.getValue().longValue() + chargeCountEntry_2.longValue();
				//  Remove entry from chargeCountKeys_2 copy of keyset
				chargeCountKeysCopy_2.remove( chargeValueEntry_1.getKey() );
			}
			resultCountMap_KeyedOnChargeValue.put( chargeValueEntry_1.getKey(), newChargeCount );
		}
		//  Next add any entries in chargeCountMap_KeyedOnChargeValue_2 not in chargeCountMap_KeyedOnChargeValue_1
		// (removed from chargeCountKeysCopy_2 the keys that are in both maps in previous loop)
		if ( ! chargeCountKeysCopy_2.isEmpty() ) {
			for ( Integer chargeCountKeysCopy_2_Entry : chargeCountKeysCopy_2 ) {
				Long chargeCount_2 = chargeCountMap_KeyedOnChargeValue_2.get( chargeCountKeysCopy_2_Entry );
				resultCountMap_KeyedOnChargeValue.put( chargeCountKeysCopy_2_Entry, chargeCount_2 );
			}
		}
		
		return resultCountMap_KeyedOnChargeValue;
	}


}
