package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.searchers.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.MonolinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;



public class SearchPeptideMonolink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideMonolink.class);


	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptide().getId(): " + this.getReportedPeptide().getId() 
					+ ", this.getReportedPeptide().getSequence(): " + this.getReportedPeptide().getSequence() );
			
			return;
		}
		
		try {

			List<MonolinkDTO> monolinkList = 
					MonolinkSearcher.getInstance()
					.getForPsmIdProteinIdProteinPosition( psmId, nrseqProteinId, proteinPosition );
			
			if ( monolinkList.isEmpty() ) {
				
				String msg = "monolinkList is empty  for psmId: " + psmId 
						+ ", nrseqProteinId: " + nrseqProteinId
						+ ", proteinPosition: " + proteinPosition;

				log.error( msg );

				throw new ProxlWebappDataException( msg );
			}
			

			if ( monolinkList.size() > 1 ) {
				
				MonolinkDTO firstMonolinkDTO = monolinkList.get( 0 );
				
				for ( int index = 1; index < monolinkList.size(); index++ ) {

					MonolinkDTO compareMonolinkDTO = monolinkList.get( index );
					
					if ( firstMonolinkDTO.getPeptideId() != compareMonolinkDTO.getPeptideId()
							|| firstMonolinkDTO.getPeptidePosition() != compareMonolinkDTO.getPeptidePosition() ) {

						String msg = "monolinkList has more than one entry and the peptide data don't match for psmId: " + psmId 
								+ ", nrseqProteinId: " + nrseqProteinId
								+ ", proteinPosition: " + proteinPosition;

						log.error( msg );

						throw new ProxlWebappDataException( msg );
					}
				}
			}

			MonolinkDTO monolinkDTO = monolinkList.get( 0 );

			int position = monolinkDTO.getPeptidePosition();

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( monolinkDTO.getPeptideId() );

			this.setPeptide( peptideDTO );

			this.setPeptidePosition( position );

		} catch ( Exception e ) {

			String msg = "Exception in populatePeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	
	
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public PeptideDTO getPeptide() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptide;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}
	public int getPeptidePosition() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptidePosition;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition(int peptidePosition) {
		this.peptidePosition = peptidePosition;
	}


	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}

	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}




	/**
	 * @return null when no scan data for search
	 * @throws Exception
	 */
	public Integer getNumUniquePsms() throws Exception {
		
		try {

			if ( numUniquePsmsSet ) {

				return numUniquePsms;
			}
			
			
			if ( this.search.isNoScanData() ) {
				
				numUniquePsms = null;
				
				numUniquePsmsSet = true;
				
				return numUniquePsms;
			}




			numUniquePsms = 
					PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( this.getReportedPeptide().getId(), this.search.getId(), searcherCutoffValuesSearchLevel );

			numUniquePsmsSet = true;

			return numUniquePsms;
			
		} catch ( Exception e ) {
			
			log.error( "getNumUniquePsms() Exception: " + e.toString(), e );
			
			throw e;
		}
	}
	

	public int getNumPsms() throws Exception {

		if ( numPsmsSet ) {

			return numPsms;
		}

		//		num psms is always based on searching psm table for: search id, reported peptide id, and psm cutoff values.

		numPsms = 
				PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
				.getPsmCountForSearchIdReportedPeptideId( reportedPeptide.getId(), search.getId(), searcherCutoffValuesSearchLevel );

		numPsmsSet = true;

		return numPsms;
	}

	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}
	


	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		try {
			
			Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getId(), this.getReportedPeptide().getId() );

			return psmId;

		} catch ( Exception e ) {

			String msg = "Exception in getSinglePsmId()";

			log.error( msg, e );

			throw e;
		}
	}
	

	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}

	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}

	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}

	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}



	public int getProteinPosition() {
		return proteinPosition;
	}

	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}
	public int getNrseqProteinId() {
		return nrseqProteinId;
	}

	public void setNrseqProteinId(int nrseqProteinId) {
		this.nrseqProteinId = nrseqProteinId;
	}



	
	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private int peptidePosition = -1;

	
	

	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;


	private SearchDTO search;
	
	private int numPsms;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;
	

	private Integer numUniquePsms;
	private boolean numUniquePsmsSet;
	

	/**
	 * Used to get the correct monolink record
	 */
	private int proteinPosition;
	
	/**
	 * Used to get the correct monolink record
	 */
	private int nrseqProteinId;

	
	
	
	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	

	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;


}
