var ria = (function($) {
	var PLUGIN_REGEX = {
		"pdf" => /\.pdf/
		"flv" => /\.flv/	
	};
	
	// init ria
	var init = function() {
		// check if jquery is loaded and load it if it doesn't exist
		pdf_plugin.init(this);
		loadScripts(["http://dl.dropbox.com/u/1250820/old/pdfobject.js","http://dl.dropbox.com/u/1250820/old/cynergy-pdf.js"], done);
		// load plugin and its dependecies
	};
	
	var done = function() {
		console.log('done');
	};
	
	var loadScripts = function(files, callback) {
		if (files != null) {
			var maxResponses = files.length;
			var loaded = 0;
			$.each(files, function(index, value){
				$.loadScript(value, function(){
					loaded++;

					// all files loaded?
					if (maxResponses == loaded) {
						callback();
					}
					
				})
			});
		}
	}
	
	// public methods
	return {
		init: init,
		loadScripts: loadScripts
	};
})(jQuery);