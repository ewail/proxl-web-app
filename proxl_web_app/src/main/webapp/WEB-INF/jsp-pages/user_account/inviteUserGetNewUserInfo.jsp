<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  inviteUserGetNewUserInfo.jsp 

 /WEB-INF/jsp-pages/user_account/inviteUserGetNewUserInfo.jsp 
--%>



<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB</title>
 
	 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	


	<script type="text/javascript" src="static/js_generated_bundles/user_pages/inviteUserGetNewUserInfo-bundle.js?x=${ cacheBustValue }"></script>

<%--  Moved to Front End Build Bundles		
	
			<script type="text/javascript" src="js/user_account/inviteUserGetNewUserInfo.js?x=${cacheBustValue}"></script>
--%>
		
</head>

<body class="create-user-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>


 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>


	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	<%--  !!!!!!!!!!    Overlays               !!!!!!!!!! --%>

	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	
 <c:if test="${ not empty termsOfServiceTextVersion }">	
 
  <%--  Terms of Service Overlay --%>
		

	<%--  Terms of Service Overlay Background --%>
	
	
	<div id="terms_of_service_modal_dialog_overlay_background" class="terms-of-service-modal-dialog-overlay-background" style="display: none;"  >
	</div>
	
	<%--  Terms of Service Overlay Div --%>
	
	<div style="text-align: center; position: relative;">
	  <div style="text-align: center; position: relative;" >
		<div id="terms_of_service_overlay_div" class=" terms-of-service-overlay-div " style="display: none; "  >
		
			<div id="terms_of_service_overlay_header" class="terms-of-service-overlay-header" style="width:100%; " >
				<h1 id="terms_of_service_overlay_header_text" class="terms-of-service-overlay-header-text" 
					>Terms of Service</h1>
			</div>
			<div id="terms_of_service_overlay_body" class="terms-of-service-overlay-body" >
				<div style="margin-bottom: 10px;">
					Terms of Service Acceptance required.
				</div>
				
				<div id="terms_of_service_acceptance_required_text" class=" terms-of-service-overlay-text ">
					<c:out value="${ termsOfServiceTextVersion.termsOfServiceText }" escapeXml="false"></c:out>
					<input type="hidden" value="${ termsOfServiceTextVersion.idString }" id="terms_of_service_id_string">
				</div>
				
				<div style="margin-top: 10px; margin-bottom: 10px;">
					<input id="terms_of_service_acceptance_yes_button" class="submit-button terms-of-service-overlay-button"
						type="button" value="Accept Terms of Service">
		
					<input id="terms_of_service_acceptance_no_button"  class="submit-button terms-of-service-overlay-button"
						type="button" value="Reject Terms of Service">
				</div>
				
			</div> <%--  END  <div id="terms_of_service_overlay_body"  --%>
			
		</div>  <%--  END  <div id="terms_of_service_overlay_div"  --%>
	  </div>
	</div>
	
  </c:if>
  
  	

<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>

  	<div  style="position: relative;" class="page-label">
  		<div class="error-message-container error_message_container_jq" id="error_message_all_fields_required">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >All fields are required
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  	</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_password_confirm_password_not_match">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >Password and Confirm Password must match
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  	</div>
	  	</div>

  		<div class="error-message-container error_message_container_jq" id="error_message_username_taken">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >Username already taken
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
	  		</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_email_taken">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >Email address already taken
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
	  		</div>
	  	</div>
  		<div class="error-message-container error_message_container_jq" id="error_message_username_email_taken">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >Username already taken.  Email address already taken.
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
	  		</div>
	  	</div>

  		<div class="error-message-container error_message_container_jq" id="error_message_from_server">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" id="error_message_from_server_text"></div>
	  		</div>
	  	</div>
	  	
  		<div class="error-message-container error_message_container_jq" id="error_message_system_error">
  			<div class="error-message-inner-container" >
	  			<span class="error-message-text" >System Error
		  			<span class="error-message-close-x error_message_close_x_jq">X</span></span>
		  	</div>
	  	</div>
	  	
<%-- 	  	
	  	UNUSED  
--%>	  		  	
	  	
  		<div class="success-message-container error_message_container_jq" id="success_message_system_success">
  			<div class="success-message-inner-container" >
  				<div class="success-message-close-x error_message_close_x_jq">X</div>
	  			<div class="success-message-text" >Account Created</div>
		  	</div>
	  	</div>
	  		  
  		Create new user
  	</div>
  	
  	<div class="page-text">
		You have been invited to <span class="ProXL-DB-text" >ProXL DB</span>.  Fill out the form below to create an account.
	</div>
  	
  	
	<form action="javascript:createAccountFormSubmit()" >
	
		<input type="hidden" id="code" value="<c:out value="${ param.code }" />"/>
		
		<%--  size of input fields controlled by CSS, was size="20"  --%>

		<input type="text" id="firstName" placeholder="First name" title="First name" class="input-field input_field_jq" maxlength="40"/><br>
		<input type="text" id="lastName" placeholder="Last name" title="Last name" class="input-field input_field_jq" maxlength="60" /><br>
		<input type="text" id="organization" placeholder="Organization" title="Organization" class="input-field input_field_jq" maxlength="2000" /><br>
		<input type="text" id="email" placeholder="Email address" title="Email address" class="input-field input_field_jq" maxlength="255" /><br>

		<input type="text" id="username" placeholder="Username" title="Username" class="input-field input_field_jq" maxlength="40" /><br>

		<input type="password" id="password" placeholder="Password" title="Password" class="input-field input_field_jq" maxlength="40" /><br>
		<input type="password" id="passwordConfirm" placeholder="Confirm Password" title="Confirm Password" class="input-field input_field_jq" maxlength="40" /><br>
		
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Create Account" id="create_account_button">
	</form>

	
  </div> <%-- end <div class="page-content" >	 --%>

  <div class="bottom-tab">
  
  <%-- 
  	<c:url var="loginURL" value="user_loginPage.do" >
  		<c:param name="requestedURL">${ intialIncomingURL }</c:param>
  	
  	</c:url>
  
		<a href="${ loginURL }" >Sign In</a>
--%>		
		
		<a href='javascript:$("#signin_form").submit()' >Sign In</a>

  </div>
  
  
 </div>
</div>

  	
	<%-- submitted by the "Sign In" link above --%>
	<form action="user_loginPage.do" id="signin_form" >
		<input type="hidden" name="requestedURL" value="user_inviteProcessCode.do?code=<c:out value="${ param.code }" />">
	</form>
			  	
	<%-- submitted by the javascript if the account is successfully created --%>
	<form action="listProjects.do" id="list_projects_form" >
	</form>



<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
