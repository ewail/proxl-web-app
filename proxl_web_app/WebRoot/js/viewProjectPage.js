
	
//    viewProjectPage.js

//  Javascript for the viewProject.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


var _project_id = null;


	 		$(document).ready(function()  { 
				
	 			try {

	 				initPage();

	 			} catch( e ) {
	 				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 				throw e;
	 			}
	 				
			});
	 		
	 		/////////////
	 		
	 		function initPage() {
	 			
	 			var project_id = $("#project_id").val();
	 			
	 			if ( project_id === undefined || project_id === null 
	 					|| project_id === "" ) {
	 				
	 				throw Error( '$("#project_id").val() returned no value' );
	 			
	 			} else {
	 				
	 				_project_id = project_id;
	 			}
	 			
	 			
	 			
	 			//  tool tips for files attached to searches
	 			
	 			
	 			$(".search_file_link_for_tooltip_jq").each(function() { // Grab search file links
	 				
	 				var $search_file_link_tooltip_jq = $(this).children(".search_file_link_tooltip_jq");
	 				
	 				if ( $search_file_link_tooltip_jq.length > 0 ) {

	 					var tipText = $search_file_link_tooltip_jq.text();

	 					$(this).qtip({ 
	 						content: {
	 							text: tipText
	 						}
	 					});
	 				}
	 			});
	 			
	 			
	 			
	 			//   Attach Delete Search click handlers
	 			

	 			$(".delete_search_link_jq").click(function(eventObject) {

	 				var clickThis = this;
	 				
	 				try {

	 					deleteSearchClickHandler( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}
	 				
	 				return false;
	 			});	 			
	 			
	 			
	 			$("#delete_search_confirm_button").click(function(eventObject) {

	 				var clickThis = this;

	 				try {

	 					deleteSearchConfirmed( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}

	 				return false;
	 			});
	 			

	 			$(".delete_search_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				try {

	 					closeConfirmDeleteSearchOverlay( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}
	 				
	 				return false;
	 			});
	 			

	 			
	 			///////  Attach Delete Search Weblink Click handlers
	 			
	 			

	 			$(".delete_search_webLink_link_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				try {

	 					deleteSearchWebLinkClickHandler( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}
	 				
	 				return false;
	 			});	 			

	 			
	 			$("#delete_search_web_link_confirm_button").click(function(eventObject) {

	 				var clickThis = this;

	 				try {

	 					deleteSearchWebLinkConfirmed( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}

	 				return false;
	 			});
	 			

	 			$(".delete_search_web_link_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;

	 				try {

	 					closeConfirmDeleteSearchWebLinkOverlay( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}
	 				
	 				return false;
	 			});
	 			
	 			
	 			
	 			
	 			///////  Attach Delete Search Comment Click handlers
	 			
	 			
	 			
	 			$("#delete_search_comment_confirm_button").click(function(eventObject) {

	 				var clickThis = this;
	 				
	 				try {

	 					deleteSearchCommentConfirmed( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}

	 				return false;
	 			});
	 			

	 			$(".delete_search_comment_overlay_show_hide_parts_jq").click(function(eventObject) {

	 				var clickThis = this;
	 				
	 				try {

	 					closeConfirmDeleteSearchCommentOverlay( clickThis, eventObject );

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}

	 				return false;
	 			});
	 			
	 			
	 			
	 			/////////////////////////
	 			
	 			
	 		   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
	 			  
	 				try {
		 			
	 					initQCPlotsClickHandlers();

	 					initQCPlotPSMCountVsScoreClickHandlers();

	 				} catch( e ) {
	 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	 					throw e;
	 				}
	 		   },10);
	 			
	 			
	 			
	 			//  Initialize the buttons from the current values of the check boxes.
	 			//     The check boxes may be checked from using the back button.
	 			updateButtonsBasedOnCheckedSearches ( );
					
	 		}
	 		
	 		
	 		/////////////
			
			var searchesToMerge = new Array();
			
			
			

			///////////////
			
			
			function validateURL(textval) {
			      var urlregex = new RegExp(
			            "^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
			      return urlregex.test(textval);
			}
			
			
			//////////

			function addWebLink( searchId ) {
			
				var _URL = contextPathJSVar + "/services/searchWebLinks/add";
				
				var $linkUrlInputField = $( "input#web-links-url-input-" + searchId );
				
				var linkUrl = $linkUrlInputField.val();
				if( linkUrl === undefined || linkUrl === "" ) { return; }

				var linkLabel = $( "input#web-links-label-input-" + searchId ).val();
				
				if( linkLabel === undefined || linkLabel === "" ) { 
					
					linkLabel = linkUrl;
				}
				
				
				
				
				if ( validateURL( linkUrl )  ) {
					
				} else {
					
//					alert("url not valid");
					
					var $element = $("#error_message_web_link_url_invalid_" + searchId );
					
//					var linkUrlInputFieldTop = $linkUrlInputField.offset().top;
//					
//					$element.css("top", linkUrlInputFieldTop );
					
					showErrorMsg( $element );
						
					return;  //  !!!  EARLY EXIT
				}
				
				
				
//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'searchId' : searchId, 'linkUrl' : linkUrl, linkLabel: linkLabel },
				        dataType: "json",
				        success: function(data)	{
				        	
				        	try {

				        		// add new web link to DOM

				        		var id = data.id;

				        		var source = $("#web_link_template").html();

				        		if ( source === undefined ) {
				        			throw Error( '$("#web_link_template").html() === undefined' );
				        		}
				        		if ( source === null ) {
				        			throw Error( '$("#web_link_template").html() === null' );
				        		}

				        		var template = Handlebars.compile(source);

				        		var context = data;

				        		var html = template(context);

				        		var web_link_root_container_div_jq = $( html ).insertBefore( "#add-web-links-link-span-" + searchId );
				        		addToolTips( web_link_root_container_div_jq );


//				        		attachProjectNoteMaintOnClick( web_link_root_container_div_jq );

//				        		$("#add_note_field").val("");



				        		$("#web-links-delete-" + id).click(function(eventObject) {

				        			var clickThis = this;
				        			
				        			try {

				        				deleteSearchWebLinkClickHandler( clickThis, eventObject );

					 				} catch( e ) {
					 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					 					throw e;
					 				}

				        			return false;
				        		});	 	

				        		$( "div#web-links-" + id ).show( 200 );

				        		$( "#add-web-links-form-span-" + searchId ).hide();
				        		$( "#add-web-links-link-span-" + searchId ).show();
				        		$( "input#web-links-input-" + searchId ).val( "" );

			 				} catch( e ) {
			 					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			 					throw e;
			 				}

														
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
				        error: function(jqXHR, textStatus, errorThrown) {	
				        	
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
			}

			
			//////////

			//   Called by "onclick" on HTML element
			
			function cancelWebLink( id ) {
				
				try {
					$( "#add-web-links-form-span-" + id ).hide();
					$( "#add-web-links-link-span-" + id ).show();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element
			
			function showAddWebLink( id ) {
				
				try {

					$( "input#web-links-url-input-" + id ).val("");
					$( "input#web-links-label-input-" + id ).val( "" );

					$( "#add-web-links-form-span-" + id ).show();
					$( "#add-web-links-link-span-" + id ).hide();
					$( "#web-links-url-input-" + id ).focus();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			
			////////////////////
			
			//  Files for Search Maint
			

			//////////
			
			//   Called by "onclick" on HTML element

			function saveSearchFilename( clickThis ) {
			
				try {

					var _URL = contextPathJSVar + "/services/search_file/updateDisplayFilename";
				

					var $clickThis = $( clickThis );

					var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");

					var search_file_id = $display_search_filename_outer_container_jq.attr("search_file_id");
//					var search_id = $display_search_filename_outer_container_jq.attr("search_id");

					var $edit_search_filename_input_field_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_input_field_jq");

					var edit_search_filename_input_value = $edit_search_filename_input_field_jq.val(); 

					if ( edit_search_filename_input_value === "" ) {

						return;
					}

					var requestData = { 'project_id' : project_id, 
							'searchFileId' : search_file_id, 
							displayFilename: edit_search_filename_input_value };


//					var request = 
					$.ajax({
						type: "POST",
						url: _URL,
						data: requestData,
						dataType: "json",
						success: function(data)	{

							try {

								var $search_filename_jq = $display_search_filename_outer_container_jq.find(".search_filename_jq");
								$search_filename_jq.text( edit_search_filename_input_value );

								var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
								var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
								$edit_search_filename_container_jq.hide();
								$display_search_filename_container_jq.show();

							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
						},
						failure: function(errMsg) {
							handleAJAXFailure( errMsg );
						},
						error: function(jqXHR, textStatus, errorThrown) {	

							handleAJAXError( jqXHR, textStatus, errorThrown );

//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
					});

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			
			//////////

			//   Called by "onclick" on HTML element
			
			function cancelSearchFilenameEdit( clickThis ) {
				
				try {

					var $clickThis = $( clickThis );

					var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");

					var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
					var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
					$edit_search_filename_container_jq.hide();
					$display_search_filename_container_jq.show();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//////////////
			
			//   Called by "onclick" on HTML element
			
			function showSearchFilenameForm( clickThis ) {

				try {

					var $clickThis = $( clickThis );

					var $display_search_filename_outer_container_jq = $clickThis.closest(".display_search_filename_outer_container_jq");

					var $display_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".display_search_filename_container_jq");
					var $edit_search_filename_container_jq = $display_search_filename_outer_container_jq.find(".edit_search_filename_container_jq");
					$edit_search_filename_container_jq.show();
					$display_search_filename_container_jq.hide();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			
			
			///////////////////////////////
			
			
			////////////  Search Comment
			
			
			
			

			///////////////

			//   Called by "onclick" on HTML element
			
			function showSearchCommentEditForm( clickThis ) {

				try {

					var $clickThis = $( clickThis );

					var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");

					var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
					var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");

					var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
					var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");

					var search_comment_value = $search_comment_string_jq.text();
					$search_comment_input_field_jq.val( search_comment_value );


					$search_comment_edit_jq.show();
					$search_comment_display_jq.hide();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
			


			///////////////

			//   Called by "onclick" on HTML element
			
			function cancelSearchCommentEditForm( clickThis ) {

				try {

					var $clickThis = $( clickThis );

					var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");

					var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
					var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");


					$search_comment_edit_jq.hide();
					$search_comment_display_jq.show();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}			

			///////////////
			
			
			
			//////////

			//   Called by "onclick" on HTML element
			
			function updateSearchComment( clickThis ) {
			
				try {

					var _URL = contextPathJSVar + "/services/searchComment/updateText";

					var $clickThis = $( clickThis );

					var $search_comment_root_jq = $clickThis.closest(".search_comment_root_jq");

					var searchCommentId = $search_comment_root_jq.attr("searchCommentId");	

					var $search_comment_input_field_jq = $search_comment_root_jq.find(".search_comment_input_field_jq");

					var search_comment_value = $search_comment_input_field_jq.val();



//					var request = 
					$.ajax({
						type: "POST",
						url: _URL,
						data: { 'id' : searchCommentId, 'comment' : search_comment_value },
						dataType: "json",
						success: function(data)	{

							try {

								// update comment 

								var $search_comment_string_jq = $search_comment_root_jq.find(".search_comment_string_jq");
								var $search_comment_date_jq = $search_comment_root_jq.find(".search_comment_date_jq");

								$search_comment_string_jq.text( data.comment );
								$search_comment_date_jq.text( data.dateTimeString );

								var $search_comment_display_jq = $search_comment_root_jq.find(".search_comment_display_jq");
								var $search_comment_edit_jq = $search_comment_root_jq.find(".search_comment_edit_jq");

								$search_comment_edit_jq.hide();
								$search_comment_display_jq.show();

							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}

						},
						failure: function(errMsg) {
							handleAJAXFailure( errMsg );
						},
						error: function(jqXHR, textStatus, errorThrown) {	

							handleAJAXError( jqXHR, textStatus, errorThrown );
						}
					});

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

						
			
			
			//////////

			//   Called by "onclick" on HTML element
			
			function addComment( searchId ) {
			
				try {

					var _URL = contextPathJSVar + "/services/searchComment/add";

					var comment = $( "input#comment-input-" + searchId ).val();
					if( comment == undefined || comment == "" ) { return; }



//					var request = 
					$.ajax({
						type: "POST",
						url: _URL,
						data: { 'searchId' : searchId, 'comment' : comment },
						dataType: "json",
						success: function(data)	{

							try {

								// add new comment to DOM

								var id = data[ 'id' ];


								var source = $("#search_comment_template").html();

								if ( source === undefined ) {
									throw Error( '$("#search_comment_template").html() === undefined' );
								}
								if ( source === null ) {
									throw Error( '$("#search_comment_template").html() === null' );
								}

								var template = Handlebars.compile(source);

								var context = data;

								var html = template(context);

//								var comment_root_container_div_jq = 
								$inserted = $( html ).insertBefore( "span#add-comment-link-span-" + searchId );
								addToolTips( $inserted );

								$( "div#comment-" + id ).show( 200 );

								$( "span#add-comment-form-span-" + searchId ).hide();
								$( "span#add-comment-link-span-" + searchId ).show();

								$( "input#comment-input-" + searchId ).val( "" );

							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}

						},
						failure: function(errMsg) {
							handleAJAXFailure( errMsg );
						},
						error: function(jqXHR, textStatus, errorThrown) {	

							handleAJAXError( jqXHR, textStatus, errorThrown );

//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
					});

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			
			//////////

			//   Called by "onclick" on HTML element
			
			function cancelComment( id ) {
			
				try {

					$( "span#add-comment-form-span-" + id ).hide();
					$( "span#add-comment-link-span-" + id ).show();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element
			
			function showAddComment( id ) {

				try {

					$( "span#add-comment-form-span-" + id ).show();
					$( "span#add-comment-link-span-" + id ).hide();
					$( "#comment-input-" + id ).focus();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}


			
			//   Delete Search processing
			

			/////////////////

			var deleteSearchClickHandler = function(clickThis, eventObject) {

				openConfirmDeleteSearchOverlay(clickThis, eventObject);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchOverlay = function(clickThis, eventObject) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_root_jq");

				var searchId = $search_root_jq.attr("searchId");	

				// copy the search name to the overlay

				var $search_name_display_jq = $search_root_jq.find(".search_name_display_jq");

				var search_name_display_jq = $search_name_display_jq.text();

				var $delete_search_overlay_search_name = $("#delete_search_overlay_search_name");
				$delete_search_overlay_search_name.text( search_name_display_jq );



				var $delete_search_confirm_button = $("#delete_search_confirm_button");
				$delete_search_confirm_button.data("searchId", searchId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_overlay_containing_outermost_div_inline_div = $("#delete_search_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_overlay_container = $("#delete_search_overlay_container");
				
				var height__delete_search_overlay_container = $delete_search_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_overlay_container / 2 );
				
				$delete_search_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_overlay_background = $("#delete_search_overlay_background"); 
				$delete_search_overlay_background.show();
				$delete_search_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchOverlay = function(clickThis, eventObject) {

				var $delete_search_confirm_button = $("#delete_search_confirm_button");
				$delete_search_confirm_button.data("searchId", null);

				$(".delete_search_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_confirm_button

			var deleteSearchConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchId = $clickThis.data("searchId");
				
				if ( searchId === undefined || searchId === null ) {
					
					throw Error( " searchId === undefined || searchId === null " );
				}

				if ( searchId === "" ) {
					
					throw Error( ' searchId === "" ' );
				}

				document.location.href= contextPathJSVar + "/deleteSearch.do?searchId=" + searchId;
								
				
				closeConfirmDeleteSearchOverlay();
			};
			
			

			//   END   Delete Search processing
			
			
			
			

			
			//   Delete Search Comment processing
			

			/////////////////

			var deleteSearchCommentClickHandler = function(clickThis) {

				openConfirmDeleteSearchCommentOverlay(clickThis);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchCommentOverlay = function(clickThis) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_comment_root_jq");

				var searchCommentId = $search_root_jq.attr("searchCommentId");	



				var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
				$delete_search_comment_confirm_button.data("searchCommentId", searchCommentId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_comment_overlay_containing_outermost_div_inline_div = $("#delete_search_comment_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_comment_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_comment_overlay_container = $("#delete_search_comment_overlay_container");
				
				var height__delete_search_comment_overlay_container = $delete_search_comment_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_comment_overlay_container / 2 );
				
				$delete_search_comment_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_comment_overlay_background = $("#delete_search_comment_overlay_background"); 
				$delete_search_comment_overlay_background.show();
				$delete_search_comment_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchCommentOverlay = function(clickThis, eventObject) {

				var $delete_search_comment_confirm_button = $("#delete_search_comment_confirm_button");
				$delete_search_comment_confirm_button.data("searchCommentId", null);

				$(".delete_search_comment_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_comment_confirm_button

			var deleteSearchCommentConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchCommentId = $clickThis.data("searchCommentId");
				
				if ( searchCommentId === undefined || searchCommentId === null ) {
					
					throw Error( " searchCommentId === undefined || searchCommentId === null " );
				}

				if ( searchCommentId === "" ) {
					
					throw Error( ' searchCommentId === "" ' );
				}

				var _URL = contextPathJSVar + "/services/searchComment/delete";

//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'id' : searchCommentId },
				        dataType: "json",
				        success: function(data)	{

							try {

								$( "div#comment-" + searchCommentId ).hide(200, function() { $( "div#comment-" + searchCommentId ).remove(); });

							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
						error: function(jqXHR, textStatus, errorThrown) {	
						
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
				
				closeConfirmDeleteSearchCommentOverlay();
			};
			
			

			//   END   Delete Search Comment processing
			
			
			
			

			
			//   Delete Search Web Link processing
			

			/////////////////

			var deleteSearchWebLinkClickHandler = function(clickThis, eventObject) {

				openConfirmDeleteSearchWebLinkOverlay(clickThis, eventObject);

				return;

			};

			///////////
			
			var openConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {

				var $clickThis = $(clickThis);

				//	get root div for this search
				var $search_root_jq = $clickThis.closest(".search_web_link_root_jq");

				var searchwebLinkId = $search_root_jq.attr("searchwebLinkId");	



				var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
				$delete_search_web_link_confirm_button.data("searchwebLinkId", searchwebLinkId);
				
				// Position dialog over clicked delete icon
				
				//  get position of div containing the dialog that is inline in the page
				var $delete_search_web_link_overlay_containing_outermost_div_inline_div = $("#delete_search_web_link_overlay_containing_outermost_div_inline_div");
				
				var offset__containing_outermost_div_inline_div = $delete_search_web_link_overlay_containing_outermost_div_inline_div.offset();
				var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
				
				var offset__ClickedDeleteIcon = $clickThis.offset();
				var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
				
				var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
				
				//  adjust vertical position of dialog 
				
				var $delete_search_web_link_overlay_container = $("#delete_search_web_link_overlay_container");
				
				var height__delete_search_web_link_overlay_container = $delete_search_web_link_overlay_container.outerHeight( true /* [includeMargin ] */ );
				
				var positionAdjust = offsetDifference - ( height__delete_search_web_link_overlay_container / 2 );
				
				$delete_search_web_link_overlay_container.css( "top", positionAdjust );

				
				var $delete_search_web_link_overlay_background = $("#delete_search_web_link_overlay_background"); 
				$delete_search_web_link_overlay_background.show();
				$delete_search_web_link_overlay_container.show();
			};

			//////////	/

			var closeConfirmDeleteSearchWebLinkOverlay = function(clickThis, eventObject) {

				var $delete_search_web_link_confirm_button = $("#delete_search_web_link_confirm_button");
				$delete_search_web_link_confirm_button.data("searchwebLinkId", null);

				$(".delete_search_web_link_overlay_show_hide_parts_jq").hide();
			};


			/////////////////

			//	put click handler for this on #delete_search_web_link_confirm_button

			var deleteSearchWebLinkConfirmed = function(clickThis, eventObject) {
				

				var $clickThis = $(clickThis);

				var searchwebLinkId = $clickThis.data("searchwebLinkId");
				
				if ( searchwebLinkId === undefined || searchwebLinkId === null ) {
					
					throw Error( " searchwebLinkId === undefined || searchwebLinkId === null " );
				}

				if ( searchwebLinkId === "" ) {
					
					throw Error( ' searchwebLinkId === "" ' );
				}

				var _URL = contextPathJSVar + "/services/searchWebLinks/delete";

//				var request = 
				$.ajax({
				        type: "POST",
				        url: _URL,
				        data: { 'id' : searchwebLinkId },
				        dataType: "json",
				        success: function(data)	{
							
				        	try {

								$( "div#web-links-" + searchwebLinkId ).hide(200, function() { $( "div#web-links-" + searchwebLinkId ).remove(); });

				        	} catch( e ) {
				        		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				        		throw e;
				        	}
						},
				        failure: function(errMsg) {
				        	handleAJAXFailure( errMsg );
				        },
						error: function(jqXHR, textStatus, errorThrown) {	
						
							handleAJAXError( jqXHR, textStatus, errorThrown );

//								alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
				  });
					
				
				closeConfirmDeleteSearchWebLinkOverlay();
			};
			
			

			//   END   Delete Search Web Link processing
			
			
			
						
			
						
			
			
			
			//////////

			//   Called by "onclick" on HTML element
			
			function checkSearchCheckboxes( searchId) {

				try {

					if( $( "input#search-checkbox-" + searchId ).is( ":checked" ) ) {
						if( searchesToMerge.indexOf( searchId ) == -1 ) { searchesToMerge.push( searchId ); }
					} else {
						var index = searchesToMerge.indexOf( searchId );
						if( index != -1 ) {
							searchesToMerge.splice( index, 1 );
						}
					}

					updateButtonsBasedOnCheckedSearches();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
			
			
			//////////
			
			function updateButtonsBasedOnCheckedSearches ( ) {
				
				var count = 0;
				
				$( ".search-checkbox" ).each( function() {
					if( $( this ).is( ":checked" ) ) {
						count++;
					}
				});				

				if( count < 2 ) { 
					disableButtons(); 
				} else { 
					enableButtons(); 
				}
				
				//  The following function is only on the page for admin so an exception will occur for non admin
				
				try {
					updateMoveSearchesButtonFromSearchCheckboxes( count );
				}
				catch(err) {
				    
				}					
				
			}
			
			
			//////////
			
			function disableButtons() {
				$( ".merge-button" ).attr("disabled", "disabled"); 
				//  show covering div
				$(".merge_button_disabled_cover_div_jq").show();
			}
			
			function enableButtons() {
				$( ".merge-button" ).removeAttr("disabled"); 
				//  hide covering div
				$(".merge_button_disabled_cover_div_jq").hide();
			}

			//   Called by "onclick" on HTML element
			
			function viewMergedPeptides() {

				try {

					$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/mergedPeptide.do");
					$( "form#viewMergedDataForm" ).submit();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element
			
			function viewMergedProteins() {
				
				try {

					$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/mergedCrosslinkProtein.do");
					$( "form#viewMergedDataForm" ).submit();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element
			
			function viewMergedImage() {
				$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/image.do");
				$( "form#viewMergedDataForm" ).submit();
			}

			//   Called by "onclick" on HTML element
			
			function viewMergedStructure() {

				try {

					$( "form#viewMergedDataForm" ).attr("action", contextPathJSVar + "/structure.do");
					$( "form#viewMergedDataForm" ).submit();

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}


			//   Called by "onclick" on HTML element
			
			function showSearchDetails( id ) {
				
				try {

					if( $( "table#search-details-" + id ).is( ":visible" ) ) {
						$( "table#search-details-" + id ).hide();
//						$( "a#search-details-link-" + id ).html( "[+]" );
						$( "a#search-details-link-" + id ).html( '<img src="' + contextPathJSVar + '/images/icon-expand-small.png">' );

					} else {
						$( "table#search-details-" + id ).show();
//						$( "a#search-details-link-" + id ).html( "[-]" );
						$( "a#search-details-link-" + id ).html( '<img src="' + contextPathJSVar + '/images/icon-collapse-small.png">' );
					}

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
			

			//   Called by "onclick" on HTML element
			
			function expandAll() {

				try {

					$( "table.search-details" ).show();
					$( "a.expand-link" ).html( '<img src="' + contextPathJSVar + '/images/icon-collapse-small.png">' );

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element

			function collapseAll() {

				try {

					$( "table.search-details" ).hide();
					$( "a.expand-link" ).html( '<img src="' + contextPathJSVar + '/images/icon-expand-small.png">' );

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			//   Called by "onclick" on HTML element

			function showSearchNameForm( id ) {

				try {

					$( "span#search-name-normal-" + id ).hide();
					$( "span#search-name-edit-" + id ).show();	

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}

			function cancelNameEdit( id ) {

				try {

					$( "span#search-name-edit-" + id ).hide();
					$( "span#search-name-normal-" + id ).show();

					$( "input#search-name-value-" + id ).val( $( "span#search-name-display-" + id ).html() );

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			}
			
			
			//////////

			//   Called by "onclick" on HTML element
			
			function saveName( searchId ) {
	
				try {

					var _URL = contextPathJSVar + "/services/searchName/save";

					var name = $( "input#search-name-value-" + searchId ).val();
					if( name == undefined || name == "" ) { return; }

//					var request = 
					$.ajax({
						type: "POST",
						url: _URL,
						data: { 'searchId' : searchId, 'name' : name },
						dataType: "json",
						success: function(data)	{

							try {

								$( "span#search-name-display-" + searchId ).html( name );
								$( "span#search-name-edit-" + searchId ).hide();
								$( "span#search-name-normal-" + searchId ).show();

							} catch( e ) {
								reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
								throw e;
							}
						},
						failure: function(errMsg) {
							handleAJAXFailure( errMsg );
						},
						error: function(jqXHR, textStatus, errorThrown) {	

							handleAJAXError( jqXHR, textStatus, errorThrown );

//							alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ", textStatus: " + textStatus );
						}
					});

				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			
			}
			
			
			