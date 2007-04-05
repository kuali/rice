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
	<script type="text/javascript">
	    var kualiForm = document.forms['KualiForm'];
	    var kualiElements = kualiForm.elements;

		function lookupHelper(name,val) {
		    var list=document.getElementsByName(name);
		    if (list.length==0) return 0;
		    var item=list.item(0);
			for (i=0; i<item.options.length; i++) {
				if (item.options[i].value==val) return i;
			}
			return 0;
		}
	
		function lookupLHS(val) {
			return lookupHelper("document.newMaintainableObject.chartOfAccountsCode",val);
		}
	
		function lookupRHS(val) {
			return lookupHelper("document.newMaintainableObject.reportsToChartOfAccountsCode",val);
		}
	
		function setReportsToChartCode() {
		    var coa;
		    var reportsTo;
		    var chart = new Array(14);
		  	    <c:forEach items="${KualiForm.reportsToHierarchy}" var="select">
				   chart[lookupLHS("<c:out value="${select.key}"/>")]=lookupRHS("<c:out value="${select.value}"/>"); // ${select.key}-->${select.value}
				</c:forEach>
	
		    var list=document.getElementsByName("document.newMaintainableObject.chartOfAccountsCode");
		    if (list.length>0) {
			    coa=list.item(0);
			    list=document.getElementsByName("document.newMaintainableObject.reportsToChartOfAccountsCode");
			    if (list.length>0) {
				    reportsTo=list.item(0);
				    var newValue=chart[coa.selectedIndex];
				    if (reportsTo.selectedIndex!=newValue) {
				        reportsTo.selectedIndex=newValue;
				        reportsToCode=document.getElementsByName("document.newMaintainableObject.reportsToFinancialObjectCode").item(0);
				        if (reportsToCode.value.length>0) {
				        		alert("Reports to chart of accounts was updated. Reports to Object Code must be entered again");
				        		reportsToCode.value="";
				        }
				    }
		     	    reportsTo.setAttribute("onclick","setReportsToChartCode();"); //disallow subsequent changes
			    }
			}
		}
	</script>
</kul:maintenanceDocument>