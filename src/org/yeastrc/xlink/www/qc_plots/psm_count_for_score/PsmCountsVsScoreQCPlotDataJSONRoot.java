package org.yeastrc.xlink.www.qc_plots.psm_count_for_score;



/**
 * Root of JSON returned for Psm Count per Q value Chart
 *
 */
public class PsmCountsVsScoreQCPlotDataJSONRoot {

	private PsmCountsVsScoreQCPlotDataJSONPerType crosslinkData;
	private PsmCountsVsScoreQCPlotDataJSONPerType looplinkData;
	private PsmCountsVsScoreQCPlotDataJSONPerType unlinkedData;
	
	private PsmCountsVsScoreQCPlotDataJSONPerType alllinkData;
	
	private int dataArraySize;
	
	private boolean sortDirectionAbove;
	private boolean sortDirectionBelow;
	

	public boolean isSortDirectionAbove() {
		return sortDirectionAbove;
	}

	public void setSortDirectionAbove(boolean sortDirectionAbove) {
		this.sortDirectionAbove = sortDirectionAbove;
	}

	public boolean isSortDirectionBelow() {
		return sortDirectionBelow;
	}

	public void setSortDirectionBelow(boolean sortDirectionBelow) {
		this.sortDirectionBelow = sortDirectionBelow;
	}

	public int getDataArraySize() {
		return dataArraySize;
	}

	public void setDataArraySize(int dataArraySize) {
		this.dataArraySize = dataArraySize;
	}

	public PsmCountsVsScoreQCPlotDataJSONPerType getCrosslinkData() {
		return crosslinkData;
	}

	public void setCrosslinkData(
			PsmCountsVsScoreQCPlotDataJSONPerType crosslinkData) {
		this.crosslinkData = crosslinkData;
	}

	public PsmCountsVsScoreQCPlotDataJSONPerType getLooplinkData() {
		return looplinkData;
	}

	public void setLooplinkData(PsmCountsVsScoreQCPlotDataJSONPerType looplinkData) {
		this.looplinkData = looplinkData;
	}

	public PsmCountsVsScoreQCPlotDataJSONPerType getUnlinkedData() {
		return unlinkedData;
	}

	public void setUnlinkedData(PsmCountsVsScoreQCPlotDataJSONPerType unlinkedData) {
		this.unlinkedData = unlinkedData;
	}

	public PsmCountsVsScoreQCPlotDataJSONPerType getAlllinkData() {
		return alllinkData;
	}

	public void setAlllinkData(PsmCountsVsScoreQCPlotDataJSONPerType alllinkData) {
		this.alllinkData = alllinkData;
	}
	
}
