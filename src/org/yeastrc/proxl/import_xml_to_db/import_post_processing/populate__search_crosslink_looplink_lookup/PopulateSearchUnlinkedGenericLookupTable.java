package org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchUnlinkedBestPSMValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchUnlinkedGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedBestPSMValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedBestPeptideValueGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchUnlinkedGenericLookupDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteria;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_utils.CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords;
import org.yeastrc.xlink.searchers.AnnotationTypesForSearchIdPSMPeptideTypeSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

/**
 * 
 *
 */
public class PopulateSearchUnlinkedGenericLookupTable {

	private static final Logger log = Logger.getLogger( PopulateSearchUnlinkedGenericLookupTable.class );
	
	/**
	 * private constructor
	 */
	private PopulateSearchUnlinkedGenericLookupTable() {  }
	
	public static PopulateSearchUnlinkedGenericLookupTable getInstance() { 
		return new PopulateSearchUnlinkedGenericLookupTable(); 
	}


	private String PRIMARY_SELECT_SQL = 
			
			"SELECT unlinked.nrseq_id "
			
			+ " FROM unlinked "
			+ " INNER JOIN psm ON unlinked.psm_id = psm.id "
			
			+ " WHERE psm.search_id = ? "
			+ " GROUP BY unlinked.nrseq_id ";



	/**
	 * For the given search id, will populate the search_unlinked_lookup table
	 * 
	 * @param searchId
	 * @throws Exception 
	 */
	public void populateSearchUnlinkedGenericLookupTable( int searchId ) throws Exception {
		
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
	    
	    
	    
		SearchDTO searchDTO = SearchDAO.getInstance().getSearch( searchId );


		//  Get Annotation Type records for PSM and Peptide
		
		
		//  Get  Annotation Type records for PSM
		
		List<AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOList =
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_PSM_Filterable_ForSearchId( searchId );
		

		

		//  Get  Annotation Type records for Reported Peptides
		
		List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList =
				AnnotationTypesForSearchIdPSMPeptideTypeSearcher.getInstance().get_Peptide_Filterable_ForSearchId( searchId );
		
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		ResultSet rs = null;
		
		String sql = PRIMARY_SELECT_SQL;

		int processedRecordCount = 0;
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

//			st = conn.createStatement();
//			st.execute( disableKeysSQL );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );


			rs = pstmt.executeQuery();

			
			DB_Insert_SearchUnlinkedGenericLookupDAO db_Insert_SearchUnlinkedGenericLookupDAO = DB_Insert_SearchUnlinkedGenericLookupDAO.getInstance();

			while( rs.next() ) {

				processedRecordCount++;
				
				SearchUnlinkedGenericLookupDTO item = new SearchUnlinkedGenericLookupDTO();

				item.setSearchId( searchId );
				item.setNrseqId( rs.getInt( "nrseq_id" ) );

				
				//     Get PSM and Peptide counts at default cutoff values
				
				
				//  Get a searcherCutoffValuesSearchLevel object for the default cutoff values 
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
						CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords.getInstance()
						.createSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords( 
								searchId, 
								srchPgm_Filterable_Psm_AnnotationType_DTOList, 
								srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );



				NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
						NumPeptidesPSMsForProteinCriteria.getInstance()
						.getNumPeptidesPSMsForUnlinked(
								item.getSearchId(),
								searcherCutoffValuesSearchLevel,
								item.getNrseqId(),
								YRC_NRSEQUtils.getDatabaseIdFromName( searchDTO.getFastaFilename() ) );
				
				
				int numPsmAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
				int numLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();

				int numUniqueLinkedPeptidesAtDefaultCutoff = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();

				item.setNumPsmAtDefaultCutoff( numPsmAtDefaultCutoff );
				item.setNumLinkedPeptidesAtDefaultCutoff( numLinkedPeptidesAtDefaultCutoff );
				item.setNumUniqueLinkedPeptidesAtDefaultCutoff( numUniqueLinkedPeptidesAtDefaultCutoff );
				
				db_Insert_SearchUnlinkedGenericLookupDAO.save( item );

//				List<SearchUnlinkedBestPSMValueGenericLookupDTO> insertedBestPSMValueRecords =
				populateUnlinkedBestPSMValue( item, srchPgm_Filterable_Psm_AnnotationType_DTOList );
				
//				List<SearchUnlinkedBestPeptideValueGenericLookupDTO> insertedBestPeptideValueRecords = 
				populateUnlinkedBestPeptideValue( item, srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList );

				if ( log.isInfoEnabled() ) {

					if ( ( processedRecordCount % 100000 ) == 0 ) {

						log.info( "populateSearchUnlinkedGenericLookupTable: processed " + processedRecordCount + " records." );
					}
				}

			}

//			st.execute( enableKeysSQL );


		} catch ( Exception e ) {

			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' \n sql: " + sql
//					+ "\n disableKeysSQL: " + disableKeysSQL
//					+ "\n enableKeysSQL: " + enableKeysSQL
					, e );

