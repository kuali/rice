<%--
 Copyright 2006-2007 The Kuali Foundation
 
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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="tabGroup" classname="org.kuali.rice.kns.uif.container.TabGroup"/>

<krad:group group="${group}" groupBodyIdSuffix="_tabGroup">

    <%-- render items through layout manager --%>
    <div id="${tabGroup.id}_tabs">
	  <%-- render items in list --%>
	  <ul id="${tabGroup.id}">
	    <c:forEach items="${tabGroup.items}" var="item">
	      <li>
	         <a href="#${item.id}_tab">${item.title}</a>
	      </li>
	    </c:forEach>
	  </ul>

      <c:forEach items="${tabGroup.items}" var="item">
	      <div id="${item.id}_tab">
	         <krad:template component="${item}"/>
	      </div>
	  </c:forEach>
	</div>

</krad:group>

<%-- render tabs widget --%>
<krad:template component="${tabGroup.tabsWidget}" parent="${tabGroup}"/>
