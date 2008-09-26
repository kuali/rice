<%@ taglib uri="../../tld/struts-html-el.tld" prefix="html-el" %>
<%@ taglib uri="../../tld/struts-bean-el.tld" prefix="bean-el" %>
<%@ taglib uri="../../tld/struts-logic-el.tld" prefix="logic-el"%>
<%@ taglib uri="../../tld/c.tld" prefix="c" %>
<%@ taglib uri="../../tld/fmt.tld" prefix="fmt" %>
<%@ taglib uri="../../tld/displaytag.tld" prefix="display-el" %>
<%@ page import="org.kuali.rice.kew.preferences.service.PreferencesService" %>

<html-el:html>
<head>
<link href="../kr/css/kuali.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<title class="pagetitle" >Workflow Preferences</title>
</head>
<body>
<!-- table width="100%" border=0 cellpadding=0 cellspacing=0 class="headercell1"-->
<div class="headerarea-small">
<table width="100%" >
  <tr>
    <td><img src="images/wf-logo.gif" alt="OneStart Workflow" width=150 height=21 hspace=5 vspace=5></td>
    <td width="90%"><html-el:link action="ActionList">Return to Action List</html-el:link></td>
  </tr>
</table>
</div>

<br>
<jsp:include page="../WorkflowMessages.jsp" flush="true" />
<div class="workarea"><table><tr><td rowspan=3><strong>Action List Preferences</strong></td></tr></table></div>
 
<html-el:form action="/Preferences.do">
<html-el:hidden property="returnMapping"/>
<table width="100%" class="datatable-80" align="center">
	<tr>
		<td class="infocell">Automatic Refresh Rate:(in whole minutes - 0 is no automatic refresh.)</td>
        <td class="infocell"><html-el:text property="preferences.refreshRate" size="3" /></td>
	</tr>
        <tr>
          <td class="infocell" width="50%">Action List Page Size</td>
          <td class="infocell"><html-el:text property="preferences.pageSize" size="3" /></td>
        </tr>
        <tr>
          <td class="infocell">Email Notification</td>
          <td class="infocell">
            <html-el:select property="preferences.emailNotification">
              <html-el:option value="${Constants.EMAIL_RMNDR_NO_VAL}">None</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_DAY_VAL}">Daily</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_WEEK_VAL}">Weekly</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_IMMEDIATE}">Immediate</html-el:option>
            </html-el:select>
            <bean-el:message key="actionlist.help.emailNotification"/>
          </td>
        </tr>
        <tr>
          <td class="infocell">Send Email Notifications for Documents where I am a Primary Delegate</td>
          <td class="infocell">Yes <html-el:radio property="preferences.notifyPrimaryDelegation" value="${Constants.PREFERENCES_YES_VAL}"/>
                No <html-el:radio property="preferences.notifyPrimaryDelegation" value="${Constants.PREFERENCES_NO_VAL}"/>
          </td>
        </tr>
        <tr>
          <td class="infocell">Send Email Notifications for Documents where I am a Secondary Delegate</td>
          <td class="infocell">Yes <html-el:radio property="preferences.notifySecondaryDelegation" value="${Constants.PREFERENCES_YES_VAL}"/>
                No <html-el:radio property="preferences.notifySecondaryDelegation" value="${Constants.PREFERENCES_NO_VAL}"/>
          </td>
        </tr>
        <tr>
          <td class="infocell" width="50%">Delegator Filter</td>
          <td class="infocell">
				<html-el:select property="preferences.delegatorFilter">
				  <html-el:options collection="delegatorFilter" labelProperty="value" property="key"/>
				</html-el:select>
          </td>
        </tr>
        <tr>
          <td class="infocell" colspan="2">Fields Displayed In Action List</td>
        </tr>

        <tr>
          <td class="infocell">Document Type</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showDocType" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDocType" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">Title</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showDocTitle" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDocTitle" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">ActionRequested</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showActionRequested" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showActionRequested" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">Initiator</td>
			<td class="infocell">Yes <html-el:radio property="preferences.showInitiator" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showInitiator" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>
        
        <tr>
          <td class="infocell">Delegator</td>
			<td class="infocell">Yes <html-el:radio property="preferences.showDelegator" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDelegator" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">Date Created</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showDateCreated" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDateCreated" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>
		<tr>
          <td class="infocell">Date Approved</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showDateApproved" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDateApproved" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>
        <tr>
          <td class="infocell">Current Route Node(s)</td>
			<td class="infocell">Yes <html-el:radio property="preferences.showCurrentNode" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showCurrentNode" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>
        <tr>
          <td class="infocell">WorkGroup Request</td>
			<td class="infocell">Yes <html-el:radio property="preferences.showWorkgroupRequest" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showWorkgroupRequest" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">Document Route Status</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showDocumentStatus" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showDocumentStatus" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

        <tr>
          <td class="infocell">Clear FYI</td>
          <td class="infocell">Yes <html-el:radio property="preferences.showClearFyi" value="${Constants.PREFERENCES_YES_VAL }"/>
          		No <html-el:radio property="preferences.showClearFyi" value="${Constants.PREFERENCES_NO_VAL }"/>
          </td>
        </tr>

		<c:if test="${PreferencesForm.showOutbox }">
	        <tr>
	          <td class="infocell">Use Outbox</td>
	          <td class="infocell">Yes <html-el:radio property="preferences.useOutbox" value="${Constants.PREFERENCES_YES_VAL }"/>
	          		No <html-el:radio property="preferences.useOutbox" value="${Constants.PREFERENCES_NO_VAL }"/>
	          </td>
	        </tr>
        </c:if>
        
        <tr>
          <td colspan="2" class="infocell">Document Route Status Colors for Actionlist Entries</td>
        </tr>
		<tr>
			<td class="infocell">Saved</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorSaved" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		
		<tr>
			<td class="infocell">Initiated</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorInitiated" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>

		<tr>
			<td class="infocell">Disapproved</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorDissaproved" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Enroute</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorEnroute" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Approved</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorApproved" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Final</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorFinal" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Processed</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorProccessed" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Exception</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorException" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
		<tr>
			<td class="infocell">Canceled</td>
			<td class="infocell">
			  <table>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>'><html-el:radio property="preferences.colorCanceled" value="${colorType.key}" /></td>
                  </c:forEach>
				</tr>
			  </table>
			</td>
		</tr>
	
		<tr align=center>
			<td class="infoline" colspan="2" align=center>
                <html-el:image src="images/buttonsmall_save.gif" align="absmiddle" property="methodToCall.save" />&nbsp;
                <a href="javascript:document.forms[0].reset()"><img src="images/buttonsmall_reset.gif" border=0 alt="reset" align="absmiddle"></a>
			</td>
			
		</tr>
	</table>

</html-el:form>
</td>
<td></td>
</tr>
</table>

<jsp:include page="../BackdoorMessage.jsp" flush="true"/>

</body>
</html-el:html>