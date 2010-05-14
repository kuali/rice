/**
 * The pdf plugin
 */
function PdfPlugin() {
	
	var VERSION = "1.0";
	var GET_DATA_COMMAND = "GetDataFields";
	var SET_DATA_COMMAND = "SetDataFields";
	var TOGGLE_RO_COMMAND = "ToggleReadOnly";
	var ERROR_MESSAGE = 'There seems to be a problem with the version of the Adobe Reader plugin that you are using to access this workflow document. For more information on how to install or update your copy of Adobe Reader please go to: <a href="https://confluence.cornell.edu/x/HIt3Bg">https://confluence.cornell.edu/x/HIt3Bg</a>. A full list of valid browsers and Adobe plugin versions are available on this page along with set up instructions. <br /> We apologize for the inconvenience.<br /><br />';
	
	// plugin dependecies 
	var dependecies = ["jquery.browser.min.js", "pdfobject.js"];
	var pdf;
	var ria = null;
	var actionStack = [];
	var that = this;
	
	// callbacks called from pdf
	var messageHandler = {
		onMessage: function(aMessage) {
			var lastAction = actionStack.pop();
			switch(lastAction) {
				case "getData":
					that.ria.onGetData(aMessage[0]);
					break;
				case "setData":
					that.ria.onSetData(aMessage[0]);
					break;
				default:
					throw "No pending call found";
			}	   	
		},
        onError: function(error, aMessage) { 
        	that.ria.onError(error.message);
      	}
  	};

	// entry point for the plugin
	this.init = function(ria) {
		that.ria = ria;
		// load the dependencies
		ria.loadScripts(dependecies, true, onDependeciesLoad);
	};
		
	this.postMessage = function(messageArray) {
    	that.pdf.postMessage(messageArray);
    }

    this.getData = function() {
    	actionStack.push("getData");
    	that.pdf.postMessage([VERSION, GET_DATA_COMMAND]);
    }

    this.setData = function(data) {
		actionStack.push("setData");
        that.pdf.postMessage([VERSION, SET_DATA_COMMAND, data]);
	}
	
	this.action = function(action) {
		actionStack.push("action");
	}
	
	// initialize plugin after deps are loaded
	var onDependeciesLoad = function() {
		
		if (!pipwerks.pdfUTILS.detect.hasReader() || checkPluginVersion() < 9) {
			that.ria.onError(ERROR_MESSAGE + createDiagnosticMessage());
		}
		else {
			var pdfElement = that.ria.riaElement.get(0);
		   	var agentTest = /WebKit/;
	       	var mimeType = "application/pdf";
	       	
	        if (agentTest.test(navigator.userAgent)) {
	            mimeType = "application/vnd.adobe.pdf";
	        }
	        
	        // http://support.adobe.com/devsup/devsup.nsf/docs/51866.htm
	        if (typeof hideToolbar == "undefined") {
	            hideToolbar = true;
	        }
	        
	        pdfElement.innerHTML = '<object class="pdf-object" id="pdf" type="' + mimeType + '" data="' + that.ria.url + '#view=Fit&pagemode=none&statusbar=0&navpanes=0&messages=0' + (hideToolbar ? '&toolbar=0' : '') + '"></object>';
	        
	        that.pdf = jQuery('#pdf').get(0);
	        that.pdf.messageHandler = messageHandler;
	        
	        // is read only
	        if (that.ria.readOnly == "true") {
				toggleReadOnly();				
			}
	        
	        setTimeout(function() {
	        	if (that.ria.url != null) {
	        		that.ria.onLoaded();
	        	}
	        }, 1000);
		}
	}
	
	// private methods
	
    var checkPluginVersion = function() {
    	var plugins = navigator.plugins;
    	for (var i = 0; i < plugins.length; i++) {
	    	var name = plugins[i].name;
	        if (termFound(name, "Adobe Reader") || termFound(name, "Adobe PDF") || termFound(name, "Acrobat")) {
	        	return parseInt(plugins[i].description.replace( /^[^\d]*/, '' ));
	        }
		}
		return 0;
    }
    
    var getPluginVersion = function() {
    	var plugins = navigator.plugins;
    	for (var i = 0; i < plugins.length; i++) {
	    	var name = plugins[i].name;
	        if (termFound(name, "Adobe Reader") || termFound(name, "Adobe PDF") || termFound(name, "Acrobat")) {
	        	return plugins[i].description;
	        }
		}
		return 0;
    }
    
    var termFound = function (strToSearch, term) {
	    return (strToSearch.indexOf(term) !== -1);
	}
    
  	var createDiagnosticMessage = function() {
		var message = "error";
		var message = "We are detecting that you are running <strong>" + jQuery.os.name + "</strong> ";
		message += "using <strong>" + jQuery.browser.name + " version " + jQuery.browser.version + "</strong>. <br />";
		if (checkPluginVersion() > 0) {
			message += "PDF Reader Plugin is: <strong>" + getPluginVersion() + "</strong>. <br />";
		}
		else {
			message += "Adobe Reader Plugin <strong>was not found</strong>.<br />";
		}
		message += "System: <strong>" + navigator.userAgent + "</strong>";
		
		return message;	
	};
	
	var toggleReadOnly = function() {
    	that.pdf.postMessage([VERSION, TOGGLE_RO_COMMAND]);
    }
};

// create plugin
var riaPlugin = new PdfPlugin();