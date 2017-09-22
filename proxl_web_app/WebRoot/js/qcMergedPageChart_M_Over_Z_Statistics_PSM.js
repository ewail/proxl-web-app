/**
 * qcMergedPageChart_M_Over_Z_Statistics_PSM.js
 * 
 * Javascript for the viewQCMerged.jsp page - Chart M/Z Statistics - in PSM Level section
 * 
 * page variable qcMergedPageChart_M_Over_Z_Statistics_PSM
 * 
 * Merged QC Page
 * 
 * This code has been updated to cancel existing active AJAX calls when "Update from Database" button is clicked.
 *   This is done so that previous AJAX responses don't overlay new AJAX responses.
 */

//JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";


/**
 * Constructor 
 */
var QCMergedPageChart_M_Over_Z_Statistics_PSM = function() {
	
	//  Download data URL
	var _downloadStrutsAction = "downloadQC_Psm_M_Over_Z_ChartData.do";

//	/**
//	 * Overridden for Specific elements like Chart Title and X and Y Axis labels
//	 */
//	var _CHART_GLOBALS = {
//			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
//			_TITLE_FONT_SIZE : 15, // In PX
//			_AXIS_LABEL_FONT_SIZE : 14, // In PX
//			_TICK_MARK_TEXT_FONT_SIZE : 14 // In PX
//
//			, _ENTRY_ANNOTATION_TEXT_SIGNIFICANT_DIGITS : 2
//	}
	
	//  From QCPageMain
	var _OVERALL_GLOBALS;

	var _project_search_ids = undefined;
	var _searchIdsObject_Key_projectSearchId = undefined;

	var _colorsPerSearch = undefined;
	
	var _anySearchesHaveScanDataYes = undefined;

	//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
	var _common_chart_outer_entry_templateHTML = undefined;

	var _common_chart_inner_entry_templateHTML = undefined;

	var _dummy_chart_entry_for_message_templateHTML = undefined;


	var _link_type_crosslink_constant = undefined;
	var _link_type_looplink_constant = undefined;
	var _link_type_unlinked_constant = undefined;
	var _link_type_default_selected = undefined;

	var _link_type_crosslink_LOWER_CASE_constant = undefined;
	var _link_type_looplink_LOWER_CASE_constant = undefined;
	var _link_type_unlinked_LOWER_CASE_constant = undefined;

	var _link_type_combined_LOWER_CASE_constant = undefined;

	//   These will have the link type added in between prefix and suffix, adding a space after link type.
	//       There is no space at start of suffix to support no link type
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = undefined;
	var _DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = undefined;
	var _DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = undefined;

	var _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = undefined;

	var _IS_LOADED_YES = "YES";
	var _IS_LOADED_NO = "NO";
	var _IS_LOADED_LOADING = "LOADING";
	

	//  passed in functions

	//  Copy references to qcPageMain functions to here
	this._passAJAXErrorTo_handleAJAXError = undefined;
	this._addChartOuterTemplate = undefined;
	this._addChartInnerTemplate = undefined;
	this._placeEmptyDummyChartForMessage = undefined;
	this.getColorAndBarColorFromLinkType = undefined;
	
	var _get_hash_json_Contents = undefined; // function on qcPageMain
	
	
	///////////
	
	//   Variables for this chart
	
	var _chart_isLoaded = _IS_LOADED_NO;

	/**
	 * Init page Actual - Called from qcPageMain.initActual
	 */
	this.initActual = function( params ) {
		try {
			var objectThis = this;

			_OVERALL_GLOBALS = params.OVERALL_GLOBALS;

			_project_search_ids = params.project_search_ids;
			_searchIdsObject_Key_projectSearchId = params.searchIdsObject_Key_projectSearchId;

			_colorsPerSearch = params.colorsPerSearch;
			
			_anySearchesHaveScanDataYes = params.anySearchesHaveScanDataYes;

			//  Contains {{link_type}} to replace with link type.  Contains {{link_type}}_chart_outer_container_jq chart_outer_container_jq
			_common_chart_outer_entry_templateHTML = params.common_chart_outer_entry_templateHTML;
			_common_chart_inner_entry_templateHTML = params.common_chart_inner_entry_templateHTML;
			_dummy_chart_entry_for_message_templateHTML = params.dummy_chart_entry_for_message_templateHTML;

			_link_type_crosslink_constant = params.link_type_crosslink_constant;
			_link_type_looplink_constant = params.link_type_looplink_constant;
			_link_type_unlinked_constant = params.link_type_unlinked_constant;
			_link_type_default_selected = params.link_type_default_selected;

			_link_type_crosslink_LOWER_CASE_constant = params.link_type_crosslink_LOWER_CASE_constant;
			_link_type_looplink_LOWER_CASE_constant = params.link_type_looplink_LOWER_CASE_constant;
			_link_type_unlinked_LOWER_CASE_constant = params.link_type_unlinked_LOWER_CASE_constant;

			_link_type_combined_LOWER_CASE_constant = params.link_type_combined_LOWER_CASE_constant;

			//   These will have the link type added in between prefix and suffix, adding a space after link type.
			//       There is no space at start of suffix to support no link type
			_DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA;
			_DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_PREFIX_ERROR_LOADING;
			_DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING = params.DUMMY_CHART_STATUS_TEXT_SUFFIX_ERROR_LOADING;

			_DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED = params.DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED;
			
			//  Copy references to qcPageMain functions to here
			this._passAJAXErrorTo_handleAJAXError = params._passAJAXErrorTo_handleAJAXError;
			this._addChartOuterTemplate = params._addChartOuterTemplate;
			this._addChartInnerTemplate = params._addChartInnerTemplate;
			this._placeEmptyDummyChartForMessage = params._placeEmptyDummyChartForMessage;
			this.getColorAndBarColorFromLinkType = params.getColorAndBarColorFromLinkType

			//  Do not store what is returned from function _get_hash_json_Contents since it can change
			_get_hash_json_Contents = params.get_hash_json_Contents; // function

			this.addClickAndOnChangeHandlers();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	};



	/**
	 * Add Click and onChange handlers 
	 */
	this.addClickAndOnChangeHandlers = function() {
		var objectThis = this;

	};


	///////////////////////////////////////////

	///////////////////////////////////////////

	/////////   


	/**
	 * Clear data for 
	 */
	this.clearChart = function() {

		_chart_isLoaded = _IS_LOADED_NO;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
	};


	/**
	 * If not currently loaded, load
	 */
	this.loadChartIfNeeded = function() {

		if ( _chart_isLoaded === _IS_LOADED_NO ) {
			this.load_M_Over_Z_For_PSMs_Histogram();
		}
	};

	var _activeAjax = null;

	/**
	 * Load the data for  M/Z for PSMs Histogram
	 */
	this.load_M_Over_Z_For_PSMs_Histogram = function() {
		var objectThis = this;

		_chart_isLoaded = _IS_LOADED_LOADING;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}
		$PSM_M_Over_Z_CountsBlock.empty();
		
		var hash_json_Contents = _get_hash_json_Contents();

		var selectedLinkTypes = hash_json_Contents.linkTypes;

		if ( ! _anySearchesHaveScanDataYes ) {

			// Show cells for selected link types
			selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
				var selectedLinkType = currentArrayValue;

				//  Add empty chart with Loading message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
//					linkType : selectedLinkType, 
					messageWhole:  _DUMMY_CHART_STATUS_WHOLE_TEXT_SCANS_NOT_UPLOADED
				} );
			}, this /* passed to function as this */ );

			_chart_isLoaded = _IS_LOADED_YES;

			//  Exit since no data to display

			return;  //  EARLY EXIT
		}

		// Add cells for selected link types
		selectedLinkTypes.forEach( function ( currentArrayValue, index, array ) {
			var selectedLinkType = currentArrayValue;

			//  Add empty chart with Loading message
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { linkType : selectedLinkType, $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );

			//  Add empty chart with Loading message
			this._placeEmptyDummyChartForMessage( { 
				$chart_outer_container_jq : $chart_outer_container_jq, 
				//				linkType : selectedLinkType, 
				messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_LOADING,
				messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_LOADING
			} );

		}, this /* passed to function as this */ );

		var hash_json_field_Contents_JSONString = JSON.stringify( hash_json_Contents );
		var ajaxRequestData = {
				project_search_id : _project_search_ids,
				filterCriteria : hash_json_field_Contents_JSONString
		};

		if ( _activeAjax ) {
			_activeAjax.abort();
			_activeAjax = null;
		}
		//  Set to returned jQuery XMLHttpRequest (jqXHR) object
		_activeAjax =
			$.ajax({
				url : contextPathJSVar + "/services/qc/dataPage/mzForPSMsHistogramCounts_Merged",
				traditional: true,  //  Force traditional serialization of the data sent
				//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
				//   So project_search_ids array is passed as "project_search_ids=<value>" which is what Jersey expects
				data : ajaxRequestData,  // The data sent as params on the URL
				dataType : "json",
				success : function( ajaxResponseData ) {
					try {
						_activeAjax = null;
						var responseParams = {
								ajaxResponseData : ajaxResponseData, 
								ajaxRequestData : ajaxRequestData
//								,
//								topTRelement : topTRelement
						};
						objectThis.load_M_Over_Z_For_PSMs_HistogramResponse( responseParams );
//						$topTRelement.data( _DATA_LOADED_DATA_KEY, true );
					} catch( e ) {
						reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
						throw e;
					}
				},
				failure: function(errMsg) {
					_activeAjax = null;
					handleAJAXFailure( errMsg );
				},
				error : function(jqXHR, textStatus, errorThrown) {
					_activeAjax = null;
					if ( objectThis._passAJAXErrorTo_handleAJAXError(jqXHR, textStatus, errorThrown) ) {
						handleAJAXError(jqXHR, textStatus, errorThrown);
					}
				}
			});
	};

	/**
	 * Process AJAX Response
	 */
	this.load_M_Over_Z_For_PSMs_HistogramResponse = function( params ) {
		var ajaxResponseData = params.ajaxResponseData;
		var ajaxRequestData = params.ajaxRequestData;

		var preMZ_Chart_For_PSMPeptideCutoffs_Merged_Results = ajaxResponseData.preMZ_Chart_For_PSMPeptideCutoffs_Merged_Results;
		var dataForChartPerLinkTypeList = preMZ_Chart_For_PSMPeptideCutoffs_Merged_Results.dataForChartPerLinkTypeList;

		var $PSM_M_Over_Z_CountsBlock = $("#PSM_M_Over_Z_CountsBlock");
		if ( $PSM_M_Over_Z_CountsBlock.length === 0 ) {
			throw Error( "unable to find HTML element with id 'PSM_M_Over_Z_CountsBlock'" );
		}

		$PSM_M_Over_Z_CountsBlock.empty();

		dataForChartPerLinkTypeList.forEach( function ( currentArrayValue, indexForLinkType, array ) {
			var entryForLinkType = currentArrayValue;
			var linkType = entryForLinkType.linkType;
			var dataFound = entryForLinkType.dataFound;

			if ( ! dataFound ) {
				//  No data for this link type

				//  Add empty chart with No Data message
				var $chart_outer_container_jq =
					this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
				this._placeEmptyDummyChartForMessage( { 
					$chart_outer_container_jq : $chart_outer_container_jq, 
					linkType : linkType, 
					messagePrefix:  _DUMMY_CHART_STATUS_TEXT_PREFIX_NO_DATA,
					messageSuffix:  _DUMMY_CHART_STATUS_TEXT_SUFFIX_NO_DATA
				} );

				return;  //  EARLY exit for this array element
			}
			var $chart_outer_container_jq =
				this._addChartOuterTemplate( { $chart_group_container_table_jq : $PSM_M_Over_Z_CountsBlock } );
			var $chart_container_jq = this._addChartInnerTemplate( { $chart_outer_container_jq : $chart_outer_container_jq } );

			var colorAndbarColor = this.getColorAndBarColorFromLinkType( linkType );

			this._add_M_Over_Z_For_PSMs_Chart( { entryForLinkType: entryForLinkType, colorAndbarColor: colorAndbarColor, $chartContainer : $chart_container_jq } );

			//  Download Data Setup
			
			var hash_json_Contents = _get_hash_json_Contents();
			//  Set link types to chart link type
			hash_json_Contents.linkTypes = [ linkType ];

			var downloadSummaryDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;
				
				qcChartDownloadHelp._downloadBoxplotChartSummaryData( { filenamePartChartName : "qc-m-over-z-summary-" , entryForLinkType : entryForLinkType, _project_search_ids : _project_search_ids } );
			};
			
			var downloadDataCallback = function( params ) {
//				var clickedThis = params.clickedThis;

				//  Download the data for params
				qc_pages_Single_Merged_Common.submitDownloadForParams( { downloadStrutsAction : _downloadStrutsAction, project_search_ids : _project_search_ids, hash_json_Contents : hash_json_Contents } );
			};
			
			//  Get Help tooltip HTML
			var elementId = "psm_level_block_help_tooltip_m_over_z_statistics_" + linkType
			var $psm_level_block_help_tooltip_m_over_z_statistics_LinkType = $("#" + elementId );
			if ( $psm_level_block_help_tooltip_m_over_z_statistics_LinkType.length === 0 ) {
				throw Error( "No element found with id '" + elementId + "' " );
			}
			var helpTooltipHTML = $psm_level_block_help_tooltip_m_over_z_statistics_LinkType.html();

			qcChartDownloadHelp.add_DownloadClickHandlers_HelpTooltip( { 
				$chart_outer_container_for_download_jq :  $chart_outer_container_jq, 
				downloadDataCallback : downloadDataCallback,
				downloadSummaryDataCallback : downloadSummaryDataCallback,
				helpTooltipHTML : helpTooltipHTML, 
				helpTooltip_Wide : false 
			} );
			
			// Add tooltips for download links
			addToolTips( $chart_outer_container_jq );
		}, this /* passed to function as this */ );

		_chart_isLoaded = _IS_LOADED_YES;
	};

	//  Overridden for Specific elements like Chart Title and X and Y Axis labels
	var _M_Over_Z_For_PSMs_CHART_GLOBALS = {
			_CHART_DEFAULT_FONT_SIZE : 12,  //  Default font size - using to set font size for tick marks.
			_TITLE_FONT_SIZE : 15, // In PX
			_AXIS_LABEL_FONT_SIZE : 14, // In PX
			_TICK_MARK_TEXT_FONT_SIZE : 14, // In PX
	}

	/**
	 * 
	 */
	this._add_M_Over_Z_For_PSMs_Chart = function( params ) {
		var entryForLinkType = params.entryForLinkType;
		var colorAndbarColor = params.colorAndbarColor;
		var $chartContainer = params.$chartContainer;

		var linkType = entryForLinkType.linkType;
		var dataForChartPerSearchIdMap_KeyProjectSearchId = entryForLinkType.dataForChartPerSearchIdMap_KeyProjectSearchId;
		
		//  Get max preMZ_outliers length
		
		var preMZ_outliers_Max_Length = undefined;

		var preMZ_outliers_Min_Length = undefined;

		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, index, array ) {
			
			var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
			
			if ( dataForChartPerSearchIdEntry.preMZ_outliers ) { // preMZ_outliers null if no outliers
			
				if ( preMZ_outliers_Max_Length === undefined 
						|| ( dataForChartPerSearchIdEntry.preMZ_outliers
								&& dataForChartPerSearchIdEntry.preMZ_outliers.length > preMZ_outliers_Max_Length ) ) {
					preMZ_outliers_Max_Length = dataForChartPerSearchIdEntry.preMZ_outliers.length; 
				}

				if ( preMZ_outliers_Min_Length === undefined 
						|| ( dataForChartPerSearchIdEntry.preMZ_outliers
								&& dataForChartPerSearchIdEntry.preMZ_outliers.length < preMZ_outliers_Min_Length ) ) {
					preMZ_outliers_Min_Length = dataForChartPerSearchIdEntry.preMZ_outliers.length; 
				}
			}
		}, this /* passed to function as this */ );
		
		if ( preMZ_outliers_Max_Length === undefined ) {
			// not set so set to 0
			preMZ_outliers_Max_Length = 0;
		}
		if ( preMZ_outliers_Min_Length === undefined ) {
			// not set so set to 0
			preMZ_outliers_Min_Length = 0;
		}

		//  chart data for Google charts
		var chartData = [];

		var chartDataHeaderEntry = [ 
			'SearchId', 
			
			//  Putting style here doesn't work since that styles the lines which are hidden
			
			'Max',
			'Min',
			'First Quartile',
			'Median',
			'Third Quartile',

			//  Putting style here doesn't work since that styles the intervals
			
			{id:'max', type:'number', role:'interval'},
			{id:'min', type:'number', role:'interval'},
			
			{id:'firstQuartile', type:'number', role:'interval'},
			{id:'median', type:'number', role:'interval'},
			{id:'thirdQuartile', type:'number', role:'interval'},
			
			{role: "tooltip", 'p': {'html': true} }, // tooltip for top of top box
			
			{type:'string', role: 'style' } // Color for all of interval parts/entries for current X axis entry
			
			];
				
