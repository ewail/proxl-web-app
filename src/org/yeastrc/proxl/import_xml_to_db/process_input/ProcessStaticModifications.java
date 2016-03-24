package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.List;

//import org.apache.log4j.Logger;


import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;
import org.yeastrc.proxl_import.api.xml_dto.StaticModifications;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;

/**
 * 
 *
 */
public class ProcessStaticModifications {
	
//	private static final Logger log = Logger.getLogger( ProcessStaticModifications.class );
	
	/**
	 * private constructor
	 */
	private ProcessStaticModifications(){}
	
	public static ProcessStaticModifications getInstance() {
		
		return new ProcessStaticModifications();
	}

	
	
	/**
	 * @param proxlInput
	 * @param searchId
	 * @throws Exception 
	 */
	public void processStaticModifications( ProxlInput proxlInput, int searchId ) throws Exception {
		
		StaticModifications staticModifications =
				proxlInput.getStaticModifications();
		
		if ( staticModifications != null ) {

			List<StaticModification> staticModificationList =
				staticModifications.getStaticModification();

			if ( staticModificationList != null && ( ! staticModificationList.isEmpty() ) ) {

				for ( StaticModification staticModification : staticModificationList ) {

					StaticModDTO StaticModDTO = new StaticModDTO();

					StaticModDTO.setSearch_id( searchId );

					StaticModDTO.setResidue( staticModification.getAminoAcid() );
					StaticModDTO.setMass( staticModification.getMassChange() );
					StaticModDTO.setMassString( staticModification.getMassChange().toString() );

					StaticModDAO.getInstance().save( StaticModDTO );
				}
			}
		}
	}
}
