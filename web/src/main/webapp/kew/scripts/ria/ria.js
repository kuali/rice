/**
 * ria plugin loader 
 */
var ria = (function($) {
	
	// plugin types
	var PLUGIN_REGEX = {
		"pdf": /\.pdf/,
		"vimeo": /vimeo\.com/
	};
	
	var PLUGIN_URL_PREFIX = "scripts/ria/plugins/";
	var PLUGIN_NAME = "-plugin";
	var JS_TYPE = ".js";
	
	var url = null;
	var readOnly = false;
	var plugin = null;
	var key = 'DocumentContent';
	var pluginPath = PLUGIN_URL_PREFIX;
	
	// element which holds data
	var dataElement;
	
	// element to which plugin's content will be loaded
	var riaElement;
	var errorContainer;
	var errorMessage;
	
	// the name of the action taken
	var actionTaken = null;
	
	// container for all tiny buttons
	var tinyButtonClicked = null;

	// init ria
	var init = function(url, dataElement, riaElement, errorContainer, errorMessage, readOnly) {		
		
		self = this;
		self.url = url;
		self.dataElement = $('#' + dataElement);
		self.riaElement = $('#' + riaElement);
		console.log(url);
		//TODO create them dynamically
		self.errorContainer = $('#' + errorContainer);
		self.errorMessage = $('#' + errorMessage);
		
		self.readOnly = readOnly;
		initPlugin(url);
	};
	
	var registerKualiEvents = function() {
		
		$("#kualiForm").bind("submit", function(e) {
			console.log("kualiForm submit ");
	    	self.plugin.getData();
	    	// set KUALI global vars to false
	    	formHasAlreadyBeenSubmitted = false;
			excludeSubmitRestriction = false;
			return false;
		});
		
		// set action which was taken to trigger it later from callback
	   	$("input.globalbuttons").click(function() {
			self.actionTaken = $(this).attr('name');
		});
      
        $(".tinybutton").click(function() {
        	self.tinyButtonClicked = $(this);
        	self.actionTaken = null;	
        });
	}
	
	/**
	* Initializes plugin.
 	*/
	var initPlugin = function(url) {
		$.each(PLUGIN_REGEX, function(type, value) {
			if (url.search(value) >= 0) {
				self.pluginPath = PLUGIN_URL_PREFIX + type + PLUGIN_NAME + "/";
				$.getScript(self.pluginPath + type + PLUGIN_NAME + JS_TYPE , onPluginLoaded);
			}
		});
	}
		
	/**
	* Loads js files.
	*/
	var loadScripts = function(files, isLocal, callback) {
		if (files != null) {
			var maxResponses = files.length;
			var loaded = 0;
			$.each(files, function(index, file) {
				
				// load files locally
				if (isLocal) {
					file = self.pluginPath + file;
				}
				
				$.getScript(file, function() {
					loaded++;
					// all files loaded?
					if (maxResponses == loaded) {
						callback();
					}
				})
			});
		}
	}
	
	/**
	 * Overrides Kuali's toggleTab
	 */
	var toggleTab = function(doc, tabKey) {
		if (doc.forms[0].elements['tabStates(' + tabKey + ')'].value == 'CLOSE') {
			showTab(document, tabKey);
			if (tabKey == key) {
				self.actionTaken = "showTab";
				initPlugin(self.url);
			}
	    } else {
	    	
	    	if (tabKey == key) {
				self.actionTaken = "hideTab";
		        self.plugin.getData();
			}
			else {
				hideTab(document, tabKey);
			}
		}
		return false;
	};
	
	/**
	* Callback called after plugin was loaded successfully.
 	*/
	var onPluginLoaded = function() {
		self.plugin = riaPlugin;
		self.plugin.init(self);
	};
	
	/**
	* Callback from plugin on get data.
	*/
	var onGetData = function(data) {
		if (self.actionTaken != null) {
			self.dataElement.val(data);			
			switch (self.actionTaken) {
				case 'hideTab':
					hideTab(document, self.key);
					break;
				case 'showTab':
					break;
				default:
					$("#kualiForm").unbind("submit");
					$('[name="' + self.actionTaken + '"]').trigger('click');
			}
		}
		else if (self.tinyButtonClicked != null) {
    		$("#kualiForm").unbind("submit");
    		self.tinyButtonClicked.trigger('click');
    	}
	};
	
	// CALLBACKS
	
	/**
	* Callback from plugin on set data.
	*/
	var onSetData = function(data) {
		//console.log("onSetData");
	};
	
	/**
	* Callback from plugin when error occurs.
	*/
	var onError = function(message) {
		self.riaElement.hide();
		self.errorMessage.html(message);
		self.errorContainer.show();		
	}
	
	/**
	* Callback called after ria element was 
	* embended into DOM and its ready to use.
	* <p>This is a safe starting point 
	* to start interacting with the plugin </p>
	*/
	var onLoaded = function() {
		if (self.dataElement.val() != null 
			&& self.dataElement.val() != "") {
			self.plugin.setData(self.dataElement.val());
		}
		registerKualiEvents();
	};
	
	// public methods
	return {
		init: init,
		loadScripts: loadScripts,
		toggleTab: toggleTab,
		
		// ria properties used by plugins
		url: url,
		riaElement: riaElement,
		dataElement: dataElement,
		
		// callbacks
		onLoaded: onLoaded,
		onGetData: onGetData,
		onSetData: onSetData,
		onError: onError
	};
})(jQuery);

/**
* Override KUALI taggleTab method.
*/
toggleTab = function(doc, tabKey) {
	return ria.toggleTab(doc, tabKey);
};