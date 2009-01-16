<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="roleAttributes" value="${DataDictionary.KimRoleImpl.attributes}" />
<c:set var="roleTypeAttributes" value="${DataDictionary.KimTypeImpl.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

	<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${inquiry}" tabErrorKey="document.perm*,document.resp*,document.assign*,document.active,document.delegations*">

	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Overview</span>
        </h3>

		<table cellpadding=0 cellspacing=0 summary=""> 
		 	<tr>
        				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.roleId}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleId" attributeEntry="${roleAttributes.roleId}" readOnly="true" /></td>
        				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleTypeAttributes.name}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.kimType.name" attributeEntry="${roleTypeAttributes.name}" readOnly="true" /></td>
		 	</tr>
		 	<tr>
        				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.namespaceCode}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleNamespace" attributeEntry="${roleAttributes.namespaceCode}" readOnly="true" /></td>
        				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.roleName}"  /></div></th>
		 		<td><kul:htmlControlAttribute property="document.roleName" attributeEntry="${roleAttributes.roleName}" readOnly="true" /></td>
		 	</tr>
		 	<tr>
        				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.active}"  /></div></th>
		 		<td colspan="3" ><kul:htmlControlAttribute property="document.active" attributeEntry="${roleAttributes.active}" /></td>
		 	</tr>
		</table> 
	
		</div>
	</kul:tab>

