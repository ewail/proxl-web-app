/**
 * projectPage_Root_ProjectLocked_ProjectOwnerUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Root JS file for Project Owner Users when the Project is Locked
 * 
 * 
 */


/**
 * Always do in Root Javascript for page:
 */


//  Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


 

//  Local Imports

//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';

import { viewProjectPage_loggedInUser } from './viewProjectPage_loggedInUser.js';

import { viewProject_ProjectAdminSection, set_projectPage_UserCustomProjectLabel_ProjectOwnerInteraction } from './viewProject_ProjectAdminSection.js';

import { viewProject_ProjectLockAdmin } from './viewProject_ProjectLockAdmin.js';

import { ProjectPage_UserCustomProjectLabel_ProjectOwnerInteraction } from './projectPage_UserCustomProjectLabel.js';


///////////////
$(document).ready(function() {
	try {
        // <input type="hidden" id="project_id" value="1" />

		var $project_id = $("#project_id");
		if ($project_id.length === 0) {
			throw Error("projectPage_Root_ProjectOwnerUser.js: No DOM element with id 'project_id'");
        }
        const projectIdString = $project_id.val();
		if ( projectIdString === undefined || projectIdString === "" ) {
			throw Error("projectPage_Root_ProjectOwnerUser.js: Value in DOM element with id 'project_id' is undefined or empty string");
        }

        const userIsProjectOwner = true;
        const projectLocked = true;

        const projectPage_UserCustomProjectLabel_ProjectOwnerInteraction =
            new ProjectPage_UserCustomProjectLabel_ProjectOwnerInteraction( { 
                projectIdString, userIsProjectOwner, projectLocked } );

        set_projectPage_UserCustomProjectLabel_ProjectOwnerInteraction( projectPage_UserCustomProjectLabel_ProjectOwnerInteraction );


	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
