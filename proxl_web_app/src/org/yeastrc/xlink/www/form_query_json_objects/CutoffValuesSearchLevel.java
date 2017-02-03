package org.yeastrc.xlink.www.form_query_json_objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry per Search
 *
 */
public class CutoffValuesSearchLevel {

	/**
	 * Actually projectSearchId, not changed since is set by deserializing JSON from URL
	 */
	private int searchId;

	private Map<String,CutoffValuesAnnotationLevel> psmCutoffValues;
	private Map<String,CutoffValuesAnnotationLevel> peptideCutoffValues;
	
	
	public Map<String, CutoffValuesAnnotationLevel> getPsmCutoffValues() {
		
		if ( psmCutoffValues == null ) {
			
			psmCutoffValues = new HashMap<>();
		}
		return psmCutoffValues;
	}
	public Map<String, CutoffValuesAnnotationLevel> getPeptideCutoffValues() {
		
		if ( peptideCutoffValues == null ) {
			
			peptideCutoffValues = new HashMap<>();
		}
		return peptideCutoffValues;
	}

	
	
	public void setPsmCutoffValues(
			Map<String, CutoffValuesAnnotationLevel> psmCutoffValues) {
		this.psmCutoffValues = psmCutoffValues;
	}
	public void setPeptideCutoffValues(
			Map<String, CutoffValuesAnnotationLevel> peptideCutoffValues) {
		this.peptideCutoffValues = peptideCutoffValues;
	}
	
	/**
	 * Actually projectSearchId
	 */
	public int getSearchId() {
		return searchId;
	}
	/**
	 * Actually projectSearchId
	 */
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

}
