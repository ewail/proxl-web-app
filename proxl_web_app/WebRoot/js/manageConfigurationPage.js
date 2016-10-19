
//   manageConfigurationPage.js



//  Javascript for the project admin section of the page manageUsersPage.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




/////////////////

var getListConfiguration = function() {

	var requestData = {

	};

	var _URL = contextPathJSVar + "/services/config/list";

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {

				getListConfigurationResponse(requestData, data);
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});

};


///////

var getListConfigurationResponse = function(requestData, responseData) {

	var configList = responseData.configList;
	
	//  Process text inputs
	
	var $config_text_inputs_jq = $(".config_text_inputs_jq");
	
	$config_text_inputs_jq.each( function( index, element ) {
		
		var $configTextInput = $( this );
		
		var configKeyForInput = $configTextInput.attr("data-config-key");
		
//		var foundConfigValueForField = false;

		for ( var configListIndex = 0; configListIndex < configList.length; configListIndex++ ) {

			var configListItem = configList[ configListIndex ];

			if ( configListItem.configKey === configKeyForInput ) {

				$configTextInput.val( configListItem.configValue );
				
//				foundConfigValueForField = true;
			}
		}
	} );
	
	//  Process checkbox inputs

	var $config_checkbox_inputs_jq = $(".config_checkbox_inputs_jq");
	
	$config_checkbox_inputs_jq.each( function( index, element ) {
		
		var $configCheckboxInput = $( this );
		
		var configKeyForInput = $configCheckboxInput.attr("data-config-key");
		
//		var foundConfigValueForField = false;

		for ( var configListIndex = 0; configListIndex < configList.length; configListIndex++ ) {

			var configListItem = configList[ configListIndex ];

			if ( configListItem.configKey === configKeyForInput ) {

				var dataValueChecked = $configCheckboxInput.attr("data-value-checked");
//				var dataValueNOTChecked = $configCheckboxInput.attr("data-value-not-checked");
				
				if ( configListItem.configValue === dataValueChecked ) {
					
					$configCheckboxInput.prop( "checked", true );
					
				} else {
					
					$configCheckboxInput.prop( "checked", false );
				}
				
//				foundConfigValueForField = true;
			}
		}
	} );
};


function saveListConfiguration() {


	var configList = [];
	
	
	var input_footer_center_of_page_html_Val = null;
	

	//  Process text inputs
	
	var $config_text_inputs_jq = $(".config_text_inputs_jq");
	
	$config_text_inputs_jq.each( function( index, element ) {
		
		var $configTextInput = $( this );
		
		var configKeyForInput = $configTextInput.attr("data-config-key");

		var valueInInput = $configTextInput.val( );

		var configListItem = { 
				configKey: configKeyForInput,
				configValue : valueInInput };
		configList.push( configListItem );
		
		//  save special data for data-FOOTER_CENTER_OF_PAGE_HTML
		
		var data_FOOTER_CENTER_OF_PAGE_HTML_val = $configTextInput.attr("data-FOOTER_CENTER_OF_PAGE_HTML");
		
		if ( data_FOOTER_CENTER_OF_PAGE_HTML_val === "true" ) {
			
			input_footer_center_of_page_html_Val = valueInInput;
		}
	} );
	
	//  Process checkbox inputs

	var $config_checkbox_inputs_jq = $(".config_checkbox_inputs_jq");
	
	$config_checkbox_inputs_jq.each( function( index, element ) {
		
		var $configCheckboxInput = $( this );
		
		var configKeyForInput = $configCheckboxInput.attr("data-config-key");
		
		var dataValueChecked = $configCheckboxInput.attr("data-value-checked");
		var dataValueNOTChecked = $configCheckboxInput.attr("data-value-not-checked");

		var valueToSaveToConfig = dataValueNOTChecked;
		
		if ( $configCheckboxInput.prop( "checked" ) ) {
			
			valueToSaveToConfig = dataValueChecked;
		}

		var configListItem = { 
				configKey: configKeyForInput,
				configValue : valueToSaveToConfig };
		configList.push( configListItem );

	} );


	var requestObj = { configList : configList };
	
	var requestData = JSON.stringify( requestObj );

	var _URL = contextPathJSVar + "/services/config/save";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
	    contentType: "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {

			try {

				saveListConfigurationResponse( { 
					requestData : requestData, 
					responseData : data, 
					input_footer_center_of_page_html_Val : input_footer_center_of_page_html_Val
				} );
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
	
}


function saveListConfigurationResponse( params ) {

//	var requestData = params.requestData;
//	var responseData = params.responseData;
	var input_footer_center_of_page_html_Val = params.input_footer_center_of_page_html_Val;
	
	
	var $element = $("#success_message_values_updated");
	
	showErrorMsg( $element );  //  Used for success messages as well
	
	if ( input_footer_center_of_page_html_Val !== null ) {
	
		//  Update footer text on current page

		var $footer_center_container = $("#footer_center_container");

		$footer_center_container.html( input_footer_center_of_page_html_Val );
	}
}


////////////////////////////////

//////   Term of Service

function openTermsOfServiceOverlay() {
	
	
	$("#terms_of_service_modal_dialog_overlay_background").show();
	$("#terms_of_service_overlay_div").show();
}

function closeTermsOfServiceOverlay() {
	
	$("#terms_of_service_modal_dialog_overlay_background").hide();
	$("#terms_of_service_overlay_div").hide();
}



/////////////////

var getTermsOfServiceData = function() {

	var requestData = {

	};

	var _URL = contextPathJSVar + "/services/config/termsOfServiceData";

//	var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {

				getTermsOfServiceDataResponse(requestData, data);
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});

};


