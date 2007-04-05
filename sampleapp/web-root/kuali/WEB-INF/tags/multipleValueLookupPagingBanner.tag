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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="pageNumber" required="true" description="The current page, starting from 0" %>
<%@ attribute name="totalPages" required="true" description="The number of pages" %>
<%@ attribute name="firstDisplayedRow" required="true" description="The first displayed row, indexed from 0" %>
<%@ attribute name="lastDisplayedRow" required="true" description="The last displayed row , indexed from 0" %>
<%@ attribute name="resultsActualSize" required="true" type="java.lang.Integer" description="The number of rows that would actually be returned if there is no results size limit" %>
<%@ attribute name="resultsLimitedSize" required="true" type="java.lang.Integer" description="The number of rows that that satisfy the criteria, or the limit of results rows, whichever is less" %>
<p>

<c:if test="${resultsActualSize gt resultsLimitedSize}">
	<%-- results were truncated off --%>
	${resultsActualSize} items found. ${resultsLimitedSize} items retrieved. Please refine your search criteria to narrow down your search.
	<br/>
	<br/>
</c:if>

<c:choose>
	<c:when test="${totalPages == 1}">
		Viewing rows ${firstDisplayedRow + 1} to ${lastDisplayedRow + 1}
	</c:when>
	<c:otherwise>
		Currently viewing page ${pageNumber + 1} of ${totalPages} (rows ${firstDisplayedRow + 1} to ${lastDisplayedRow + 1}).
		<br/><br/>
		Goto page: 
		<c:if test="${pageNumber != 0}">
			<c:forEach var="pageBeforeCurrent" begin="0" end="${pageNumber - 1}">
			    <input type="submit" name="methodToCall.switchToPage.<c:out value="${pageBeforeCurrent}"/>.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" value="<c:out value="${pageBeforeCurrent + 1}"/>"/>
			</c:forEach>
		</c:if>
		<c:out value="${pageNumber + 1}"/>
		<c:forEach var="pageAfterCurrent" begin="${pageNumber + 1}" end="${totalPages - 1}">
		    <input type="submit" name="methodToCall.switchToPage.<c:out value="${pageAfterCurrent}"/>.x" value="<c:out value="${pageAfterCurrent + 1}"/>"/>
		</c:forEach>		
	</c:otherwise>
</c:choose>
</p>