			throw e;

		} finally {

			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}

			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}

			if( st != null ) {
				try { st.close(); } catch( Throwable t ) { ; }
				st = null;
			}

			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}

		}
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

		if ( log.isInfoEnabled() ) {

			log.info( "populateSearchUnlinkedGenericLookupTable: Record Count Total: " + processedRecordCount );
		}

	}
	
	
	/**
	 * @param searchUnlinkedGenericLookupDTO
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @throws Exception
	 */
	private List<SearchUnlinkedBestPSMValueGenericLookupDTO> populateUnlinkedBestPSMValue( 
			
			SearchUnlinkedGenericLookupDTO searchUnlinkedGenericLookupDTO,
			
			List<AnnotationTypeDTO>	srchPgm_Filterable_Psm_AnnotationType_DTOList
			
			) throws Exception {
		
		
		List<SearchUnlinkedBestPSMValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOList.size() );
		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
			GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher.getInstance()
			.getBestAnnotationValue( 
					srchPgmFilterablePsmAnnotationTypeDTO.getId(), 
					searchUnlinkedGenericLookupDTO, 
					srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );
			
			if ( bestFilterableAnnotationValue != null ) {

				SearchUnlinkedBestPSMValueGenericLookupDTO item = new SearchUnlinkedBestPSMValueGenericLookupDTO();

				item.setSearchUnlinkedGenericLookup( searchUnlinkedGenericLookupDTO.getId() );
				
				item.setSearchId( searchUnlinkedGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchUnlinkedGenericLookupDTO.getNrseqId() );

				item.setPsmFilterableAnnotationTypeId( srchPgmFilterablePsmAnnotationTypeDTO.getId() );

				double bestPsmValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPsmValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();

				item.setBestPsmValueForAnnTypeId( bestPsmValueForAnnTypeId );
				item.setBestPsmValueStringForAnnTypeId( bestPsmValueStringForAnnTypeId );

				DB_Insert_SearchUnlinkedBestPSMValueGenericLookupDAO.getInstance().save( item );
				
				results.add( item );
			}
		}
		
		return results;
	}
	

	
	/**
	 * @param searchUnlinkedGenericLookupDTO
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @throws Exception 
	 */
	private List<SearchUnlinkedBestPeptideValueGenericLookupDTO> populateUnlinkedBestPeptideValue( 
			
			SearchUnlinkedGenericLookupDTO searchUnlinkedGenericLookupDTO,
			

			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
			
			) throws Exception {
		
		

		List<SearchUnlinkedBestPeptideValueGenericLookupDTO> results = new ArrayList<>( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList.size() );
		
		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchUnlinkedProteinSearcher.getInstance()
					.getBestAnnotationValue( 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId(), 
							searchUnlinkedGenericLookupDTO, 
							srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() );


			if ( bestFilterableAnnotationValue != null ) {

				SearchUnlinkedBestPeptideValueGenericLookupDTO item = new SearchUnlinkedBestPeptideValueGenericLookupDTO();

				item.setSearchUnlinkedGenericLookup( searchUnlinkedGenericLookupDTO.getId() );

				item.setSearchId( searchUnlinkedGenericLookupDTO.getSearchId() );

				item.setNrseqId( searchUnlinkedGenericLookupDTO.getNrseqId() );

				item.setPeptideFilterableAnnotationTypeId( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );


				double bestPeptideValueForAnnTypeId = bestFilterableAnnotationValue.getBestValue();
				String bestPeptideValueStringForAnnTypeId = bestFilterableAnnotationValue.getBestValueString();



				item.setBestPeptideValueForAnnTypeId( bestPeptideValueForAnnTypeId );
				item.setBestPeptideValueStringForAnnTypeId( bestPeptideValueStringForAnnTypeId );



				DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO.getInstance().save( item );

				results.add( item );
			}
		}
		
		return results;
	}
	

}
