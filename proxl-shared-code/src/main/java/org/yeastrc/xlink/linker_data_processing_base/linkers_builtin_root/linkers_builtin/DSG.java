package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.AmineLinker;

public class DSG extends AmineLinker {

	@Override
	public String toString() {
		return "DSG";
	}
	
	@Override
	public double getLinkerLength() {
		return 7.7;
	}

	@Override
	public Set<String> getCrosslinkFormulas() {
		
		Set<String> formulas = new HashSet<>();
		formulas.add( "C5H4O2" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C5H4O2";
	}

	@Override
	public boolean isCleavable() {
		return false;
	}
	
}
