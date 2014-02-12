<%@ tag import="org.kuali.rice.krad.util.KRADConstants" %>
<%@ tag import="java.util.Enumeration" %>
<%--
~ Copyright 2006-2011 The Kuali Foundation
~
~ Licensed under the Educational Community License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.opensource.org/licenses/ecl2.php
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<div class="header2">
  <div class="header2-left-focus">
    <div class="breadcrumb-focus">
    	<portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.kew.url}/ActionList.do'>
   		<img src="rice-portal/images/icon-port-actionlist.gif" alt="action list" width="91" height="19" border="0"></portal:portalLink>
    	<portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.documentsearch.base.url}'>
    	<img src="rice-portal/images/icon-port-docsearch.gif" alt="doc search" width="96" height="19" border="0"></portal:portalLink>
     </div>
  </div>
</div>
