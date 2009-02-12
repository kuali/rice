<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="responsibilityIdx" required="true" %>

<c:set var="responsibility" value="${KualiForm.document.responsibilities[responsibilityIdx]}"/>
<c:set var="docRoleRspActionAttributes" value="${DataDictionary.KimDocumentRoleResponsibilityAction.attributes}" />
<kul:subtab lookedUpCollectionName="roleRspActions" noShowHideButton="true" width="${tableWidth}" subTabTitle="Responsibility Action">
    <table cellpadding=0 cellspacing=0 summary="">
      	<tr>
            <th width="5%" rowspan=20 style=border-style:none>&nbsp;</th>
			<kul:htmlAttributeHeaderCell literalLabel="Name"  align="center"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.actionTypeCode}"  align="center"/>
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.priorityNumber}"  align="center" />
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.actionPolicyCode}"  align="center" />
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.ignorePrevious}"  align="center" />
       	</tr>
       	<c:set var="roleRspAction" value="${KualiForm.document.responsibilities[responsibilityIdx].roleRspAction }" />
      	<tr>	
			<td>
				<div align="center">
					${responsibility.roleRspAction.kimResponsibility.namespaceCode}
					${responsibility.roleRspAction.kimResponsibility.nameToDisplay}
	            </div>
       		</td>
			<td>
				<div align="center">
	            	<kul:htmlControlAttribute property="document.responsibilities[${responsibilityIdx}].roleRspActions[0].actionTypeCode"  attributeEntry="${docRoleRspActionAttributes.actionTypeCode}" readOnlyAlternateDisplay="${roleRspAction.actionTypeDescription}"/>
	            </div>
       		</td>
       		<td>
        		<div align="center">
	            	<kul:htmlControlAttribute property="document.responsibilities[${responsibilityIdx}].roleRspActions[0].priorityNumber"  attributeEntry="${docRoleRspActionAttributes.priorityNumber}" />
        		</div>
       		</td>
       		<td>
        		<div align="center">
	            	<kul:htmlControlAttribute property="document.responsibilities[${responsibilityIdx}].roleRspActions[0].actionPolicyCode"  attributeEntry="${docRoleRspActionAttributes.actionPolicyCode}" readOnlyAlternateDisplay="${roleRspAction.actionPolicyDescription}"/>
        		</div>
       		</td>
       		<td>
        		<div align="center">
	            	<kul:htmlControlAttribute property="document.responsibilities[${responsibilityIdx}].roleRspActions[0].ignorePrevious"  attributeEntry="${docRoleRspActionAttributes.ignorePrevious}" />
        		</div>
       		</td>
	    </tr>
		<tr>
            <td colspan=7 style="padding:0px; border-style:none; height:22px; background-color:#F6F6F6">&nbsp;</td>
        </tr>		        	
	</table>
</kul:subtab>