//		!!!!!!!!!!   Adding a variable number of outliers does not work when putting null for missing outliers for a given search id
		
		//   Add header entries for max number of outliers found across all link types
		for ( var counter = 0; counter < preMZ_outliers_Max_Length; counter++ ) {
			chartDataHeaderEntry.push( 'Outlier Point' );
			chartDataHeaderEntry.push( {type:'string', role: 'style' } );
		}
				
		chartData.push( chartDataHeaderEntry );
		
		var _CHART_SIGNIFICANT_DIGITS = 6;

		_project_search_ids.forEach( function ( _project_search_ids_ArrayValue, indexForProjectSearchId, array ) {
			
			var dataForChartPerSearchIdEntry = dataForChartPerSearchIdMap_KeyProjectSearchId[ _project_search_ids_ArrayValue ];
			
			var searchId = dataForChartPerSearchIdEntry.searchId;
			
			var chartIntervalMaxString = dataForChartPerSearchIdEntry.chartIntervalMax.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var thirdQuartileString = dataForChartPerSearchIdEntry.thirdQuartile.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var medianString = dataForChartPerSearchIdEntry.median.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var firstQuartileString = dataForChartPerSearchIdEntry.firstQuartile.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			var chartIntervalMinString = dataForChartPerSearchIdEntry.chartIntervalMin.toPrecision( _CHART_SIGNIFICANT_DIGITS );
			

			var mainBoxPlotTooltip =
					"Search Id: " + searchId + "\n\n" +
					"Max: " + chartIntervalMaxString + "\n" +
					"Third Quartile: " + thirdQuartileString + "\n" +
					"Median: " + medianString + "\n" +
					"First Quartile: " + firstQuartileString + "\n" +
					"Min: " + chartIntervalMinString + "\n"
					;
			
			var colorForSearchEntry = _colorsPerSearch[ indexForProjectSearchId ];

			var chartEntry = [ 
				searchId.toString(),
				//  First list for charting for tool tips
				dataForChartPerSearchIdEntry.chartIntervalMax,
				dataForChartPerSearchIdEntry.chartIntervalMin,
				dataForChartPerSearchIdEntry.firstQuartile,
				dataForChartPerSearchIdEntry.median,
				dataForChartPerSearchIdEntry.thirdQuartile,
				
				//  Next list for Box Chart
				dataForChartPerSearchIdEntry.chartIntervalMax,
				dataForChartPerSearchIdEntry.chartIntervalMin,
				dataForChartPerSearchIdEntry.firstQuartile,
				dataForChartPerSearchIdEntry.median,
				dataForChartPerSearchIdEntry.thirdQuartile,

				mainBoxPlotTooltip, // tooltip for top of top box
				
				'color: ' + colorForSearchEntry + ';'  // style required to make visible :  color: blue; opacity: 1;
			
				];
						
			if ( dataForChartPerSearchIdEntry.preMZ_outliers ) {
				//  preMZ_outliers is not null

				//  Add each outlier
				dataForChartPerSearchIdEntry.preMZ_outliers.forEach( function ( currentArrayValue, indexForSearchId, array ) {
					chartEntry.push( currentArrayValue );
					chartEntry.push( 'point { visible: true; size: 2; color: ' + colorForSearchEntry + ' }' ); // style required to make visible :  color: blue; opacity: 1; 
				}, this /* passed to function as this */ );

				//  Add the last outlier point for each search to the max length of outlier.  Done so this X-axis entry has the same number of Y-axis entries as for Max Outliers X-axis entry
				var preMZ_outliers_lastEntry = dataForChartPerSearchIdEntry.preMZ_outliers[ dataForChartPerSearchIdEntry.preMZ_outliers.length - 1 ];
				for ( var counter = dataForChartPerSearchIdEntry.preMZ_outliers.length; counter < preMZ_outliers_Max_Length; counter++ ) {
					chartEntry.push( preMZ_outliers_lastEntry );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}
			
			} else {
				//  No outliers so add invisible point at the chartIntervalMax position
				for ( var counter = 0; counter < preMZ_outliers_Max_Length; counter++ ) {
					chartEntry.push( dataForChartPerSearchIdEntry.chartIntervalMax );
					chartEntry.push( 'point { visible: false; size: 0; }' ); // style as hidden, size zero since not an actual valid point 
				}
			}
			
			chartData.push( chartEntry );

		}, this /* passed to function as this */ );
		
		var chartTitle = 'Distribution of Precursor m/z (' + linkType + ")";
		var optionsFullsize = {
				//  Overridden for Specific elements like Chart Title and X and Y Axis labels
				fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._CHART_DEFAULT_FONT_SIZE,  //  Default font size - using to set font size for tick marks.

				title: chartTitle, // Title above chart
				titleTextStyle: {
					color : _PROXL_DEFAULT_FONT_COLOR, //  Set default font color
//					color: <string>,    // any HTML string color ('red', '#cc00cc')
//					fontName: <string>, // i.e. 'Times New Roman'
					fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._TITLE_FONT_SIZE, // 12, 18 whatever you want (don't specify px)
//					bold: <boolean>,    // true or false
//					italic: <boolean>   // true of false
				},
				legend: {position: 'none'},
				hAxis: {
					title: 'Search Number'
						, titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
//					gridlines: {color: '#fff'}
				},
				vAxis: 
				{ 	title: 'M/Z'
					, titleTextStyle: { color: 'black', fontSize: _M_Over_Z_For_PSMs_CHART_GLOBALS._AXIS_LABEL_FONT_SIZE }
				},
				
				lineWidth: 0,  //  Hide lines
				
				interpolateNulls: true,   //  Supposed to continue a line when there is no value for x-axis point.  Doesn't appear to work when using for outliers
				
//				series: [{'color': '#D3362D'}],
				//  Series overrides colors when there are enough entries to cover the interval entries
//				series: [{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'},{'color': '#00FF00'}],
//				colors: [ '#0000FF' ],
				
				intervals: {
					barWidth: 1,
					boxWidth: 1,
					lineWidth: 2,
					style: 'boxes'
				},
				interval: {
					max: {
						style: 'bars',
						barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
						fillOpacity: 1,
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
					},
					min: {
						style: 'bars',
						barWidth: 0.75, // length of horizontal bars, as a fraction of total width ( '1' for same width as boxes )
						fillOpacity: 1
//						,
//						color: '#777'  //  Removed since overridden on a per search basis
					}
				}
		};
        
		// create the chart
		var data = google.visualization.arrayToDataTable( chartData );
		var chartFullsize = new google.visualization.LineChart( $chartContainer[0] );

		//  Register for chart errors
		var errorDrawingChart = function( err ) {
			//  Properties of err object
//			id [Required] - The ID of the DOM element containing the chart, or an error message displayed instead of the chart if it cannot be rendered.
//			message [Required] - A short message string describing the error.
//			detailedMessage [Optional] - A detailed explanation of the error.
//			options [Optional]- An object containing custom parameters appropriate to this error and chart type.
			
			//  This thrown string is displayed on the chart on the page as well as logged to browser console and logged to the server 
			throw Error("Chart Error: " + err.message + " :: detailed error msg: " + err.detailedMessage ); 
		}
		google.visualization.events.addListener(chartFullsize, 'error', errorDrawingChart);

		chartFullsize.draw(data, optionsFullsize);
		
	};

	/**
	 * 
	 */
	this._get_M_Over_Z_For_PSMs_Histogram_ChartTickMarks = function( params ) {
		var maxValue = params.maxValue;
		if ( maxValue < 5 ) {
			var tickMarks = [ 0 ];
			for ( var counter = 1; counter <= maxValue; counter++ ) {
				tickMarks.push( counter );
			}
			return tickMarks;
		}
		return undefined; //  Use defaults
	};


};

/**
 * page variable 
 */

var qcMergedPageChart_M_Over_Z_Statistics_PSM = new QCMergedPageChart_M_Over_Z_Statistics_PSM();
