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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:maintenanceDocument>
	<script type="text/javascript"><!--
	    var kualiForm = document.forms['KualiForm'];
	    var kualiElements = kualiForm.elements;
	
		function setReportsToChartCode() {
		    var coa;
		    var reportsTo;
		    var chart = new Array(14);
		  	    <c:forEach items="${KualiForm.reportsToHierarchy}" var="select">
					if (document.getElementsByName("document.newMaintainableObject.chartOfAccountsCode").item(0).value == "<c:out value="${select.key}"/>") {
						document.getElementsByName("document.newMaintainableObject.reportsToChartOfAccountsCode").item(0).value = '<c:out value="${select.value}"/>';
						document.getElementsByName("document.newMaintainableObject.reportsToChartOfAccountsCode").item(0).disabled = true;
					}
				</c:forEach>
		}
	-->
	</script>
	
</kul:maintenanceDocument>