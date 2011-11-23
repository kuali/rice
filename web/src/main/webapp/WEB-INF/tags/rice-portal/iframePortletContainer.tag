<%--
 Copyright 2005-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp" %>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>
<%@ attribute name="frameHeight" required="false" %>

<c:if test="${empty frameHeight || frameHeight == 0}">
  <c:set var="frameHeight" value="750"/>
</c:if>

<iframe src="${channelUrl}"
        onload='<c:if test="${ConfigProperties.test.mode ne 'true'}">setIframeAnchor("iframeportlet")</c:if>'
        name="iframeportlet" id="iframeportlet" style="width: 100%;"
        title="E-Doc" scrolling="auto" frameborder="0" width="100%"></iframe>

<script type="text/javascript">
  jQuery(function() {
    var if_height = ${frameHeight};
    var if_width;
    var if_prev_height
    var thisIframe = jQuery("iframe[src='${channelUrl}']");

    //find iframe source host
    var iframeSrc = '${channelUrl}';
    var regex = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
    var receivingMessages = false;
    var intervalId;
    if(iframeSrc.indexOf("http") == 0 || iframeSrc.indexOf("ftp") == 0){
    	iframeSrc = iframeSrc.match(regex)[1].toString();
    }
    else{
    	//if it doesnt begin with http it must be local domain
    	iframeSrc = window.location.host;
    }

    if((iframeSrc !== window.location.host && (!navigator.cookieEnabled || jQuery.browser.msie))){
          jQuery(thisIframe).replaceWith(
                  "<iframe src='${channelUrl}' name='iframeportlet' id='iframeportlet'" +
                  "title='E-Doc' height='${frameHeight}' width='100%' frameborder='0'></iframe>"
          );
    }

    if(!jQuery.browser.msie){
         jQuery(thisIframe).height(if_height);
    }

    if (iframeSrc !== window.location.host) {
    	setupCrossDomainResize()
    }

    jQuery(thisIframe).load(function() {
      if (iframeSrc === window.location.host){
        setSameDomainIframeHeight();
        intervalId = setInterval(setSameDomainIframeHeight, 500);
      }
    });

    function setupCrossDomainResize(){
      sameDomain = false;
      if (navigator.cookieEnabled && !jQuery.browser.msie) {
          //add parent url to hash of iframe to pass it in, it will be stored in the cookie of that
          //frame for its future page navigations so it can communicate back with postMessage
          var parentUrl = document.location.href;
          var newUrl = '${channelUrl}' + '#' + encodeURIComponent(parentUrl);
          jQuery(thisIframe).attr("src", newUrl);
          //Also put it in the cookie in this context incase the page being viewed in the iframe switches
          //to the same host
          jQuery.cookie('parentUrl', parentUrl, {path: '/'});
        }
      else{
        jQuery(thisIframe).height(if_height);
      }

      //All kinds of special cases because of how IE handles iframe sizes differently
      if(!jQuery.browser.msie){
        jQuery(thisIframe).attr("scrolling", "auto");
        jQuery(thisIframe).css("overflow", "auto");
        jQuery("#iframe_portlet_container_div").css("overflow", "auto");
      }
    }

    //a function for iframes in the same domain
    function setSameDomainIframeHeight() {
      //check every iteration to see if the iframe is no longer in the same domain
      var url = jQuery(thisIframe).attr('src');
      if((url.indexOf("http") != 0 && url.indexOf("ftp") != 0) || url.match(regex)[1].toString() === window.location.host){
    	  sameDomain = true;
	      if (thisIframe[0] && thisIframe[0].contentWindow.document.body) {
	          if_height = thisIframe[0].contentWindow.document.body.scrollHeight;

	          thisIframe.height(if_height);
	        }
	    }
      else{
        clearInterval(intervalId);
        setupCrossDomainResize();
      }
    }

    jQuery.receiveMessage(function(e) {
      if(!sameDomain){
	      // Get the height from the passsed data.
	      var h = Number(e.data.replace(/.*if_height=(\d+)(?:&|$)/, '$1'));

	      if (!isNaN(h) && h > 0 && h + 35 !== if_height) {
	        // Height has changed, update the iframe.
	        if_height = h + 35;
	        thisIframe.height(if_height);
	      }
      }
    });
  })
          ;
</script>
