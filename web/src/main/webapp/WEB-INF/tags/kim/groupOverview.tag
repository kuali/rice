<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="groupAttributes" value="${DataDictionary.GroupImpl.attributes}" />
<c:set var="groupTypeAttributes" value="${DataDictionary.KimTypeImpl.attributes}" />

<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${inquiry}" tabErrorKey="document.group*,document.active">

<div class="tab-container" align="center">
	<table cellpadding="0" cellspacing="0" summary=""> 
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.groupId}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupId" attributeEntry="${groupAttributes.groupId}" readOnly="true" /></td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTypeAttributes.name}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupTypeName" attributeEntry="${groupTypeAttributes.name}" readOnly="true" /></td>
	 		<html:hidden property="document.groupTypeId" />
	 	</tr>
	 	<tr>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.namespaceCode}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupNamespace" attributeEntry="${groupAttributes.namespaceCode}" readOnly="${readOnly}" /></td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.groupName}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupName" attributeEntry="${groupAttributes.groupName}" readOnly="${readOnly}" /></td>
	 	</tr>
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.active}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.active" attributeEntry="${groupAttributes.active}" readOnly="${readOnly}" /></td>
	 		<th>&nbsp;</th>
            <td>&nbsp;</td>
	 	</tr>
	</table> 
</div>
</kul:tab>

