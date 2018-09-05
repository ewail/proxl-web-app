package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

public class DSS_STY extends AmineLinkerSTY {

	@Override
	public String toString() {
		return "DSS (include STY)";
	}
	
	@Override
	public double getLinkerLength() {
		return 11.4;
	}
	
	@Override
	public Collection<String> getCrosslinkFormulas() {
		
		Collection<String> formulas = new HashSet<>();
		formulas.add( "C8H10O2" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C8H10O2";
	}
}
