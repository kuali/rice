<%--

    Copyright 2005-2018 The Kuali Foundation

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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ page import="java.util.Collection" %>

<c:set var="KualiForm" value="${SuperUserForm}" scope="request"/>
<kul:page headerTitle="Superuser Document Service" lookup="false"
  headerMenuBar="" transactionalDocument="false" showDocumentInfo="false"
  htmlFormAction="SuperUser" errorKey="document" docTitle="Superuser Document Service">
  <link href="css/screen.css" rel="stylesheet" type="text/css">
  <%--
<html-el:html>
<head>
<link href="css/screen.css" rel="stylesheet" type="text/css">
<title>Superuser Document Service</title>
<script language="JavaScript" src="scripts/en-common.js"></script>
</head>
<body>
--%>
<c:set var="KewRoutingKualiForm" value="${SuperUserForm}" scope="request" />
<%-- menu and logo --%>
<%--
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="headercell1">
  <tr>
    <td>
        <img src="images/wf-logo.gif" alt="Workflow" width=150 height=21 hspace=5 vspace=5>&nbsp;
    </td>
    <td width="90%">
        <a href="DocumentSearch.do?&superUserSearch=YES"><span class="maintext">Superuser Document Search</span></a>
    </td>
  </tr>
</table>

<br>--%><%-- message and body of document --%>
<%--
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="20" height="30">&nbsp;</td>
    <td>&nbsp;</td>
    <td width="20">&nbsp;</td>
  </tr>
  <tr>
    <td></td>
  	<td>
   		<jsp:include page="../WorkflowMessages.jsp" flush="true" />
   	</td>
   	<td></td>
  </tr>

  <tr>
    <td></td>
    <td>
--%>

  <script language="JavaScript" src="scripts/en-common.js"></script>
  <%--<kul:errors keyMatch="document" infoTitle=" "/>--%>
  <table width="100%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t">

  <tr>
    <td height="30">
       <strong>Super User Action on RouteHeader <c:out value="${SuperUserForm.documentId}"/></strong>
       <br>
    </td>
  </tr>
  <tr>
    <td style="border=solid gray">
      <iframe src="<c:out value="${SuperUserForm.docHandlerUrl}"/>" width="100%" height="500" hspace="0" vspace="0" frameborder="0"></iframe>
      <html-el:hidden property="docHandlerUrl" />
    </td>
  </tr>

