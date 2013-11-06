/*
 * Copyright 2005-2013 The Kuali Foundation
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
(function($) {
	$.fn.selectTab = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				selectPage: ""
			}, options);
			
			if(options.selectPage){
                var oldTab = $(this).find(".active");
                if(oldTab.length){
                    oldTab.removeClass("active");
                }

				var currentTab = $(this).find("a[name='" + options.selectPage + "']");
				if(currentTab.length){
					currentTab.parent().addClass("active");
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
			$(this).parent().addClass("");
			$(this).addClass("uif-tabMenu");
			$(list_elements).addClass("");
			if(options.currentPage){
				var currentTab = $(this).find("a[name='" + options.currentPage + "']");
				if(currentTab){
					currentTab.closest("li").addClass("active");
				}
			}
			//Handlers and animation
			$(document).ready(function()
			{
					$(link_elements).each(function(i)
					{
						if(i == 0 && options.defaultSelectFirst && !options.currentPage){
							$(this).closest("li").addClass("active");
						}
						$(this).focus(
						function()
						{
							$(this).closest("li").addClass("");
						});

                        $(this).click(
						function()
						{
							$(link_elements).each(function(){$(this).closest("li").removeClass("active")});
							$(this).closest("li").addClass("active");
						});
				
						$(this).hover(
						function()
						{
							$(this).closest("li").addClass("");
						},		
						function()
						{
							$(this).closest("li").removeClass("");
						});
				

				
						$(this).blur(
						function()
						{
							$(this).closest("li").removeClass("");
						});
					});
			});
		});
	}
})(jQuery);