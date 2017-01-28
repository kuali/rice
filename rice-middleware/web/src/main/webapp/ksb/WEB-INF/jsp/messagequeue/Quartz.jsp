<%--

    Copyright 2005-2017 The Kuali Foundation

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
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="http://www.kuali.org/struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%--KULRICE-12287:* Included the kul taglib to let us use the testBanner tag on this page --%>
<%@ taglib tagdir="/WEB-INF/tags/kr" prefix="kul"%>

<html-el:html>

<head>
<title>Exception Routing Queue</title>
<style type="text/css">
   .highlightrow {}
   tr.highlightrow:hover, tr.over td { background-color: #66FFFF; }
</style>
<%--KULRICE-12287: Added the kuali.css to this page for the testBanner styling --%>
<link href="css/kuali.css" rel="stylesheet" type="text/css">
<link href="css/screen.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/messagequeue-common.js"></script>
</head>

<body>
<%--KULRICE-12287:Added the new tag which displays a banner when in the testing environments --%>
<kul:testBanner />
<table width="100%" border=0 cellpadding=0 cellspacing=0
	class="headercell1">
	<tr>
		<td width="15%"><img src="images/wf-logo.gif"
			alt="Workflow" width=150 height=21 hspace=5 vspace=5></td>
		<td width="85%"><a href="Quartz.do?methodToCall=start">Refresh Page</a></td>
		<td>&nbsp;&nbsp;</td>
	</tr>
</table>
<br/>
<br/>
<table width="90%" border=0 cellspacing=0 cellpadding=0 align="center">
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td>
		  <display:table excludedParams="*" pagesize="40" class="bord-r-t" style="width:100%" cellspacing="0" cellpadding="0" name="jobs" export="true" id="result"  requestURI="Quartz.do?methodToCall=start" defaultsort="4" defaultorder="descending"
				decorator="org.kuali.rice.ksb.messaging.web.KSBTableDecorator">
		    <display:setProperty name="paging.banner.placement" value="both" />
		    <display:setProperty name="export.banner" value="" />
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Job Name</div>" sortProperty="jobDetail.name">
		    	<c:out value="${result.jobDetail.name}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Job Group</div>" >
		    	<c:out value="${result.jobDetail.group}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Description</div>" >
		    	<c:out value="${result.jobDetail.description}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Time to execute</div>" sortProperty="trigger.startTime.time">
		    	<c:out value="${result.trigger.nextFireTime}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>FullName</div>" >
		    	<c:out value="${result.jobDetail.fullName}"/>&nbsp;
		    </display:column>
        <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="false" title="<div style='text-align:center;vertical-align:top;'>Actions</div>" >
         <c:choose>
           <c:when test="${not empty result.jobDetail.jobDataMap[MessageServiceExecutorJob.MESSAGE_KEY]}">
            <a href='Quartz.do?methodToCall=moveToRouteQueue&jobName=<c:out value="${result.jobDetail.name}" />&jobGroup=<c:out value="${result.jobDetail.group}"/>'>Put in message queue</a>
           </c:when>
           <c:otherwise>
           No messages available
           </c:otherwise>

         </c:choose>
		    </display:column>
		  </display:table>
	</td>
  </tr>
</table>

    <jsp:include page="../Footer.jsp"/>

</html-el:html>
