/**
 * qcChart_Download_Help_HTMLBlock.js
 * 
 * Javascript for the qcChart_Download_Help_HTMLBlock.jsp page fragment
 * 
 * page variable chartDownload
 * 
 * !!!!   Page Requirements:
 * 
 * The element containing the include of qcChart_Download_Help_HTMLBlock.jsp 
 *   has to have class "chart_outer_container_for_download_jq".
 * 
 * The 
 * 
 */

//  JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////
$(document).ready(function() { 
	qcChartDownloadHelp.init();

} ); // end $(document).ready(function() 


/**
 * Constructor 
 */
var QC_ChartDownloadHelp = function() {

	/**
	 * Init page on load 
	 */
	this.init = function() {
		var objectThis = this;
	};

	/**
	 * params: { $chart_outer_container_for_download_jq : element with ".chart_outer_container_for_download_jq"
	 * 			 helpTooltipHTML : String with text/HTML for tooltip for help icon on chart
	 * 
	 * If the element $chart_outer_container_for_download_jq was dynamically added, need to run to add tool tips on download links: 
	 *			addToolTips( $chartOuterContainer );
	 *
	 * addDownloadClickHandlers
	 */
	this.add_DownloadClickHandlers_HelpTooltip = function( params ) {
		var objectThis = this;
		var $chart_outer_container_for_download_jq = params.$chart_outer_container_for_download_jq;
		var helpTooltipHTML = params.helpTooltipHTML;
		var helpTooltip_Wide = params.helpTooltip_Wide;
		
		var $chart_download_link_jq_All = $chart_outer_container_for_download_jq.find(".chart_download_link_jq");
		$chart_download_link_jq_All.click( function( event ) { 
			objectThis._downloadChart( { clickedThis : this } ); 
			event.preventDefault();
			event.stopPropagation();
		});
		
		//  Eat any clicks that occur on these elements or their children
		
		var $svg_download_block_jq = $chart_outer_container_for_download_jq.find(".svg_download_block_jq");
		$svg_download_block_jq.click( function( event ) {  
			event.preventDefault();
			event.stopPropagation();
		});

		var $svg_download_backing_block_jq = $chart_outer_container_for_download_jq.find(".svg_download_backing_block_jq");
		$svg_download_backing_block_jq.click( function( event ) {  
			event.preventDefault();
			event.stopPropagation();
		});
				
		//  Add tooltip to ? with circle located upper right corner of chart
		
		var helpTooltipClasses = " help-for-qc-chart-tooltip ";
		
		if ( helpTooltip_Wide ) {
			helpTooltipClasses += " help-for-qc-chart-tooltip-wide ";
		}
		
		var $help_image_for_qc_chart_jq = $chart_outer_container_for_download_jq.find(".help_image_for_qc_chart_jq");

		$help_image_for_qc_chart_jq.qtip( {
	        content: {
	            text: helpTooltipHTML
	        },
	        style : {
	        	def : false,  // Do not add class 'qtip-default'.  Class 'qtip' is still added, which contains font-size
	        	classes : helpTooltipClasses //  Add this/these class to the tooltip
	        },
			position: {
				target: 'mouse',
				adjust: { x: 5, y: 5 }, // Offset it slightly from under the mouse
	            viewport: $(window)
	         }
	    });		
		
	};

	/**
	 * 
	 */
	this._downloadChart = function( params ) {
		try {
			var clickedThis = params.clickedThis;

			var $clickedThis = $( clickedThis );
			var download_type = $clickedThis.attr("data-download_type");
			var $chart_outer_container_for_download_jq = $clickedThis.closest(".chart_outer_container_for_download_jq");

			var getSVGContentsAsStringResult = this._getSVGContentsAsString( $chart_outer_container_for_download_jq );
			
			if ( getSVGContentsAsStringResult.errorException ) {
				throw errorException;
			}
			
			var fullSVG_String = getSVGContentsAsStringResult.fullSVG_String;
			
			var form = document.createElement( "form" );
			$( form ).hide();
			form.setAttribute( "method", "post" );
			form.setAttribute( "action", contextPathJSVar + "/convertAndDownloadSVG.do" );

			var svgStringField = document.createElement( "input" );
			svgStringField.setAttribute("name", "svgString");
			svgStringField.setAttribute("value", fullSVG_String );
			var fileTypeField = document.createElement( "input" );
			fileTypeField.setAttribute("name", "fileType");
			fileTypeField.setAttribute("value", download_type);
			form.appendChild( svgStringField );
			form.appendChild( fileTypeField );
			document.body.appendChild(form);    // Not entirely sure if this is necessary			
			form.submit();
			document.body.removeChild( form );
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
		
	};
	

	/**
	 * 
	 */
	this._getSVGContentsAsString = function ( $chart_outer_container_for_download_jq ) {
		try {
			var $svgRoot = $chart_outer_container_for_download_jq.find("svg");
			if ( $svgRoot.length === 0 ) {
				// No <svg> element found
				return { noPageElement : true };
			}

			var svgContents = $svgRoot.html();
			var fullSVG_String = "<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";
			fullSVG_String += "<svg id=\"svg\" ";
			fullSVG_String += "width=\"" + $svgRoot.attr( "width" ) + "\" ";
			fullSVG_String += "height=\"" + $svgRoot.attr( "height" ) + "\" ";
			fullSVG_String += "xmlns=\"http://www.w3.org/2000/svg\">" + svgContents + "</svg>";
			// fix the URL that google charts is putting into the SVG. Breaks parsing.
			fullSVG_String = fullSVG_String.replace( /url\(.+\#_ABSTRACT_RENDERER_ID_(\d+)\)/g, "url(#_ABSTRACT_RENDERER_ID_$1)" );	

			return { fullSVG_String : fullSVG_String};
		} catch( e ) {
			//  Not all browsers have svgElement.innerHTML which .html() tries to use, causing an exception
			return { errorException : e };
		}
	};

};

var qcChartDownloadHelp = new QC_ChartDownloadHelp();