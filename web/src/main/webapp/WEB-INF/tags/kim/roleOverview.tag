<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="roleAttributes" value="${DataDictionary.KimRoleImpl.attributes}" />
<c:set var="roleTypeAttributes" value="${DataDictionary.KimTypeImpl.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${readOnly}">
	<c:set var="inquiry" value="${readOnly}"/>
</c:if>

	<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${inquiry}" tabErrorKey="document.role*,document.active">

	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Overview</span>
        </h3>

		<table cellpadding=0 cellspacing=0 summary=""> 
		 	<tr>
    			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.roleId}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleId" attributeEntry="${roleAttributes.roleId}" readOnly="${readOnly}" /></td>
        		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleTypeAttributes.name}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleTypeName" attributeEntry="${roleTypeAttributes.name}" readOnly="${readOnly}" /></td>
		 		<html:hidden property="document.roleTypeId" />
		 	</tr>
		 	<tr>
        		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.namespaceCode}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleNamespace" attributeEntry="${roleAttributes.namespaceCode}" readOnly="${readOnly}" /></td>
        		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.roleName}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleName" attributeEntry="${roleAttributes.roleName}" readOnly="${readOnly}" /></td>
		 	</tr>
		 	<tr>
   				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.active}"  /></div></th>
		 		<td colspan="3" ><kul:htmlControlAttribute property="document.active" attributeEntry="${roleAttributes.active}" readOnly="${readOnly}" /></td>
		 	</tr>
		</table> 
	
		</div>
	</kul:tab>

