package org.yeastrc.xlink.www.forms;

public class DownloadProteinCLMSForm extends DownloadMergedSearchViewProteinsForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4330853285302130720L;

	
	
	
	public boolean isCrosslinksOnly() {
		return crosslinksOnly;
	}




	public void setCrosslinksOnly(boolean crosslinksOnly) {
		this.crosslinksOnly = crosslinksOnly;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}




	private boolean crosslinksOnly = false;
}
