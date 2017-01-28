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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<tr>
  <td><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
  <td> 
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" align="center">
	  <tr>
		<td class="thnormal" colspan="2" align="center" height="30"><strong>Action Requests</strong></td>
	  </tr>
	  
	  <c:choose> 
	   <c:when test="${empty DocumentOperationForm.actionRequests}">
	     <tr><td class="datacell" colspan="2" align="center" height="15">None</td></tr>
	   </c:when>
	   <c:otherwise>
	 	<logic-el:iterate id="actionRequest" name="DocumentOperationForm" property="actionRequests" indexId="ctr">
	      <html-el:hidden property="actionRequests[${ctr}].actionRequestId" />
	      <html-el:hidden property="actionRequests[${ctr}].jrfVerNbr" />
	 	  <tr>
		    <td width="33%" class="headercell3-b-l" align="right"><b> Action Request ID: </b><c:out value="${actionRequest.actionRequestId}" /> </td>
		    <td width="66%" class="headercell3-b-l">
		      <html-el:radio property="actionRequestOp[${ctr}].value" value="update" />Update &nbsp;&nbsp;<html-el:radio property="actionRequestOp[${ctr}].value" value="delete"/>Delete&nbsp;&nbsp;<html-el:radio property="actionRequestOp[${ctr}].value" value="noop"/>No Operation&nbsp;&nbsp;
		      <html-el:hidden property="actionRequestOp[${ctr}].index" />
		    </td>
		  </tr>
		  <tr>
		    <td width="33%" align="right" class="thnormal">* Document Version:</td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].docVersion" /></td>
		  </tr>
		  <tr>
	  	    <td width="33%" align="right" class="thnormal">* Document ID:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].documentId" /></td>
		  </tr>	    
		  <tr>
		  	<td width="33%" align="right" class="thnormal">* Route Node Instance ID:</td>
	  	    <td width="66%" class="datacell">
	  	    <c:choose>
	  	      <c:when test="${actionRequest.nodeInstance==null}">
	  	        None
	  	      </c:when>
	  	      <c:otherwise>
	  	      	<html-el:text property="actionRequests[${ctr}].nodeInstance.routeNodeInstanceId" />
	  	      </c:otherwise>
	  	    </c:choose>  	      
	  	    </td>
		  </tr>
		  <tr>
	  	    <td width="33%" align="right" class="thnormal">* Action Requested:</td>
	  	    <td width="66%" class="datacell">
	  	      <html-el:select property="actionRequests[${ctr}].actionRequested" value="${actionRequest.actionRequested}">
    		    <c:set var="actionRequestCds" value="${DocumentOperationForm.actionRequestCds}"/>
    		    <html-el:options collection="actionRequestCds" property="key" labelProperty="value"/>
  			  </html-el:select>    
	  	    </td>
	  	  </tr>  
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">* Create Date: </td>
	  	    <td width="66%" class="datacell"><%-- <html-el:text property="actionRequests[${ctr}].createDateString" />&nbsp; --%>
	  	     <input type='text' name='actionRequestCreateDate<c:out value="${ctr}"/>' value='<c:out value="${actionRequest.createDateString}"/>'>
	  	      <a href="javascript:addCalendar('actionRequestCreateDate<c:out value="${ctr}"/>', 'Select Date', 'actionRequestCreateDate<c:out value="${ctr}"/>', 'DocumentOperationForm'); showCal('actionRequestCreateDate<c:out value="${ctr}"/>');"><img src="images/cal.gif" width="16" height="16" align="absmiddle" alt="Click Here to select a date"></a>
	  	      <html-el:hidden property="actionRequests[${ctr}].createDateString" />
	  	   </td>
	  	  </tr>   	  
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">* Status:</td>
	  	    <td width="66%" class="datacell">
	  	      <html-el:select property="actionRequests[${ctr}].status" value="${actionRequest.status}">
    		    <c:set var="actionRequestStatuses" value="${DocumentOperationForm.actionRequestStatuses}"/>
    		    <html-el:options collection="actionRequestStatuses" property="key" labelProperty="value"/>
  			  </html-el:select>    
	  	    </td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">* Priority:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].priority" size="60" /></td>
	  	  </tr>
	  	  <%--
	     <tr>
	  	    <td width="33%" align="right" class="thnormal">* Route Method Name:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].routeMethodName" /><br>
		       &nbsp;&nbsp;&nbsp;&nbsp;Route Module&nbsp; 
		       <c:set var="routeModules" value="${DocumentOperationForm.routeModules}" scope="request" />
               <html-el:select property="routeModuleName" onchange="document.forms[0].elements['actionRequests[${ctr}].routeMethodName'].value=this.value" >
                 <html-el:options collection="routeModules" labelProperty="value" property="key" filter="false"/>
               </html-el:select><br>	                
               &nbsp;&nbsp;&nbsp;&nbsp;Rule Template&nbsp;<html-el:image property="methodToCall.performLookup" src="images/searchicon.gif" onclick="javascript:configureLookup('RuleTemplateLookupableImplService', 'ActionRequest', 'routeMethodName', '${ctr}');" />		                
  	        </td>
		  </tr>--%>
		  <tr>
		    <td width="33%" align="right" class="thnormal">* Route Level:</td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].routeLevel" /></td>
	      </tr>
	      <tr>
	  	    <td width="33%" align="right" class="thnormal">* Responsibility ID:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].responsibilityId" /></td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Responsibility Description: </td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].responsibilityDesc" /></td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Action Request Parent ID:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].parentActionRequestId" /></td>
		  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Recipient Type:</td>
	  	    <td width="66%" class="datacell">
	  	      <html-el:select property="actionRequests[${ctr}].recipientTypeCd" value="${actionRequest.recipientTypeCd}">
    		    <c:set var="actionRequestRecipientTypes" value="${DocumentOperationForm.actionRequestRecipientTypes}"/>
    		    <html-el:options collection="actionRequestRecipientTypes" property="key" labelProperty="value"/>
  			  </html-el:select> 
	  	    <%-- <html-el:text property="actionRequests[${ctr}].recipientTypeCd" />--%>
	  	    
	  	    </td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Person ID:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].principalId" />
            <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalId:actionRequests[${ctr}].principalId"
                lookupParameters="actionRequests[${ctr}].principalId:principalId"/>
	  	    </td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Workgroup ID:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].groupId" />
            <kul:lookup boClassName="org.kuali.rice.kim.impl.group.GroupBo" fieldConversions="id:actionRequests[${ctr}].groupId" lookupParameters="actionRequests[${ctr}].groupId:id"/>
          </td>
		  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Role Name:</td>
 	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].roleName"/>
             <kul:lookup boClassName="org.kuali.rice.kim.impl.role.RoleBo" fieldConversions="name:actionRequests[${ctr}].roleName" lookupParameters="actionRequests[${ctr}].roleName:name" />
           </td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Qualified Role Name:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].qualifiedRoleName" /></td>
	  	  </tr>
	  	  <tr>
	  	    <td width="33%" align="right" class="thnormal">Qualified Role Label:</td>
	  	    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].qualifiedRoleNameLabel" /></td>
	  	  </tr>	  
		  <tr>
		    <td width="33%" align="right" class="thnormal">Action Taken ID:</td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].actionTakenId" /></td>
		  </tr>
		  <tr>
		    <td width="33%" align="right" class="thnormal">Force Action: </td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].forceAction" /></td>
		  </tr>
		  <tr>
		    <td width="33%" align="right" class="thnormal">Approve Policy: </td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].approvePolicy" /></td>
		  </tr>
		  <tr>
		    <td width="33%" align="right" class="thnormal">Delegation Type: </td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].delegationTypeCode" /></td>
		  </tr>     
		  <tr>
		    <td width="33%" align="right" class="thnormal">Current Indicator: </td>
		    <td width="66%" class="datacell"><html-el:text property="actionRequests[${ctr}].currentIndicator" /></td>
		  </tr>
		  <tr>
		    <td width="33%" align="right" class="thnormal">Annotation: </td>
		    <td width="66%" class="datacell"><html-el:textarea cols="120" rows="1" property="actionRequests[${ctr}].annotation" /></td>
		  </tr> 
		  
		</logic-el:iterate>
	
		  </c:otherwise>
		 </c:choose> 
	  </table>
    </td>
    <td width="20" height="30">&nbsp;</td>
  </tr>
