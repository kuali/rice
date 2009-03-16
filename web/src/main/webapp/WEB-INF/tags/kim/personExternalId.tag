<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:subtab lookedUpCollectionName="externalId" width="${tableWidth}" subTabTitle="External Identifiers">      
        <table cellpadding=0 cellspacing=0 summary="">
          	<tr>
          		<th width="50%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.taxId}"  /></div></th>
				 <td><kul:htmlControlAttribute property="document.taxId" attributeEntry="${personAttributes.taxId}" readOnly="${readOnly}" /></td>
			</tr>
			<tr>
          		<th width="50%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${personAttributes.univId}"  /></div></th>
				 <td><kul:htmlControlAttribute property="document.univId" attributeEntry="${personAttributes.univId}" readOnly="${readOnly}"  /></td>          	
          	</tr>                 
        </table>
</kul:subtab>