///////

var getTermsOfServiceDataResponse = function(requestData, responseData) {

	var termsOfServiceEnabled = responseData.termsOfServiceEnabled;
	var termsOfServiceText = responseData.termsOfServiceText;
	
	if ( termsOfServiceText === undefined || termsOfServiceText === null || termsOfServiceText === "" ) {
		
		$(".change_tos_parts_jq").hide();
		$(".add_tos_parts_jq").show();
		
		$("#tos_not_exist").show();
		$("#tos_enabled").hide();
		
	} else {
		
		$(".add_tos_parts_jq").hide();
		$(".change_tos_parts_jq").show();
		
		$("#tos_not_exist").hide();
		
		if ( termsOfServiceEnabled ) {

			$("#tos_not_enabled").hide();
			$("#tos_enabled").show();
		} else {
			
			$("#tos_enabled").hide();
			$("#tos_not_enabled").show();
			
		}
		
	}

	$("#terms_of_service_user_text").val( termsOfServiceText );
};



function addChangeTermsOfServiceData( params ) {
	
	var add = params.add;
	var change = params.change;
	
	var $terms_of_service_user_text = $("#terms_of_service_user_text");
	
	var termsOfServiceText = $terms_of_service_user_text.val();

	var requestData = { termsOfServiceText : termsOfServiceText };
	
	var _URL = contextPathJSVar + "/services/config/addTermsOfService";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {

				addTermsOfServiceDataResponse( { 
					requestData : requestData, 
					responseData : data,
					add : add,
					change : change
				} );
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
	
}


function addTermsOfServiceDataResponse( params ) {

	var responseData = params.responseData;
	var add = params.add;
//	var change = params.change;
	
	if ( responseData.status ) {

		if ( add ) {

			$(".add_tos_parts_jq").hide();
			$(".change_tos_parts_jq").show();
			
			$("#tos_not_exist").hide();
			
			$("#tos_not_enabled").hide();
			$("#tos_enabled").show();
			

			alert( "added" );

		} else {

			alert( "changed" );
		}

		closeTermsOfServiceOverlay();

		
	} else {

		if ( add ) {

			alert( "add failed" );

		} else {

			alert( "change failed" );
		}
		
	}
}






function disableTermsOfService( ) {
	
	var requestData = { };
	
	var _URL = contextPathJSVar + "/services/config/disableTermsOfService";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				disableTermsOfServiceResponse( { 
					requestData : requestData, 
					responseData : data
				} );
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
	
}


function disableTermsOfServiceResponse( params ) {

	var responseData = params.responseData;
	
	if ( responseData.status ) {
		
		$("#tos_enabled").hide();
		$("#tos_not_enabled").show();

		alert( "disabled" );
		
	} else {
			
		alert( "failed to disable" );
	}
}


function enableTermsOfService( ) {
	
	var requestData = { };
	
	var _URL = contextPathJSVar + "/services/config/enableTermsOfService";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			try {
				enableTermsOfServiceResponse( { 
					requestData : requestData, 
					responseData : data
				} );
				
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

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
	
}


function enableTermsOfServiceResponse( params ) {

	var responseData = params.responseData;
	
	if ( responseData.status ) {
		
		$("#tos_not_enabled").hide();
		$("#tos_enabled").show();

		alert( "enabled" );
		
	} else {
			
		alert( "failed to enable" );
	}
}






/////////////////////////////////////

function initPage() {


	$("#save_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			saveListConfiguration();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	$("#reset_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			getListConfiguration();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	$("#terms_of_service_overlay_X_for_exit_overlay").click(function(eventObject) {
		try {

//			var clickThis = this;

			closeTermsOfServiceOverlay();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});

	$("#tos_add_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			openTermsOfServiceOverlay();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});

	$("#tos_change_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			openTermsOfServiceOverlay();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});

	$("#tos_disable_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			disableTermsOfService();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});

	$("#tos_enable_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			enableTermsOfService();

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}

	});


	$("#terms_of_service_overlay_add_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			addChangeTermsOfServiceData( { add : true } );

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});

	$("#terms_of_service_overlay_change_button").click(function(eventObject) {
		try {

//			var clickThis = this;

			addChangeTermsOfServiceData( { change : true } );

			return false;

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	});


	getListConfiguration();
	getTermsOfServiceData();

};


///////////////

$(document).ready(function() {

	try {

		initPage();
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
