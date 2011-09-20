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
  <c:set var="frameHeight" value="500"/>
</c:if>

<iframe src="${channelUrl}"
        onload='<c:if test="${ConfigProperties.test.mode ne 'true'}">setIframeAnchor("iframeportlet")</c:if>'
        name="iframeportlet" id="iframeportlet" style="width: 100%; overflow: hidden;"
        title="E-Doc" frameborder="0" scrolling="no" width="100%"></iframe>

<script type="text/javascript">
  jQuery(function(){
  var if_height = ${frameHeight};
  var iframe = jQuery("#iframeportlet");
  jQuery.receiveMessage(function(e) {
    // Get the height from the passsed data.
    var h = Number(e.data.replace(/.*if_height=(\d+)(?:&|$)/, '$1'));

    if (!isNaN(h) && h > 0 && h !== if_height) {
      // Height has changed, update the iframe.
      if_height = h + 40;
      iframe.height(if_height);
    }

  });
  });
</script>
