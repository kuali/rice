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

<%--
   The iframePortletContainter uses easyXDM to enable the portal resizing when the content is on a different server url.

   The portal needs to know where to find the resize_intermediate.html file on the remote server.  This means that the
   application contexts of the local and remote content need to be specified in the configuration parameters.  These
   parameters start with "context.names.". A specific suffix is not important.  For applications that use a standalone
   rice server "context.names.rice" would need to be specified.  By default context.names.app is set to app.context.name.
--%>

<script type="text/javascript">
  var getLocation = function(href) {
    var location = document.createElement("a");
    location.href = href;
    return location;
  };

  var channelLocation = getLocation("${channelUrl}");
  var contextNames = new Array();
  <c:forEach var="contextName" items="${ConfigProperties.context.names}">
          <c:if test="${not empty contextName.value}">
          contextNames.push("<c:out value="${contextName.value}" />");
  </c:if>
  </c:forEach>
  var remote = channelLocation.protocol + '//' + channelLocation.host + "/";
  for (var i = 0; i < contextNames.length; i++) {
    if (channelLocation.pathname.lastIndexOf(contextNames[i], 1) === 1) {
      remote += contextNames[i] + "/";
      break;
    }
  }
  var swf = remote + "rice-portal/scripts/easyXDM/easyxdm.swf";
  if (jQuery.browser.msie){
    remote += "rice-portal/scripts/easyXDM/resize_intermediate.html?url=/"
            + encodeURIComponent(channelLocation.pathname + channelLocation.search);
  } else {
    remote += "rice-portal/scripts/easyXDM/resize_intermediate.html?url="
            + encodeURIComponent(channelLocation.pathname + channelLocation.search);
  }

  new easyXDM.Socket(/** The configuration */{
    remote: remote,
    swf: swf,
    container: "embedded",
    props: {
      style: {
        width: "100%"
      }
    },
    onMessage: function(message, origin) {
      var availableHeight = window.innerHeight - 250;
      if (availableHeight > message) {
        this.container.getElementsByTagName("iframe")[0].style.height = availableHeight + "px";
      } else {
        this.container.getElementsByTagName("iframe")[0].style.height = message + "px";
      }
    }
  });

</script>

<div id="embedded">
</div>
