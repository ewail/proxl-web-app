package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * 
 *
 */
public class ReportedPeptidesForMergedPeptidePage implements SearchPeptideCommonLinkWebserviceResultIF {

	private String searchName;
	
	private int searchId;
	
	//  Use WebReportedPeptide
	private WebReportedPeptide webReportedPeptide;
	
	private List<String> peptideAnnotationValues;
	
	private List<String> psmAnnotationValues;
	
	//  Defer to webReportedPeptide
	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		return webReportedPeptide.getReportedPeptide();
	}
	public int getNumPSMs() throws Exception {
		return webReportedPeptide.getNumPsms();
	}
	
	@Override
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValues;
	}
	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValues = psmAnnotationValueList;
	}
	@Override
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValues;
	}
	@Override
	public void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValues = peptideAnnotationValueList;
	}
	
	public List<String> getPeptideAnnotationValues() {
		return peptideAnnotationValues;
	}
	public void setPeptideAnnotationValues(List<String> peptideAnnotationValues) {
		this.peptideAnnotationValues = peptideAnnotationValues;
	}
	public List<String> getPsmAnnotationValues() {
		return psmAnnotationValues;
	}
	public void setPsmAnnotationValues(List<String> psmAnnotationValues) {
		this.psmAnnotationValues = psmAnnotationValues;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}
	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}

	
	
}
