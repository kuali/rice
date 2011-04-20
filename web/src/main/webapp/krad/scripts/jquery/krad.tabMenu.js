(function($) {
	$.fn.selectTab = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				selectPage: ""
			}, options);
			
			if(options.selectPage){
				var currentTab = $(this).find("a[name='" + options.selectPage + "']");
				if(currentTab){
					currentTab.parent().addClass("ui-state-active");
				}
			}
		});
	}
	
	$.fn.tabMenu = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				defaultSelectFirst: true,
				currentPage: ""
			}, options);
			
			//element id strings
			var id = $(this).parent().attr('id');
			var list_elements = "#" + id + " li";
			var link_elements = list_elements + " a";
			
			//Styling
			$(this).parent().addClass("ui-tabs tab-navigation-block");
			$(this).addClass("ui-helper-reset ui-helper-clearfix tabMenu");
			$(list_elements).addClass("ui-state-default ui-corner-top");
			if(options.currentPage){
				var currentTab = $(this).find("a[name='" + options.currentPage + "']");
				if(currentTab){
					currentTab.parent().addClass("ui-state-active");
				}
			}
			//Handlers and animation
			$(document).ready(function()
			{
					$(link_elements).each(function(i)
					{
						if(i == 0 && options.defaultSelectFirst && !options.currentPage){
							$(this).parent().addClass("ui-state-active");
						}
						$(this).click(
						function()
						{
							$(link_elements).each(function(){$(this).parent().removeClass("ui-state-active")});
							$(this).parent().addClass("ui-state-active");
						});
				
						$(this).hover(
						function()
						{
							$(this).parent().addClass("ui-state-hover");
						},		
						function()
						{
							$(this).parent().removeClass("ui-state-hover");
						});
				
						$(this).focus(
						function()
						{
							$(this).parent().addClass("ui-state-focus");
						});
				
						$(this).blur(
						function()
						{
							$(this).parent().removeClass("ui-state-focus");
						});
					});
			});
		});
	}
})(jQuery);