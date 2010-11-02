/*
 * Copyright 2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
