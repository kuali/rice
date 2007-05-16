<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/kr/WEB-INF/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/kr/WEB-INF/tlds/fn.tld"%>
<%@ taglib uri="/kr/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/kr/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags/kr" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/kr/dd" prefix="dd"%>

<%@ attribute name="action" required="true"
		description="What's the action requested? Maintenance copy and edit actions get Old and New." %>
<%@ attribute name="colspan" required="true"
              description="What's the colspan for each header column?" %>
<%@ attribute name="depth" required="true"
		description="What level of recursion are we on?  Avoids putting 'New' on container contents." %>
<%--
	############################################################################################################################# 
	SHOW THE OLD/NEW BAR
	#############################################################################################################################
	--%>

<!--
<c:out value="action is ${action}, colspan is ${colspan}, <br/>" escapeXml="false" />
-->

<c:choose>

    <c:when test="${Constants.MAINTENANCE_COPY_ACTION eq action || Constants.MAINTENANCE_EDIT_ACTION eq action}">
        <c:set var="isCopyActionNotEditAction" value="${Constants.MAINTENANCE_COPY_ACTION eq action}"/>
		<td colspan="${colspan}" class="tab-subhead" width="50%">
			<div class="tab-subhead-r">
                ${isCopyActionNotEditAction ? 'Original' : 'Old'}
			</div>
		</td>
		<td colspan="${colspan}" class="tab-subhead" width="50%">
            ${isCopyActionNotEditAction ? 'New Copy' : 'New'}
		</td>
        </tr><tr>
	</c:when>
	
	<c:when test="${depth eq 0}">
		<%-- Show just one section header that goes all the way across. --%>
		<td colspan="${colspan}" class="tab-subhead">
			New
		</td>
        </tr><tr>
    </c:when>
    
    <c:otherwise>
        <%-- Show nothing. --%>
    </c:otherwise>
	
</c:choose>
