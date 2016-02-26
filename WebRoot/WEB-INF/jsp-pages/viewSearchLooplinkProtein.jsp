<%@page import="org.yeastrc.xlink.www.webapp_timing.WebappTiming"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
 
 		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
		
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="${contextPath}/js/libs/jquery-ui-1.10.4.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="${contextPath}/js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="${contextPath}/js/lorikeet/jquery.flot.js"></script>
		<script src="${contextPath}/js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="${contextPath}/js/lorikeet/specview.js"></script>
		<script src="${contextPath}/js/lorikeet/peptide.js"></script>
		<script src="${contextPath}/js/lorikeet/aminoacid.js"></script>
		<script src="${contextPath}/js/lorikeet/ion.js"></script>		
		
<%--  End of Lorikeet Core Parts --%>		

		
		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>

		
				
		
		
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
		
		<script type="text/javascript" src="${ contextPath }/js/lorikeetPageProcessing.js"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/nagWhenFormChangedButNotUpdated.js"></script>				
		
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This input is required on this page:
					<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js"></script>
	
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js"></script>
		
			
		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewProteinPageCommonCrosslinkLooplinkCoverageSearchMerged.js"></script>
		
		
		<script type="text/javascript" src="${ contextPath }/js/viewSearchLooplinkProteinPage.js"></script>
		
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		
</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

		<%--  protein name data webservice base URL, used by createTooltipForProteinNames.js --%>
	<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
				
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
				
		<div class="overall-enclosing-block">
			
			
			<h2 style="margin-bottom:5px;">List search proteins:</h2>
	

			<div style="margin-bottom:20px;"> 
				
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="peptide.do" searchId="${ search.id }"
						>peptide.do?searchId=<bean:write name="search" property="id" 
						/>&queryJSON=<c:out value="${ peptidePageQueryJSON }" escapeXml="false" 
						></c:out></proxl:defaultPageUrl>"
						>Peptide View</a>]
						 									
				[<a class="tool_tip_attached_jq" data-tooltip="View protein coverage report" href="${ contextPath }/<proxl:defaultPageUrl pageName="proteinCoverageReport.do" searchId="${ search.id }">proteinCoverageReport.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>Coverage Report</a>]
				
				<%-- Navigation links to Merged Image and Merged Structure --%>
				
				<%@ include file="/WEB-INF/jsp-includes/imageAndStructureNavLinks.jsp" %>

			</div>
				
			<%-- query JSON in field outside of form for input to Javascript --%>
				
			<input type="hidden" id="query_json_field_outside_form" value="<c:out value="${ queryJSONToForm }" ></c:out>" > 

			<%--  A block outside any form for PSM Peptide cutoff JS code --%>
			<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>


	
			<html:form action="looplinkProtein" method="get" styleId="form_get_for_updated_parameters">
				
				<html:hidden property="searchId"/>
				
				<html:hidden property="queryJSON" styleId="query_json_field" />
			
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

			</html:form>
			
						
			<%-- WAS		
			
			
			<html:form action="viewSearchCrosslinkProtein" method="get" styleId="form_get_for_updated_parameters">
			
				<html:hidden property="project_id"/>
				
				<html:hidden property="searchId"/>
			
			
			--%>
			
			
<%--
		Moved JS call to the "Update" button
		 			
			<form action="javascript:viewSearchLooplinkProteinPageCode.updatePageForFormParams()" method="get" > 
			
				--%>	 <%-- id="form_get_for_updated_parameters" --%>
			
			
			
			<table style="border-width:0px;">
			
			
			
				<tr>
					<td valign="top">Search:</td>
					<td>
					
						<%--  Set to false to not show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ false }" />
						
						<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
						<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

					</td>
				</tr>

				<%-- Spacer --%>  
<%--				  
				<tr>
					<td style="height: 6px;"></td>
				</tr>
--%>
				
				<%--  The section at the top of the page with the cutoffs, in the user input section --%>

