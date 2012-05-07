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
        name="iframeportlet" id="iframeportlet" style="height: ${frameHeight}px; width: 100%;"
        title="E-Doc" scrolling="yes" frameborder="0" width="100%"></iframe>

<script type="text/javascript">
  jQuery("#Uif-Application").ready(function () {
    var channelUrlEscaped = "${channelUrl}".replace(/'/g, "\\'");
    var iframeSrc = "${channelUrl}";
    var thisIframe = jQuery("iframe[src='" + channelUrlEscaped + "']");

    var previousHeight = "${frameHeight}";
    var previousWidth = jQuery("#iframe_portlet_container_div").width();
    var sameDomain = false;
    var regex = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
    var intervalId = "";

    //iframe resize breaks datatables in older IEs
    var browserIsOlderIE = jQuery.browser.msie && (jQuery.browser.version == 8.0);

    if (iframeSrc.indexOf("http") == 0 || iframeSrc.indexOf("ftp") == 0) {
      iframeSrc = iframeSrc.match(regex)[1].toString();
    }
    else {
      //if it doesnt begin with http it must be local domain
      iframeSrc = window.location.host;
    }

    //Unsupported browser combinations that the iframe resize wont work properly on
    if ((iframeSrc !== window.location.host && (!navigator.cookieEnabled || jQuery.browser.msie))
            || (browserIsOlderIE)) {
/*      jQuery(thisIframe).replaceWith(
              "<iframe src='${channelUrl}' name='iframeportlet' id='iframeportlet'" +
                      "title='E-Doc' height='${frameHeight}' width='100%' frameborder='0'></iframe>"
      );*/

      jQuery(thisIframe).attr("scroll", "yes");
      jQuery(thisIframe).attr("scrolling", "yes");
      jQuery(thisIframe).css("overflow", "auto");
    }

    if(!browserIsOlderIE){
      jQuery(thisIframe).attr("scroll", "no");
      jQuery(thisIframe).attr("scrolling", "no");
      if (iframeSrc !== window.location.host) {
        setupCrossDomainResize();
      }
      else {
        intervalId = setInterval(setSameDomainIframeHeight, 500);
      }
    }

    function resizeIframe() {
      var skipResize = thisIframe.contents().find("#Uif-Application").attr("data-skipResize");
      if(skipResize == undefined || skipResize == "false"){
        var newHeight = thisIframe.contents().find("body").outerHeight(true);
        var newWidth = jQuery("#iframe_portlet_container_div").width() - 15;
        thisIframe.contents().find("body").attr("style", "overflow-x: auto; padding-right: 20px;");

        if (newHeight > 100 && (newHeight != previousHeight || newWidth != previousWidth)) {
          previousHeight = newHeight;
          previousWidth = newWidth;
          thisIframe.height(newHeight);
          thisIframe.width(newWidth);
        }
      }
    }

    function setupCrossDomainResize() {
      sameDomain = false;
      if (navigator.cookieEnabled && !jQuery.browser.msie) {
        //add parent url to hash of iframe to pass it in, it will be stored in the cookie of that
        //frame for its future page navigations so it can communicate back with postMessage
        var parentUrl = document.location.href;
        var newUrl = "${channelUrl}" + '#' + encodeURIComponent(parentUrl);
        jQuery(thisIframe).attr("src", newUrl);
        //Also put it in the cookie in this context incase the page being viewed in the iframe switches
        //to the same host
        jQuery.cookie('parentUrl', parentUrl, {path:'/'});
      }
    }

    function setSameDomainIframeHeight() {
      //check every iteration to see if the iframe is no longer in the same domain
      var url = jQuery(thisIframe).attr('src');
      if ((url.indexOf("http") != 0 && url.indexOf("ftp") != 0) || url.match(regex)[1].toString() === window.location.host) {
        sameDomain = true;
        resizeIframe();
      }
      else {
        clearInterval(intervalId);
        setupCrossDomainResize();
      }
    }

    jQuery.receiveMessage(function (e) {
      console.log("message received");
      if (!sameDomain) {
        // Get the height from the passed data
        var newHeight = Number(e.data.replace(/.*if_height=(\d+)(?:&|$)/, '$1'));
        var newWidth = jQuery("#iframe_portlet_container_div").width() - 15;
        if (newWidth < 500) {
          newWidth = 500;
        }
        if (!isNaN(newHeight) && newHeight > 100
                && (newHeight != previousHeight || newWidth != previousWidth)) {
          previousHeight = newHeight;
          previousWidth = newWidth;
          thisIframe.height(newHeight);
          thisIframe.width(newWidth);
        }
      }
    });
  });
</script>
