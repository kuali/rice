<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>
<%@ taglib tagdir="/WEB-INF/tags/portal/channel" prefix="channel" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>

<channel:portalChannelTop channelTitle="Feedback" />
<div class="body">
    <ul class="chan">
    	<c:choose>
	        <c:when test="${ConfigProperties.environment == 'ptd'}" >
	            <li><a class="portal_link" href="http://www.kualitestdrive.org/kualimanual.pdf" target="_blank">Download the Drivers Manual<img src="images-portal/pdf-small.gif" width="18" height="20" border="0" align="absbottom"/></a></li>
	            <li><a class="portal_link" href="mailto:kualitestdrive@oncourse.iu.edu" title="<bean:message key="app.feedback.linkText" />"><bean:message key="app.feedback.linkText" /></a></li>
	        </c:when>
	        <c:otherwise>
	            <li><a class="portal_link" href="<bean:message key="app.feedback.link"/>" target="_blank" title="<bean:message key="app.feedback.linkText" />"><bean:message key="app.feedback.linkText" /></a></li>
	        </c:otherwise>
        </c:choose>
    </ul>
    </div>
<channel:portalChannelBottom />