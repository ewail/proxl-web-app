package org.yeastrc.xlink.www.objects;

import java.util.Map;

import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

/**
 * Result from ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher
 *
 */
public class ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem {

	private WebReportedPeptide webReportedPeptide;
	
	
	/**
	 * PSM annotation data 
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;


	/**
	 * Peptide annotation data
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;


	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}


	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}


	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap() {
		return psmAnnotationDTOMap;
	}


	public void setPsmAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap) {
		this.psmAnnotationDTOMap = psmAnnotationDTOMap;
	}


	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap() {
		return peptideAnnotationDTOMap;
	}


	public void setPeptideAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap) {
		this.peptideAnnotationDTOMap = peptideAnnotationDTOMap;
	}

	
	
}