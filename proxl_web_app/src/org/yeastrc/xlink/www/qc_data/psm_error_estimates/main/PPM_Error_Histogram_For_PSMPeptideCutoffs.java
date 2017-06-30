package org.yeastrc.xlink.www.qc_data.psm_error_estimates.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.PsmDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.PSMMassCalculator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Compute PPM Error Histogram
 */
public class PPM_Error_Histogram_For_PSMPeptideCutoffs {

	private static final Logger log = Logger.getLogger(PPM_Error_Histogram_For_PSMPeptideCutoffs.class);
	
	
	private static final int REMOVE_OUTLIERS_FIRST_QUARTER_PERCENTILE = 25;
	private static final int REMOVE_OUTLIERS_THIRD_QUARTER_PERCENTILE = 75;
	
	//  number of IQRs to add
	private static final double OUTLIER_FACTOR = 1.5;
	
	/**
	 * private constructor
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs(){}
	public static PPM_Error_Histogram_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PPM_Error_Histogram_For_PSMPeptideCutoffs instance = new PPM_Error_Histogram_For_PSMPeptideCutoffs();
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
	public PPM_Error_Histogram_For_PSMPeptideCutoffs_Result getPPM_Error_Histogram_For_PSMPeptideCutoffs( 
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
//		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
//			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType = 
				createppmErrorListForLinkType_ByLinkTypeMap( filterCriteriaJSON, searches, searchIds );
		
		//  Combine the Dimer into the Unlinked
		
		List<Double> ppmErrorListForDimer = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( ppmErrorListForDimer != null ) {
			if ( ! ppmErrorListForDimer.isEmpty() ) {
				List<Double> ppmErrorListForUnlinked = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
				ppmErrorListForUnlinked.addAll( ppmErrorListForDimer );
			}
			ppmErrorListForLinkType_ByLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
		}
		
		
		//  Build a new map, removing outliers from each list
		ppmErrorListForLinkType_ByLinkType = removeOutliers( ppmErrorListForLinkType_ByLinkType );
		
		
		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result result = 
				getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result( ppmErrorListForLinkType_ByLinkType );
		
		return result;
	}
	
	/**
	 * @param ppmErrorListForLinkType_ByLinkType
	 * @return
	 */
	private Map<String, List<Double>> removeOutliers( Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType ) {
		
		//  output map
		Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType_Result = new HashMap<>();
		
		// Process for each link type
		
		for ( Map.Entry<String, List<Double>> entry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
			String linkType = entry.getKey();
			List<Double> ppmErrorListBeforeRemoveOutliers = entry.getValue();

			// Get a DescriptiveStatistics instance - Apache Commons
			DescriptiveStatistics stats = new DescriptiveStatistics();
			
			// Add the PPM Error data
			for( Double ppmError : ppmErrorListBeforeRemoveOutliers ) {
				stats.addValue( ppmError );
			}

			// Compute some statistics
			double firstquarter = stats.getPercentile( REMOVE_OUTLIERS_FIRST_QUARTER_PERCENTILE );
			double thirdquarter = stats.getPercentile( REMOVE_OUTLIERS_THIRD_QUARTER_PERCENTILE );

			double interQuartileRegion = thirdquarter - firstquarter;
			double lowcutoff = firstquarter - ( OUTLIER_FACTOR * interQuartileRegion );
			double highcutoff = thirdquarter + ( OUTLIER_FACTOR * interQuartileRegion );
			
			//  Build a new list removing values < lowcutoff and > highcutoff 
			List<Double> ppmErrorList_After_RemoveOutliers = new ArrayList<>( ppmErrorListBeforeRemoveOutliers.size() );
			for( Double ppmError : ppmErrorListBeforeRemoveOutliers ) {
				if ( ppmError >= lowcutoff && ppmError <= highcutoff ) {
					ppmErrorList_After_RemoveOutliers.add( ppmError );
				}
			}
			//  Insert new list into new hash
			ppmErrorListForLinkType_ByLinkType_Result.put( linkType, ppmErrorList_After_RemoveOutliers );
		}
		
		return ppmErrorListForLinkType_ByLinkType_Result;
	}
	
	
	/**
	 * @param ppmErrorListForLinkType_ByLinkType
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs_Result getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result(
			Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType ) {
		
		Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap = new HashMap<>();

		for ( Map.Entry<String,List<Double>> ppmErrorListForLinkTypeEntry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
			String linkType = ppmErrorListForLinkTypeEntry.getKey();
			List<Double> ppmErrorList = ppmErrorListForLinkTypeEntry.getValue();
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType resultsForLinkType =
					getPPM_Error_HistogramData_ForLinkType( ppmErrorList );
			resultsForLinkType.setLinkType( linkType );
			resultsByLinkTypeMap.put( linkType, resultsForLinkType );
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( 5 );
		
		//  copy map to array for output, in a specific order
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		

		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result result = new PPM_Error_Histogram_For_PSMPeptideCutoffs_Result();
		result.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return result;
	}
	
	/**
	 * @param linkType
	 * @param dataForChartPerLinkTypeList
	 * @param resultsByLinkTypeMap
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList, 
			Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap ) {
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType item = resultsByLinkTypeMap.get( linkType );
		if ( item != null ) {
			dataForChartPerLinkTypeList.add( item );
		}
	}
	

	/**
	 * @param ppmErrorList
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType getPPM_Error_HistogramData_ForLinkType( 
			List<Double> ppmErrorList ) {
		
		{
			if ( ppmErrorList == null || ppmErrorList.isEmpty() ) {
				PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
				return result;
			}
		}
		
		int numScans = ppmErrorList.size();
		boolean firstOverallpreMZEntry = true;
		//  Find max and min values
		double ppmErrorMin = Double.MAX_VALUE;
		double ppmErrorMax =  Double.MIN_VALUE;
		for ( double ppmErrorEntry : ppmErrorList ) {
			if ( firstOverallpreMZEntry  ) {
				firstOverallpreMZEntry = false;
				ppmErrorMin = ppmErrorEntry;
				ppmErrorMax = ppmErrorEntry;
			} else {
				if ( ppmErrorEntry < ppmErrorMin ) {
					ppmErrorMin = ppmErrorEntry;
				}
				if ( ppmErrorEntry > ppmErrorMax ) {
					ppmErrorMax = ppmErrorEntry;
				}
			}
		}

		//  Process data into bins
		int binCount = (int) ( Math.sqrt( ppmErrorList.size() ) );
		
		if ( ppmErrorMax > 0 && ppmErrorMin < 0 ) {
			//  Change Max and Min so the center of a bin is at zero
			{
				//  Initial 'extend' Min and Max by a bin  
				double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
				double binSize = ( ppmErrorMaxMinusMin ) / binCount;
				ppmErrorMax += binSize;
				ppmErrorMin -= binSize;
				
				//  Since the new bin size will be larger than the old bin size, 
				//  adding half a bin is possibly not enough to keep from  
				//  cutting into the existing data points when shifting the bins to center a bin on zero.
				
				//  WAS:
				//  Initial 'extend' Min and Max by half a bin  
//				double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
//				double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//				double halfBinSize = binSize * 0.5;
//				ppmErrorMax += halfBinSize;
//				ppmErrorMin -= halfBinSize;
			}
//			
			
			
			double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
			double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//			double halfBinSize = binSize * 0.5;
			// The bin that contains position zero
			int binIndexContainZero = (int) ( - ( ppmErrorMin / binSize ) );
			// The start of the bin that contains position zero			
			double binStartContainZero =  ppmErrorMin + ( binIndexContainZero * binSize );
			// Center of bin that contains zero
			double binStartContainZeroPlusHalfBin = binStartContainZero + ( binSize * 0.5 );
			//  
			double shift = binStartContainZeroPlusHalfBin;
			
			if ( binStartContainZeroPlusHalfBin > 0 ) {
				//  Center of bin is 'right' of zero, so shift left
				ppmErrorMin -= ( shift ); //  binStartContainZeroPlusHalfBin is positive here
				ppmErrorMax -= ( shift ); //  binStartContainZeroPlusHalfBin is positive here
			} else {
				//  Center of bin is 'left' of zero, so shift right
				ppmErrorMin += ( - shift ); //  binStartContainZeroPlusHalfBin is negative here
				ppmErrorMax += ( - shift ); //  binStartContainZeroPlusHalfBin is negative here
			}
		}
		
		//  Debugging code
//		{
//			//  Get center of bin that contains zero
//			double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
//			double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//			// The bin that contains position zero
//			int binIndexContainZero = (int) ( - ( ppmErrorMin / binSize ) );
//			// The start of the bin that contains position zero			
//			double binStartContainZero =  ppmErrorMin + ( binIndexContainZero * binSize );
//			// Center of bin that contains zero
//			double binStartContainZeroPlusHalfBin = binStartContainZero + ( binSize * 0.5 );
//
//			int z = 0;
//		}
		
		double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
		
		
		//  Allocate bins
		int[] ppmErrorCounts = new int[ binCount ];
		//  Bin Size
		double binSize = ( ppmErrorMaxMinusMin ) / binCount;
		
		for ( double ppmErrorEntry : ppmErrorList ) {
			double preMZFraction = ( ppmErrorEntry - ppmErrorMin ) / ppmErrorMaxMinusMin;
			int bin = (int) ( (  preMZFraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			ppmErrorCounts[ bin ]++;
		}
		
		//    Exclude first contiguous and last contiguous bins where count is zero
		//  Find first and last bins where count != 0
		int firstBinIndexCountNotZero = 0;
		int lastBinIndexCountNotZero = ppmErrorCounts.length - 1;
		while ( ppmErrorCounts[ firstBinIndexCountNotZero ] == 0 && firstBinIndexCountNotZero < lastBinIndexCountNotZero ) {
			firstBinIndexCountNotZero++;
		}
		while ( ppmErrorCounts[ lastBinIndexCountNotZero ] == 0 && firstBinIndexCountNotZero < lastBinIndexCountNotZero ) {
			lastBinIndexCountNotZero--;
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSize / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = firstBinIndexCountNotZero; binIndex <= lastBinIndexCountNotZero; binIndex++ ) {
			int preMZCount = ppmErrorCounts[ binIndex ];
			double binStart = ( ( binIndex * binSize ) ) + ppmErrorMin;
			double binEnd = ( ( binIndex + 1 ) * binSize ) + ppmErrorMin;
			double binMiddleDouble = binStart + binHalf;
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket();
			chartBucket.setBinEnd( binEnd );
			chartBucket.setBinStart( binStart );
			chartBuckets.add( chartBucket );
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setCount( preMZCount );
		}
		
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setNumScans( numScans );
		result.setPpmErrorMax( ppmErrorMax );
		result.setPpmErrorMin( ppmErrorMin );
		
		return result;
	}
	
	
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @param searchIds
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	private Map<String, List<Double>> createppmErrorListForLinkType_ByLinkTypeMap(
			String filterCriteriaJSON, 
			List<SearchDTO> searches,
			Collection<Integer> searchIds)
			throws JsonParseException, JsonMappingException, IOException, Exception, ProxlWebappDataException {
		
		//  Reported Peptide Ids Skipped For Error Calculating MZ
		List<Integer> reportedPeptideIdsSkippedForErrorCalculatingMZ = new ArrayList<>( 100 );
		
		//  Internal use for tracking data used to compute PPM Error for entries with highest PPM Error
//		List<PPM_Error_ComputeEntry> ppm_Error_ComputeEntryList = new ArrayList<>( 10 );
		
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
		
		
		//   Map of List of PPM Error by Link Type
		
		Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType = new HashMap<>();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.CROSS_TYPE_STRING, new ArrayList<>() );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.LOOP_TYPE_STRING, new ArrayList<>() );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
					//  Add lists for Unlinked and Dimer
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, new ArrayList<>() );
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.DIMER_TYPE_STRING, new ArrayList<>() );
				} else {
					String msg = "linkType is invalid, linkType: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
			}
		}
		
		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();
		
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
			
			//  Get static mods for search id
			List<StaticModDTO> staticModDTOList = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );
			
			///////////////////////////////////////////////
			//  Get peptides for this search from the DATABASE
			List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
					PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
							searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
				
				String linkType = null;
				
				//  srchRepPeptPeptideDTOList: associated SrchRepPeptPeptideDTO for the link, one per associated peptide, populated per link type
				
				//  copied from SearchPeptideCrosslink, this way not load PeptideDTO in SearchPeptideCrosslink
				List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList =
						SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.getInstance()
						.getForSearchIdReportedPeptideId( searchId, reportedPeptideId );
				
				
				
				if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
					//  Process a crosslink
					linkType = XLinkUtils.CROSS_TYPE_STRING;
					
					//  validation for crosslink
					if ( srchRepPeptPeptideDTOList.size() != 2 ) {
						String msg = "For Crosslink: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
								|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}

				} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
					//  Process a looplink
					linkType = XLinkUtils.LOOP_TYPE_STRING;
					
					//  validation for looplink
					if ( srchRepPeptPeptideDTOList.size() != 1 ) {
						String msg = "For Looplink: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
								|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() == null
								|| srchRepPeptPeptideDTO.getPeptidePosition_2() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_2() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}

				} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
					//  Process a unlinked
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;
					
					//  validation for unlinked
					if ( srchRepPeptPeptideDTOList.size() != 1 ) {
						String msg = "For Unlinked: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
								&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}
				} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
					//  Process a dimer
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides

					//  validation for dimer
					if ( srchRepPeptPeptideDTOList.size() != 2 ) {
						String msg = "For Dimer: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
								&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}
				} else {
					String msg = 
							"Link type unkown"
							+ " for reportedPeptideId: " + reportedPeptideId
							+ ", searchId: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				
				// get from map for link type
				List<Double> ppmErrorListForLinkType = ppmErrorListForLinkType_ByLinkType.get( linkType );
				
				if ( ppmErrorListForLinkType == null ) {
					String msg = "In processing Reported Peptides, link type not found: " + linkType;
					log.error( msg );
					throw new Exception(msg);
				}

				//  Collect the peptides and dynamic mods
				
				PeptideDTO peptide_1 =  null;
				PeptideDTO peptide_2 =  null;
				
				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_1 = null;
				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_2 = null;
				
				//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					// get PeptideDTO, caching locally in peptideDTO_MappedById
					PeptideDTO peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
					if ( peptide == null ) {
						peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
						//  To directly retrieve from DB:  PeptideDAO.getInstance().getPeptideDTOFromDatabaseActual( id )
						if ( peptide == null ) {
							String msg = 
									"PeptideDTO not found in DB for id: " + srchRepPeptPeptideDTO.getPeptideId()
									+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", for reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
					}
					
//					staticModDTOList
					
					List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Original = 
							SrchRepPeptPeptDynamicModSearcher.getInstance()
							.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
					
					//  Remove duplicate dynamic mods for same position and both compared are monolink flag true
					//     logging error if mass is different
					
					List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = new ArrayList<>( srchRepPeptPeptDynamicModDTOList_Original.size() );

					for ( SrchRepPeptPeptDynamicModDTO item_OriginalList : srchRepPeptPeptDynamicModDTOList_Original ) {
						
						//  Check if already in list 
						boolean alreadyInList = false;
						for ( SrchRepPeptPeptDynamicModDTO item_OutputList : srchRepPeptPeptDynamicModDTOList ) {
							
							if ( item_OriginalList.getPosition() == item_OutputList.getPosition()
									&& item_OriginalList.isMonolink() 
									&& item_OutputList.isMonolink() ) {
								
								alreadyInList = true;
								
								if ( item_OriginalList.getMass() != item_OutputList.getMass() ) {
									log.error( "Two SrchRepPeptPeptDynamicModDTO for same searchReportedPeptidepeptideId"
											+ " found with same position and both are monolink and have different massses."
											+ "  Entry 1: " + item_OriginalList
											+ ", Entry 2: " + item_OutputList
											+ ". searchId: " + searchId + ", reportedPeptideId: " + reportedPeptideId
											);
								}
								break;
							}
						}
						if ( ! alreadyInList ) {
							srchRepPeptPeptDynamicModDTOList.add( item_OriginalList );
						}
					}
						
					
					//  Specific debugging
					
//					if ( searchId == 188 && reportedPeptideId == 1408748 ) {
//						log.warn( "searchId == 188 && reportedPeptideId == 1408748:  srchRepPeptPeptDynamicModDTOList: " 
//								+ srchRepPeptPeptDynamicModDTOList );
//					}
						
						

					if ( peptide_1 == null ) {
						peptide_1 = peptide;
					} else if ( peptide_2 == null ) {
						peptide_2 = peptide;
					} else {
						String msg = 
								"peptide_1 and peptide_2 already have values"
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}


					if ( srchRepPeptPeptDynamicModDTOList_1 == null ) {
						srchRepPeptPeptDynamicModDTOList_1 = srchRepPeptPeptDynamicModDTOList;
					} else if ( srchRepPeptPeptDynamicModDTOList_2 == null ) {
						srchRepPeptPeptDynamicModDTOList_1 = srchRepPeptPeptDynamicModDTOList;
					} else {
						String msg = 
								"srchRepPeptPeptDynamicModDTOList_1 and srchRepPeptPeptDynamicModDTOList_2 already have values"
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}
				

				
				//  To confirm that peptide sequences do not contain invalid amino acid characters
				
				//  Calculate M/Z from sequence(s), ... 
				
				
				try {
//					double mzCalculated = 
							PSMMassCalculator.calculateMZForPSM( 
									peptide_1, 
									peptide_2, 
									staticModDTOList, 
									srchRepPeptPeptDynamicModDTOList_1, 
									srchRepPeptPeptDynamicModDTOList_2, 
									1, // artificial charge, 
									null  // artificial linkerMassAsDouble
									);
				} catch ( Exception e ) {
					
					reportedPeptideIdsSkippedForErrorCalculatingMZ.add( reportedPeptideId );
					
//					String msg = "'Precalc' of mass at reported peptide level failed, SKIPPING processing scans for this Reported Peptide.  "
//							+ "PSMMassCalculator.calculateMZForPSM(...) threw exception."
//							+ "\n linkType: " + linkType
//							+ "\n search id: " + searchId
//							+ "\n reported peptide id: " + reportedPeptideId
//							+ "\n reported peptide: " + webReportedPeptide.getReportedPeptide().getSequence()
//							+ "\n peptide_1: " + peptide_1 
//							+ "\n peptide_2: " + peptide_2
//							+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
//							+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
//							+ "\n charge: Fake charge of '1' passed in"
//							+ "\n linkerMassAsDouble: Fake charge of null passed in"
//							+ "\n staticModDTOList: " + staticModDTOList
//							+ "\n Exception message from PSMMassCalculator.calculateMZForPSM(...): " + e.toString()
//							+ "\n Exception class from PSMMassCalculator.calculateMZForPSM(...): " + e.getClass().getCanonicalName();
//					log.warn( msg );
					
					//  SKIP to next Reported Peptide
					
					continue;  // EARLY CONTINUE
				}

				
				

				// process PSMs for this Reported Peptide

				List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
						PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );

				for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {

//					psmWebDisplayWebServiceResult.getRetentionTime();

					BigDecimal scanPreMZ = psmWebDisplayWebServiceResult.getPreMZ(); // from scan table
					double scanPreMZasDouble = scanPreMZ.doubleValue();

					PsmDTO psmDTO = psmWebDisplayWebServiceResult.getPsmDTO();
					Integer charge = psmDTO.getCharge();
					BigDecimal linkerMass = psmDTO.getLinkerMass();
					
					Double linkerMassAsDouble = null;
					
					if ( linkerMass != null ) {
						linkerMassAsDouble = linkerMass.doubleValue();
					}
					
					if ( charge != null && scanPreMZ != null ) {

						//  Compute PPM Error

						double ppmError = 0;
						
						try {
							ppmError = 
									PSMMassCalculator.calculatePPMEstimateForPSM(
											scanPreMZasDouble, 
											peptide_1, 
											peptide_2, 
											staticModDTOList, 
											srchRepPeptPeptDynamicModDTOList_1, 
											srchRepPeptPeptDynamicModDTOList_2, 
											charge, 
											linkerMassAsDouble);

							/*
							if( Math.abs( ppmError ) > 400 ) {
								String msg = "Got ppm error over 400:"
										+ "\n linkType: " + linkType
										+ "\n scanPreMZasDouble: " + scanPreMZasDouble
										+ "\n ppmError: " + ppmError
										+ "\n search id: " + searchId
										+ "\n reported peptide id: " + reportedPeptideId
										+ "\n reported peptide: " + webReportedPeptide.getReportedPeptide().getSequence()
										+ "\n peptide_1: " + peptide_1 
										+ "\n peptide_2: " + peptide_2
										+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
										+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
										+ "\n charge: " + charge
										+ "\n linkerMassAsDouble: " + linkerMassAsDouble
										+ "\n staticModDTOList: " + staticModDTOList;
								
								System.out.println( msg );
							}
							*/
							
