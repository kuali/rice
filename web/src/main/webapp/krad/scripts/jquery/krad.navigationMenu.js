(function($) {
		
	$.fn.navMenu = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				parent_div: "viewlayout_div",
				nav_div: "viewnavigation_div",
				animate: false,
				slideout: true,
				pad_out: 25,
				pad_in: 18
			}, options);
			
			//element id strings
			var id = $(this).parent().attr('id');
			var list_elements = "#" + id + " li";
			var link_elements = list_elements + " a";
			
			//Styling
			$(this).parent().addClass("navigation-block");
			$("#" + options.parent_div).addClass("navigation-parent-div");
			$("#" + options.nav_div).addClass("navigation-div");
			if (options.animate === "true") {
				//Animated menu
				$("li", this).addClass("animated-element");
				$(this).addClass("animated-navigation");
			}
			else{
				//Plain menu
				$("li", this).addClass("basic-element");
				$(this).addClass("basic-navigation");
			}
			
			if(options.slideout === "true"){
				$(this).before("<div class='panelslider_control' id='control'><a id='controlbtn' href='#' alt='open'><img src='/kr-dev/krad/images/slide-control-close.png' width='30' height='30' alt='close' /></a></div>");
			}
			
			//Handlers and animation
			$(document).ready(function()
			{
				if(options.animate === "true"){
					
					$(link_elements).each(function(i)
					{
						$(this).click(
						function()
						{
						$("li.animated-element a").removeClass("current");
						$(this).addClass("current");
						});
				
						$(this).hover(
						function()
						{
							if (!$(this).is(':animated')) {
								$(this).animate({
									paddingLeft: options.pad_out
								}, 150);
							}
						},		
						function()
						{
								$(this).animate({
									paddingLeft: options.pad_in
								}, 150);
						});
				
						$(this).focus(
						function()
						{
							$(this).animate({ paddingLeft: options.pad_out }, 150);
						});
				
						$(this).blur(
						function()
						{
							$(this).animate({ paddingLeft: options.pad_in }, 150);
						});
					});
				}
				
				if(options.slideout === "true"){
					//Slideout animation
					$("a#controlbtn", this).click(function(e) {
			            e.preventDefault();
			            var slidepx = $("#" + options.nav_div).width() + 5;
			            if (!$("#" + options.parent_div).is(':animated')) {
			                if (parseInt($("#" + options.parent_div).css('marginLeft'), 0) + 5 < slidepx) {
			                    $(this).removeClass('close').html('<img src="/kr-dev/krad/images/slide-control-close.png" width="30" height="30" alt="close" />');
			                    margin = "+=" + slidepx;
			                } else {
			                    $(this).addClass('close').html('<img src="/kr-dev/krad/images/slide-control-open.png" width="30" height="30" alt="open" />');
			                    margin = "-=" + slidepx;
			                }
			                $("#" + options.parent_div).animate({marginLeft: margin}, "slow");
			            }
			        });
				}
			});
		});
	}
})(jQuery);