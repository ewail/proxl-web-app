package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideDimer {
	
	private static final Logger log = Logger.getLogger(SearchPeptideDimer.class);

	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptideId(): " + this.getReportedPeptideId() );
			
			return;
		}
		
		try {

			//  Get MatchedPeptide table entries for a psm.
			
			List<MatchedPeptideDTO> results = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId(  psmId );

			if ( results.size() < 2 ) {
				

				String msg = "results.size() < 2 for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}
			

			PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(0).getPeptide_id() );

			this.setPeptide1( peptideDTO1 );
			

			PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(1).getPeptide_id() );

			this.setPeptide2( peptideDTO2 );


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
	

	public int getReportedPeptideId() {
		return reportedPeptideId;
	}

	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}


	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		
		try {
			if ( reportedPeptide == null ) {

				reportedPeptide = 
						ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId );
			}

			return reportedPeptide;

		} catch ( Exception e ) {

			log.error( "getReportedPeptide() Exception: " + e.toString(), e );

			throw e;
		}
			
	}


	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;

		if ( reportedPeptide != null ) {
			this.reportedPeptideId = reportedPeptide.getId();
		}

	}


	public PeptideDTO getPeptide1() throws Exception {
		
		try {

			if( this.peptide1 == null )
				populatePeptides();

			return peptide1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1(PeptideDTO peptide) {
		this.peptide1 = peptide;
	}
	
	public PeptideDTO getPeptide2() throws Exception {
		
		try {

			if( this.peptide2 == null )
				populatePeptides();

			return peptide2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2(PeptideDTO peptide) {
		this.peptide2 = peptide;
	}


	
	
	public int getNumPsms() {
		
		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");
//		return numPsms;
	}
	public void setNumPsms(int numPsms) {

		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");
//		return numPsms;

//		this.numPsms = numPsms;
	}
	
	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getId(), this.getReportedPeptide().getId() );
		
		return psmId;
	}
	
	
	
	
	public List<SearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		try {

			if( this.peptide1ProteinPositions == null )
				this.peptide1ProteinPositions = SearchProteinSearcher.getInstance().getProteinForDimer( this.search, this.peptide1);

			return peptide1ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	
	public List<SearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		try {

			if( this.peptide2ProteinPositions == null )
				this.peptide2ProteinPositions = SearchProteinSearcher.getInstance().getProteinForDimer( this.search, this.peptide2);

			return peptide2ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
	

	private int reportedPeptideId = -999;


	private ReportedPeptideDTO reportedPeptide;
	
	
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;

	
	
	private SearchDTO search;
//	private List<SearchProteinDoublePosition> peptideProteinPositions;
	
//	private int numPsms = -999;
	
}