							ppmErrorListForLinkType.add( ppmError );
						} catch ( Exception e ) {
							String msg = "PSMMassCalculator.calculatePPMEstimateForPSM(...) threw exception:"
									+ "\n linkType: " + linkType
									+ "\n scanPreMZasDouble: " + scanPreMZasDouble
									+ "\n search id: " + searchId
									+ "\n reported peptide id: " + reportedPeptideId
									+ "\n reported peptide: " + webReportedPeptide.getReportedPeptide().getSequence()
									+ "\n peptide_1: " + peptide_1 
									+ "\n peptide_2: " + peptide_2
									+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
									+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
									+ "\n charge: " + charge
									+ "\n linkerMassAsDouble: " + linkerMassAsDouble
									+ "\n staticModDTOList: " + staticModDTOList;
							log.error( msg, e );
							throw e;
						}
						
						
//						//  Calculate M/Z from sequence(s), ...
//						
//						double mzCalculated = 0;
//						
//						try {
//							mzCalculated = 
//									PSMMassCalculator.calculateMZForPSM( 
//											peptide_1, 
//											peptide_2, 
//											staticModDTOList, 
//											srchRepPeptPeptDynamicModDTOList_1, 
//											srchRepPeptPeptDynamicModDTOList_2, 
//											charge, 
//											linkerMassAsDouble );
//						} catch ( Exception e ) {
//							String msg = "PSMMassCalculator.calculateMZForPSM(...) threw exception:"
//									+ "\n linkType: " + linkType
//									+ "\n scanPreMZasDouble: " + scanPreMZasDouble 
//									+ "\n peptide_1: " + peptide_1 
//									+ "\n peptide_2: " + peptide_2
//									+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
//									+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
//									+ "\n charge: " + charge
//									+ "\n linkerMassAsDouble: " + linkerMassAsDouble
//									+ "\n staticModDTOList: " + staticModDTOList;
//							log.error( msg, e );
//							throw e;
//						}
//
//						
//						//  Compare preMZ to computed mass, applying charge, linkerMass(if not null)
//						
//						double ppmError = ( scanPreMZasDouble - mzCalculated ) / mzCalculated * 1000000;
//						
//						ppmErrorListForLinkType.add( ppmError );
						
						
//						//  ONLY FOR DEBUGGING
//						
//						//  Tracking entries with largest PPM Error for Unlinked
//						
//						if ( linkType.equals( XLinkUtils.UNLINKED_TYPE_STRING ) ) {
////						if ( linkType.equals( XLinkUtils.CROSS_TYPE_STRING ) ) {
//
//							PPM_Error_ComputeEntry ppm_Error_ComputeEntry = new PPM_Error_ComputeEntry();
//
//							ppm_Error_ComputeEntry.ppmError = ppmError;
//							ppm_Error_ComputeEntry.linkType = linkType;
//							
//							ppm_Error_ComputeEntry.scanPreM = scanPreMZasDouble;
////							ppm_Error_ComputeEntry.computedMZ = mzCalculated;
//							
//							ppm_Error_ComputeEntry.searchId = searchId;
//							ppm_Error_ComputeEntry.reportedPeptideId = reportedPeptideId;
//							
//							ppm_Error_ComputeEntry.reportedPeptideString = webReportedPeptide.getReportedPeptide().getSequence();
//							
//							ppm_Error_ComputeEntry.peptide1 = peptide_1;
//							ppm_Error_ComputeEntry.peptide2 = peptide_2;
//							ppm_Error_ComputeEntry.staticMods = staticModDTOList;
//							ppm_Error_ComputeEntry.dynamicMods1 = srchRepPeptPeptDynamicModDTOList_1;
//							ppm_Error_ComputeEntry.dynamicMods2 = srchRepPeptPeptDynamicModDTOList_2;
//							ppm_Error_ComputeEntry.charge = charge;
//							ppm_Error_ComputeEntry.linkerMass = linkerMassAsDouble;
//
//							ppm_Error_ComputeEntryList.add( ppm_Error_ComputeEntry );
//
//							//  Sort in descending ABS( ppmError ) order
//							Collections.sort( ppm_Error_ComputeEntryList, new Comparator<PPM_Error_ComputeEntry>() {
//
//								@Override
//								public int compare(PPM_Error_ComputeEntry o1, PPM_Error_ComputeEntry o2) {
//									double o1_ppmError = o1.ppmError;
//									double o2_ppmError = o2.ppmError;
//									double difference = Math.abs( o1_ppmError ) - Math.abs( o2_ppmError );
//									//  Sort in descending ppmError order
//									if ( difference > 0 ) {
//										return -1;
//									}
//									if ( difference < 0 ) {
//										return 1;
//									}
//									return 0;
//								}
//							});
//
//							int MAX_ENTRIES = 10;
//
//							if ( ppm_Error_ComputeEntryList.size() > MAX_ENTRIES ) {
//								for ( int index = MAX_ENTRIES; index < ppm_Error_ComputeEntryList.size(); index++ ) {
//									ppm_Error_ComputeEntryList.remove( index );
//								}
//							}
//						} // END  //  Tracking entries with largest PPM Error for Unlinked
						
					}


				}
				
			}
		}
		
		
	//  ONLY FOR DEBUGGING
		
		//  Output List  ppm_Error_ComputeEntryList
		
