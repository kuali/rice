<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />


	<kul:tab tabTitle="Overview" defaultOpen="true" tabErrorKey="document.pr*,document.tax*,document.univ*,document.active,document.affiliations*">
	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Overview</span>
        </h3>
		<kul:subtab lookedUpCollectionName="overview" width="100%" subTabTitle="Overview">      
		<table cellpadding=0 cellspacing=0 summary=""> 
		    <tr>
        		<td colspan="4">
				 	<tr>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.entityId}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.entityId" attributeEntry="${personAttributes.entityId}" readOnly="true" /></td>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.principalId}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.principalId" attributeEntry="${personAttributes.principalId}" readOnly="true" /></td>
				 	</tr>
					<tr>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.principalName}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.principalName" attributeEntry="${personAttributes.principalName}" /></td>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.taxId}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.taxId" attributeEntry="${personAttributes.taxId}" /></td>
				 	</tr>
				 	<tr>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.password}"  /></div></th>
				 		<td><html:password property="document.password" /></td>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.univId}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.univId" attributeEntry="${personAttributes.univId}" /></td>          	
				 	</tr>
				 	<tr>
          				<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.active}"  /></div></th>
				 		<td><kul:htmlControlAttribute property="document.active" attributeEntry="${personAttributes.active}" /></td>
				 	</tr>
		</table> 
		</kul:subtab>
		<kim:personAffln />
		
		</div>
	</kul:tab>

