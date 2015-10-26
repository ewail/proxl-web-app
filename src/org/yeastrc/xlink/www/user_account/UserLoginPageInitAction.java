package org.yeastrc.xlink.www.user_account;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * 
 *
 */
public class UserLoginPageInitAction  extends Action {
	
	private static final Logger log = Logger.getLogger(UserLoginPageInitAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {

//			String getRequestURL = request.getRequestURL().toString();
//			String getRequestURI = request.getRequestURI();
//			String getProtocol = request.getProtocol();
			
					
					
//			UserLoginForm form = (UserLoginForm) actionForm;


			return mapping.findForward( "Success" );


		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}

		
}
