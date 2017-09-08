
//   viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.js

//  Monolink Reported Peptide

//   Process and load data into the file viewMonolinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//   Class contructor

var ViewMonolinkReportedPeptidesLoadedFromWebServiceTemplate = function() {

	var _DATA_LOADED_DATA_KEY = "dataLoaded";
	var _handlebarsTemplate_monolink_peptide_block_template = null;
	var _handlebarsTemplate_monolink_peptide_data_row_entry_template = null;
	var _handlebarsTemplate_monolink_peptide_child_row_entry_template = null;
	
	var _excludeLinksWith_Root =  null;
	var _psmPeptideAnnTypeIdDisplay = null;
	var _psmPeptideCutoffsRootObject = null;

	//////////////
	this.setPsmPeptideCriteria = function( psmPeptideCutoffsRootObject ) {
		_psmPeptideCutoffsRootObject = psmPeptideCutoffsRootObject;
	};

	//////////////
	this.setPsmPeptideAnnTypeIdDisplay = function( psmPeptideAnnTypeIdDisplay ) {
		_psmPeptideAnnTypeIdDisplay = psmPeptideAnnTypeIdDisplay;
	};

	//////////////
	this.setExcludeLinksWith_Root = function( excludeLinksWith_Root ) {
		_excludeLinksWith_Root = excludeLinksWith_Root;
	};
	
	// ////////////
	//   Called by "onclick" on HTML element
	this.showHideMonolinkReportedPeptides = function( params ) {
		try {
			var clickedElement = params.clickedElement;
			var $clickedElement = $( clickedElement );
			var $itemToToggle = $clickedElement.next();
			if( $itemToToggle.is(":visible" ) ) {
				$itemToToggle.hide(); 
				$clickedElement.find(".toggle_visibility_expansion_span_jq").show();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").hide();
			} else { 
				$itemToToggle.show();
				$clickedElement.find(".toggle_visibility_expansion_span_jq").hide();
				$clickedElement.find(".toggle_visibility_contraction_span_jq").show();
				this.loadAndInsertMonolinkReportedPeptidesIfNeeded( { $topTRelement : $itemToToggle, $clickedElement : $clickedElement } );
			}
			return false;  // does not stop bubbling of click event
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};

	// ////////////
	this.reloadData = function( params ) {
		try {
			var $htmlElement = params.$htmlElement;
			var $itemToToggle = $htmlElement.next();

			this.loadAndInsertMonolinkReportedPeptidesIfNeeded( { 
				$topTRelement : $itemToToggle, 
				$clickedElement : $htmlElement,
				reloadData : true } );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	
	////////////////////////
	this.loadAndInsertMonolinkReportedPeptidesIfNeeded = function( params ) {
		var objectThis = this;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var reloadData = params.reloadData;
		
		if ( ! reloadData ) {
			var dataLoaded = $topTRelement.data( _DATA_LOADED_DATA_KEY );
			if ( dataLoaded ) {
				return;  //  EARLY EXIT  since data already loaded. 
			}
		}
		
		var project_search_id = $clickedElement.attr( "data-project_search_id" );
		var protein_id = $clickedElement.attr( "data-protein_id" );
		var protein_position = $clickedElement.attr( "data-protein_position" );
		//  Convert all attributes to empty string if null or undefined
		if ( ! project_search_id ) {
			project_search_id = "";
		}
		if ( ! protein_id ) {
			protein_id = "";
		}
		if ( ! protein_position ) {
			protein_position = "";
		}
		//   Currently expect _psmPeptideCriteria = 
//	           The key to:
//					searches - searchId
//					peptideCutoffValues and psmCutoffValues - annotation type id
//				peptideCutoffValues.id and psmCutoffValues.id - annotation type id
		if ( _psmPeptideCutoffsRootObject === null || _psmPeptideCutoffsRootObject === undefined ) {
			throw "_psmPeptideCutoffsRootObject not initialized";
		} 
		var psmPeptideCutoffsForProjectSearchId = _psmPeptideCutoffsRootObject.searches[ project_search_id ];
		if ( psmPeptideCutoffsForProjectSearchId === undefined || psmPeptideCutoffsForProjectSearchId === null ) {
			psmPeptideCutoffsForProjectSearchId = {};
//			throw "Getting data.  Unable to get cutoff data for project_search_id: " + project_search_id;
		}
		var psmPeptideCutoffsForProjectSearchId_JSONString = JSON.stringify( psmPeptideCutoffsForProjectSearchId );

		var psmPeptideAnnTypeDisplayPerSearchId_JSONString = null;
		if ( _psmPeptideAnnTypeIdDisplay ) {
			var psmPeptideAnnTypeIdDisplayForSearchId = _psmPeptideAnnTypeIdDisplay.searches[ project_search_id ];
			if ( psmPeptideAnnTypeIdDisplayForSearchId === undefined || psmPeptideAnnTypeIdDisplayForSearchId === null ) {
//				psmPeptideAnnTypeIdDisplayForSearchId = {};
				throw Error( "Getting data.  Unable to get ann type display data for project_search_id: " + project_search_id );
			}
			psmPeptideAnnTypeDisplayPerSearchId_JSONString = JSON.stringify( psmPeptideAnnTypeIdDisplayForSearchId );
		}

		var excludeLinksWith_Root_JSONString = undefined;
		if ( _excludeLinksWith_Root ) {
			excludeLinksWith_Root_JSONString = JSON.stringify( _excludeLinksWith_Root );
		}

		var ajaxRequestData = {
				project_search_id : project_search_id,
				protein_id : protein_id,
				protein_position : protein_position,
				psmPeptideCutoffsForProjectSearchId : psmPeptideCutoffsForProjectSearchId_JSONString,
				peptideAnnTypeDisplayPerSearch : psmPeptideAnnTypeDisplayPerSearchId_JSONString,
				excludeLinksWith_Root : excludeLinksWith_Root_JSONString
		};
		$.ajax({
			url : contextPathJSVar + "/services/data/getMonolinkReportedPeptides",
//			traditional: true,  //  Force traditional serialization of the data sent
//								//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
//								//   So searchIds array is passed as "searchIds=<value>" which is what Jersey expects
			data : ajaxRequestData,  // The data sent as params on the URL
			dataType : "json",
			success : function( ajaxResponseData ) {
				try {
					var responseParams = {
							ajaxResponseData : ajaxResponseData, 
							ajaxRequestData : ajaxRequestData,
							$topTRelement : $topTRelement,
							$clickedElement : $clickedElement
					};
					objectThis.loadAndInsertMonolinkReportedPeptidesResponse( responseParams );
					$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
				} catch( e ) {
					reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
					throw e;
				}
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error : function(jqXHR, textStatus, errorThrown) {
				handleAJAXError(jqXHR, textStatus, errorThrown);
			}
		});
	};
	
	///////
	this.loadAndInsertMonolinkReportedPeptidesResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;
		var $topTRelement = params.$topTRelement;
		var $clickedElement = params.$clickedElement;
		var show_children_if_one_row = $clickedElement.attr( "show_children_if_one_row" );
		var peptideAnnotationDisplayNameDescriptionList = ajaxResponseData.peptideAnnotationDisplayNameDescriptionList;
		var psmAnnotationDisplayNameDescriptionList = ajaxResponseData.psmAnnotationDisplayNameDescriptionList;
		var monolink_peptides = ajaxResponseData.searchPeptideMonolinkList;
		var $monolink_peptide_data_container = $topTRelement.find(".child_data_container_jq");
		if ( $monolink_peptide_data_container.length === 0 ) {
			throw "unable to find HTML element with class 'child_data_container_jq'";
		}
		$monolink_peptide_data_container.empty();
		if ( _handlebarsTemplate_monolink_peptide_block_template === null ) {
			var handlebarsSource_monolink_peptide_block_template = $( "#monolink_peptide_block_template" ).html();
			if ( handlebarsSource_monolink_peptide_block_template === undefined ) {
				throw "handlebarsSource_monolink_peptide_block_template === undefined";
			}
			if ( handlebarsSource_monolink_peptide_block_template === null ) {
				throw "handlebarsSource_monolink_peptide_block_template === null";
			}
			_handlebarsTemplate_monolink_peptide_block_template = Handlebars.compile( handlebarsSource_monolink_peptide_block_template );
		}
		if ( _handlebarsTemplate_monolink_peptide_data_row_entry_template === null ) {
			var handlebarsSource_monolink_peptide_data_row_entry_template = $( "#monolink_peptide_data_row_entry_template" ).html();
			if ( handlebarsSource_monolink_peptide_data_row_entry_template === undefined ) {
				throw "handlebarsSource_monolink_peptide_data_row_entry_template === undefined";
			}
			if ( handlebarsSource_monolink_peptide_data_row_entry_template === null ) {
				throw "handlebarsSource_monolink_peptide_data_row_entry_template === null";
			}
			_handlebarsTemplate_monolink_peptide_data_row_entry_template = Handlebars.compile( handlebarsSource_monolink_peptide_data_row_entry_template );
		}
		if ( _handlebarsTemplate_monolink_peptide_child_row_entry_template === null ) {
			if ( _handlebarsTemplate_monolink_peptide_child_row_entry_template === null ) {
				var handlebarsSource_monolink_peptide_child_row_entry_template = $( "#monolink_peptide_child_row_entry_template" ).html();
				if ( handlebarsSource_monolink_peptide_child_row_entry_template === undefined ) {
					throw "handlebarsSource_monolink_peptide_child_row_entry_template === undefined";
				}
				if ( handlebarsSource_monolink_peptide_child_row_entry_template === null ) {
					throw "handlebarsSource_monolink_peptide_child_row_entry_template === null";
				}
				_handlebarsTemplate_monolink_peptide_child_row_entry_template = Handlebars.compile( handlebarsSource_monolink_peptide_child_row_entry_template );
			}
		}
		//  Search for NumberUniquePSMs being set in any row
		var showNumberNonUniquePSMs = false;
		for ( var monolink_peptideIndex = 0; monolink_peptideIndex < monolink_peptides.length ; monolink_peptideIndex++ ) {
			var monolink_peptide = monolink_peptides[ monolink_peptideIndex ];
			if ( monolink_peptide.numNonUniquePsms !== undefined && monolink_peptide.numNonUniquePsms !== null ) {
				showNumberNonUniquePSMs = true;
				break;
			}
		}
		//  create context for header row
		var context = { 
				showNumberNonUniquePSMs : showNumberNonUniquePSMs,
				peptideAnnotationDisplayNameDescriptionList : peptideAnnotationDisplayNameDescriptionList,
				psmAnnotationDisplayNameDescriptionList : psmAnnotationDisplayNameDescriptionList
		};
		var html = _handlebarsTemplate_monolink_peptide_block_template(context);
		var $monolink_peptide_block_template = $(html).appendTo($monolink_peptide_data_container);
		var monolink_peptide_table_jq_ClassName = "monolink_peptide_table_jq";
		var $monolink_peptide_table_jq = $monolink_peptide_block_template.find("." + monolink_peptide_table_jq_ClassName );
		if ( $monolink_peptide_table_jq.length === 0 ) {
			throw "unable to find HTML element with class '" + monolink_peptide_table_jq_ClassName + "'";
		}
		//  Add monolink_peptide data to the page
		for ( var monolink_peptideIndex = 0; monolink_peptideIndex < monolink_peptides.length ; monolink_peptideIndex++ ) {
			var monolink_peptide = monolink_peptides[ monolink_peptideIndex ];
			//  wrap data in an object to allow adding more fields
			var context = { 
					showNumberNonUniquePSMs : showNumberNonUniquePSMs,
					data : monolink_peptide, 
					projectSearchId : ajaxRequestData.project_search_id
					};
			var html = _handlebarsTemplate_monolink_peptide_data_row_entry_template(context);
			var $monolink_peptide_entry = 
				$(html).appendTo($monolink_peptide_table_jq);
			//  Get the number of columns of the inserted row so can set the "colspan=" in the next row
			//       that holds the child data
			var $monolink_peptide_entry__columns = $monolink_peptide_entry.find("td");
			var monolink_peptide_entry__numColumns = $monolink_peptide_entry__columns.length;
			//  colSpan is used as the value for "colspan=" in the <td>
			var childRowHTML_Context = { colSpan : monolink_peptide_entry__numColumns };
			var childRowHTML = _handlebarsTemplate_monolink_peptide_child_row_entry_template( childRowHTML_Context );
			//  Add next row for child data
			$( childRowHTML ).appendTo($monolink_peptide_table_jq);	
			//  If only one record, click on it to show it's children
			if ( show_children_if_one_row === "true" && monolink_peptides.length === 1 ) {
				$monolink_peptide_entry.click();
			}
		}

		//  Add tablesorter to the populated table of psm data
		setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
			$monolink_peptide_table_jq.tablesorter(); // gets exception if there are no data rows
		},10);
		
		//  If the function window.linkInfoOverlayWidthResizer() exists, call it to resize the overlay
		if ( window.linkInfoOverlayWidthResizer ) {
			window.linkInfoOverlayWidthResizer();
		}
	};
};
//Static Singleton Instance of Class
var viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate = new ViewMonolinkReportedPeptidesLoadedFromWebServiceTemplate();