<%-- 
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inDataEntryForm.jsp" %>
--%>				
				

				<tr>
					<td>Exclude xlinks with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterNonUniquePeptides" > <%-- onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" --%> 					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePSM" > <%--  onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" --%> 					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<input type="checkbox" id="filterOnlyOnePeptide" > <%--  onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" --%> 					
						 	 only one peptide
						 </span></label>
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						
<%-- 						
						 <label style="white-space: nowrap" >
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
						   <bean:write name="taxonomy" property="key"/> 
						  </html:multibox> 
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 
--%>						 
						 <label style="white-space: nowrap" >
						  <input type="checkbox" name="excludeTaxonomy" value="<bean:write name="taxonomy" property="key"/>" class=" excludeTaxonomy_jq "> <%-- onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" --%>  
						  
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 						 
						</logic:iterate>				
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--  shortened property from "excludeProtein" to "excP" to shorten the URL  --%>
						<%-- TODO   TEMP
						<html:select property="excP" multiple="true" styleId="excludeProtein" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
							<html:options collection="proteins" property="nrProtein.nrseqId" labelProperty="name" />
						</html:select>
						--%>
						
						
						<%--  New version:  Commented out since not getting the list of proteins in the action yet 
						
						All <option> values must be parsable as integers:
						--%>
						<select name="excludedProteins" multiple="multiple" id="excludeProtein"> <!-- onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" -->  
						  
	  						<logic:iterate id="protein" name="proteins">
	  						  <option value="<c:out value="${ protein.nrProtein.nrseqId }"></c:out>"><c:out value="${ protein.name }"></c:out></option>
	  						</logic:iterate>
	  					</select>
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>

<%--   WAS 						
						<input type="submit" value="Update" onclick="searchFormUpdateButtonPressed()">
--%>						
						<input type="button" value="Update"  onclick="viewSearchLooplinkProteinPageCode.updatePageForFormParams()" >
														

						<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
							<input type="button" value="Save As Default" id="mergedImageSaveOrUpdateDefaultPageView"
								onclick="saveOrUpdateDefaultPageView( { clickedThis : this, searchId: <bean:write name="search" property="id" /> } )">
						</c:if>
					</td>
				</tr>
			
			</table>
			
			
<%-- 			
			</form>