<c:if test="${(! SuperUserForm.routeHeader['canceled']) && (SuperUserForm.authorized)}">
      <%--<html-el:form method="post" action="/SuperUser.do">--%>
    <html-el:hidden property="methodToCall" value="" />
      <html-el:hidden property="documentId" value="${SuperUserForm.routeHeader.documentId}" />
	  <html-el:hidden property="docId" value="${SuperUserForm.workflowDocument.documentId}" />
	  <html-el:hidden property="lookupableImplServiceName" />
  	  <html-el:hidden property="lookupType" />

  <c:if test="${(! SuperUserForm.routeHeader['final'])}">
  <tr>
    <td height="30">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t">
        <tr>
          <td width="25%" align="right" valign="top" class="thnormal">Annotation:</td>
          <td width="75%" class="datacell">
            <textarea name="annotation" cols="50" rows="6"></textarea>
          </td>
        </tr>
        <tr>
          <td align="right" valign="top" class="thnormal">Perform Post Processor Logic:</td>
          <td class="datacell">
            Value Before: ${SuperUserForm.runPostProcessorLogic}
            <html-el:checkbox property="runPostProcessorLogic" value="true"/>
            <html-el:hidden property="runPostProcessorLogic" value=""/>
          </td>
        </tr>
      </table>
    </td>
  </tr>

   <c:set var="inputLocation" value="SuperUser.jsp" scope="request"/>
   <jsp:include page="../AppSpecificRoute.jsp" flush="true" />


  <tr>
    <td height="30" class="headercell1" align="center" nowrap>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
       <tr>
	     <td nowrap align="center">
           <c:if test="${SuperUserForm.SUDocument && SuperUserForm.stateAllowsAction && SuperUserForm.superUserFinalApproveAllowed}">
             <html-el:image property="methodToCall.approve" src="images/buttonsmall_approvedoc.gif" style="border-style:none;" align="absmiddle" />
             <c:set var="futureNodeNames" value="${SuperUserForm.futureNodeNames}" />

            <c:if test="${not empty SuperUserForm.futureNodeNames}">
               &nbsp;&nbsp;&nbsp;&nbsp;Select a route node:&nbsp;
               <html-el:select property="destNodeName" >
                 <c:forEach var="nodeName" items="${SuperUserForm.futureNodeNames}">
                   <html-el:option value="${nodeName}" />
                 </c:forEach>
               </html-el:select>&nbsp;<html-el:image property="methodToCall.routeLevelApprove" src="images/buttonsmall_docroulevapp.gif" style="border-style:none;" align="absmiddle" />&nbsp;
            </c:if>
           </c:if>
         </td>
       </tr>
       <tr>
       	 <td>&nbsp;</td>
       </tr>
       <tr>
       	 <td nowrap align="center">
       	   <c:if test="${SuperUserForm.stateAllowsAction}">
       	     <c:if test="${SuperUserForm.SUDocument}">
               <html-el:image property="methodToCall.disapprove" src="images/buttonsmall_disapprovedoc.gif" style="border-style:none;" align="absmiddle" />&nbsp;&nbsp;&nbsp;&nbsp;
       	     </c:if>
             <html-el:image property="methodToCall.cancel" src="images/buttonsmall_canceldoc.gif" style="border-style:none;" align="absmiddle" />

             <c:set var="previousNodeNameCol" value="${SuperUserForm.previousNodes}" />
             <c:if test="${not empty previousNodeNameCol}">
	             <c:if test="${SuperUserForm.SUDocument}">

		           &nbsp;&nbsp;&nbsp;&nbsp;Select a node name:&nbsp;
			       <html-el:select property="returnDestNodeName">
		             <html-el:options collection="previousNodeNameCol" labelProperty="value" property="key"/>
		           </html-el:select>&nbsp;<html-el:image src="images/buttonsmall_retprevrtlevel.gif" align="absmiddle" property="methodToCall.returnToPreviousNode" style="border-style:none;" />&nbsp;&nbsp;&nbsp;&nbsp;
	             </c:if>
              </c:if>
            </c:if>
         </td>
       </tr>
      </table>
    </td>
  </tr>
 </c:if>

  <c:if test="${(! SuperUserForm.routeHeader.stateInitiated) && (! empty SuperUserForm.actionRequests) && SuperUserForm.superUserFinalApproveAllowedForActionRequest}">
    <tr>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="30">
         <strong>Super User Action on Action Requests</strong>
         <br>
      </td>
    </tr>
    <tr>
      <td>
        <jsp:include page="SUActionRequests.jsp" flush="true"/>
      </td>
    </tr>
  </c:if>
<%--</html-el:form>--%>
</c:if>

  </table>
<%--
    </td>
    <td></td>
  </tr>
</table>
--%>
<jsp:include page="../BackdoorMessage.jsp" flush="true"/>

  <%-- KULRICE-3035: The superuser form now stores the "returnLocation" needed by the doc search after the "cancel" button is clicked. --%>
<html-el:hidden property="returnLocation" />
<c:choose>
	<c:when test="${not empty SuperUserForm.returnLocation}">
		<c:set var="returnLocParam" value="returnLocation=${SuperUserForm.returnLocation}&" />
	</c:when>
	<c:otherwise>
		<c:set var="returnLocParam" value="" />
	</c:otherwise>
</c:choose>

  <div class="globalbuttons">
	<a href="DocumentSearch.do"><img src="images/buttonsmall_cancel.gif" border="0" alt="cancel"></a>
</div>
<%--
</body>
</html-el:html>
--%>
</kul:page>