//		System.out.println( "!!!!!!!!!!!!!!!!!!!!" );
//		System.out.println( "ppm_Error_ComputeEntryList Values:" );
//		
//		for ( PPM_Error_ComputeEntry entry : ppm_Error_ComputeEntryList ) {
//		
//			System.out.println( "!!!!!!!" );
//			System.out.println( entry.toString() );
//		}
		
		if ( ! reportedPeptideIdsSkippedForErrorCalculatingMZ.isEmpty() ) {
			
			log.warn( "Number of Reported Peptides Skipped For Error Calculating MZ: " + reportedPeptideIdsSkippedForErrorCalculatingMZ.size()
					+ ", List of Reported Peptide Ids: " + reportedPeptideIdsSkippedForErrorCalculatingMZ );
		}
		
		
		return ppmErrorListForLinkType_ByLinkType;
	}
	
//  ONLY FOR DEBUGGING
	
//	private static class PPM_Error_ComputeEntry {
//		
//		double scanPreM;
//		
////		double computedMZ;  // not set
//		
//		double ppmError;
//		
//		String linkType;
//		
//		int searchId;
//		int reportedPeptideId;
//		
//		String reportedPeptideString;
//		
//		PeptideDTO peptide1;
//		PeptideDTO peptide2;
//		List<StaticModDTO> staticMods;
//		List<SrchRepPeptPeptDynamicModDTO> dynamicMods1;
//		List<SrchRepPeptPeptDynamicModDTO> dynamicMods2;
//		Integer charge;
//		Double linkerMass;
//		
//		
//		@Override
//		public String toString() {
//			return "PPM_Error_ComputeEntry [\n scanPreM=" + scanPreM 
////					+ ", computedMZ=" + computedMZ 
//					+ ", ppmError=" + ppmError
//					+ "\n, linkType=" + linkType + ", searchId=" + searchId 
//					+ "\n, reportedPeptideId=" + reportedPeptideId
//					+ ", reportedPeptideString=" + reportedPeptideString 
//					+ "\n, peptide1=" + peptide1 
//					+ "\n, peptide2="
//					+ peptide2
//					+ "\n, staticMods=" + staticMods 
//					+ "\n, dynamicMods1=" + dynamicMods1 
//					+ "\n, dynamicMods2="
//					+ dynamicMods2 
//					+ "\n, charge=" + charge + ", linkerMass=" + linkerMass + "]";
//		}
//		
//	}
	

}