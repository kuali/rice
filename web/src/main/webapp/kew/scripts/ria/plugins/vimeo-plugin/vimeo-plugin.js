/**
 * The vimeo plugin
 */
function VimeoPlugin() {
	
	var VERSION = "1.0";
	var ERROR_MESSAGE = 'Error goes here';
	var VIMEO_PLAYER = "http://vimeo.com/moogaloop.swf";
	
	var dependecies = ["swfobject.js"];
	
	var actionStack = [];
	var ria = null;
	var that = this;
	
	// init vimeo_plugin
	this.init = function(ria) {
		that.ria = ria;
		ria.loadScripts(dependecies, true, onDependeciesLoad);
	};
	
	var vimeoPlayerLoaded = function() {}
	
	var onDependeciesLoad = function() {

		var video_id = getMovieId(that.ria.url);		
		var swf_id = that.ria.riaElement.attr('id');
		
		var flashvars = {
	        clip_id: video_id,
	        show_portrait: 1,
	        show_byline: 1,
	        show_title: 1,
			js_api: 1, // required in order to use the Javascript API
			js_onLoad: 'vimeoPlayerLoaded', // moogaloop will call this JS function when it's done loading (optional)
			js_swf_id: swf_id // this will be passed into all event methods so you can keep track of multiple moogaloops (optional)
	    };
		var params = {
			allowscriptaccess: 'always',
			allowfullscreen: 'true'
		};
		var attributes = {};
				
		swfobject.embedSWF(VIMEO_PLAYER, swf_id, "504", "340", "9.0.0","expressInstall.swf", flashvars, params, attributes);
	}
	
	// TODO use REGEX
	var getMovieId = function(url) {
		var a = url.split("/");
		if (a.length > 0) {
			return a[a.length - 1];
		}
	}
	
	this.getData = function() {}
    this.setData = function(data) {}
	this.action = function(action) {}
};

var riaPlugin = new VimeoPlugin();