--%>
			
			
			
			
	
			<h3 style="display:inline;">Looplinks (<bean:write name="numLooplinks" />):</h3>
			<div style="display:inline;">

				[<a class="tool_tip_attached_jq" data-tooltip="View crosslinks (instead of looplinks)" href="${ contextPath }/<proxl:defaultPageUrl pageName="crosslinkProtein.do" searchId="${ search.id }">crosslinkProtein.do?<bean:write name="queryString" /></proxl:defaultPageUrl>"
						>View Crosslinks (<bean:write name="numCrosslinks" />)</a>]

				[<a class="tool_tip_attached_jq" data-tooltip="Download all looplinks as tab-delimited text" href="${ contextPath }/downloadMergedProteins.do?<bean:write name="mergedQueryString" />">Download Data (<bean:write name="numLinks" />)</a>]
				[<a class="tool_tip_attached_jq" data-tooltip="Download all distinct UDRs (crosslinks and looplinks) as tab-delimited text" href="${ contextPath }/downloadMergedProteinUDRs.do?<bean:write name="mergedQueryString" />">Download UDRs (<bean:write name="numDistinctLinks" />)</a>]
			</div>

			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				viewSearchLooplinkProteinPageCode.createPartsAboveMainTable();
				
			</script>
			
			
				<table style="" id="main_page_data_table" class="tablesorter  top_data_table_jq ">
				
					<thead>
					<tr>
						<th data-tooltip="Name of the looplinked protein" class="tool_tip_attached_jq" style="text-align:left;width:10%;font-weight:bold;">Protein</th>
						<th data-tooltip="Beginning position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;1</th>
						<th data-tooltip="Ending position of the looplink" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">Position&nbsp;2</th>
						<th data-tooltip="Number of peptide spectrum matches showing this link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">PSMs</th>
						<th data-tooltip="Number of distinct peptides showing link" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Peptides</th>
						<th data-tooltip="Number of found peptides that uniquely map to this protein from the FASTA file" class="tool_tip_attached_jq integer-number-column-header" style="width:10%;font-weight:bold;">#&nbsp;Unique Peptides</th>


						<c:forEach var="peptideAnnotationDisplayNameDescription" items="${ peptideAnnotationDisplayNameDescriptionList }">

								<%--  Consider displaying the description somewhere   peptideAnnotationDisplayNameDescription.description --%>
							<th data-tooltip="Best Peptide-level <c:out value="${ peptideAnnotationDisplayNameDescription.displayName }"></c:out> for this peptide (or linked pair)" 
									class="tool_tip_attached_jq" 
									style="width:10%;font-weight:bold;">
								<span style="white-space: nowrap">Best Peptide</span>
								<span style="white-space: nowrap"><c:out value="${ peptideAnnotationDisplayNameDescription.displayName }"></c:out></span>
							</th>
							
						</c:forEach>
										

						<c:forEach var="psmAnnotationDisplayNameDescription" items="${ psmAnnotationDisplayNameDescriptionList }">

								<%--  Consider displaying the description somewhere   psmAnnotationDisplayNameDescription.description --%>
						  <th data-tooltip="Best PSM-level <c:out value="${ psmAnnotationDisplayNameDescription.displayName }"></c:out> for PSMs matched to peptides that show this link" class="tool_tip_attached_jq" style="width:10%;font-weight:bold;"
							><span style="white-space: nowrap">Best PSM</span> 
								<span style="white-space: nowrap"><c:out value="${ psmAnnotationDisplayNameDescription.displayName }"></c:out></span></th>

						</c:forEach>
							
					</tr>
					</thead>
						
					<logic:iterate id="looplink" name="looplinks">
							<tr id="<bean:write name="looplink" property="protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="proteinPosition1" />-<bean:write name="looplink" property="protein.nrProtein.nrseqId" />-<bean:write name="looplink" property="proteinPosition2" />"
								style="cursor: pointer; "
								
								
								onclick="viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.showHideLooplinkReportedPeptides( { clickedElement : this })"
								search_id="${ search.id }"
								project_id="${ projectId }"
								peptide_q_value_cutoff="${ peptideQValueCutoff }"
								psm_q_value_cutoff="${ psmQValueCutoff }"
								protein_id="<bean:write name="looplink" property="protein.nrProtein.nrseqId" />"
								protein_position_1="<bean:write name="looplink" property="proteinPosition1" />"
								protein_position_2="<bean:write name="looplink" property="proteinPosition2" />"
							>
								<td><span class="proteinName" id="protein-id-<bean:write name="looplink" property="protein.nrProtein.nrseqId" />"><bean:write name="looplink" property="protein.name" /></span></td>
								<td class="integer-number-column"><bean:write name="looplink" property="proteinPosition1" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="proteinPosition2" /></td>
								<td class="integer-number-column"><bean:write name="looplink" property="numPsms" /></td>
				
								<td class="integer-number-column"><a class="show-child-data-link   " 
										href="javascript:"
										><bean:write name="looplink" property="numPeptides"  
											/><span class="toggle_visibility_expansion_span_jq" 
												><img src="${contextPath}/images/icon-expand-small.png" 
													class=" icon-expand-contract-in-data-table "
													></span><span class="toggle_visibility_contraction_span_jq" 
														style="display: none;" 
														><img src="${contextPath}/images/icon-collapse-small.png"
															class=" icon-expand-contract-in-data-table "
															></span>
									</a>
								</td>
																
								<td class="integer-number-column"><bean:write name="looplink" property="numUniquePeptides" /></td>

								<c:forEach var="annotationValue" items="${ looplink.peptideAnnotationValueList }">
			
									<td style="white-space: nowrap"><c:out  value="${ annotationValue }" /></td>
								</c:forEach>	

								<c:forEach var="annotationValue" items="${ looplink.psmAnnotationValueList }">
			
									<td><c:out  value="${ annotationValue }" /></td>
								</c:forEach>	
							</tr>
							
							<tr class="expand-child" style="display:none;">

									<%--  Adjust colspan for number of columns in current table --%>
									
								<%--  Add to value for length of Peptide and PSM value lists --%>
								<c:set var="columnsAddedForAnnotationData" 
									value="${ fn:length( looplink.peptideAnnotationValueList ) + fn:length( looplink.psmAnnotationValueList ) }" />
														
								<td colspan="${ 7 + columnsAddedForAnnotationData }" align="center" class=" child_data_container_jq ">
															
									<div style="color: green; font-size: 16px; padding-top: 10px; padding-bottom: 10px;" >
										Loading...
									</div>
								</td>
							</tr>	

					</logic:iterate>
				</table>
	


			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	

			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
		
		</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>


<%

WebappTiming webappTiming = (WebappTiming)request.getAttribute( "webappTiming" );

if ( webappTiming != null ) {
		
	webappTiming.markPoint( "At end of JSP" );
	
	webappTiming.logTiming();
}



%>