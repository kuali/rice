<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docPrivacyAttributes" value="${DataDictionary.PersonDocumentPrivacy.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

	<kul:tab tabTitle="Privacy Preferences" defaultOpen="false" tabErrorKey="document.privacy*">
	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Privacy Preferences</span>
        </h3>
		<table cellpadding=0 cellspacing=0 summary=""> 
		    <tr>
        		<td colspan="4">
				 		<tr>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressName}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressName" attributeEntry="${docPrivacyAttributes.suppressName}" readOnly="${readOnly}" /></div></td>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressAddress}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressAddress" attributeEntry="${docPrivacyAttributes.suppressAddress}" readOnly="${readOnly}" /></div></td>
				 		</tr>
				 		<tr>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPersonal}"  /></div></th>
				 		<td aligh="center" width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPersonal" attributeEntry="${docPrivacyAttributes.suppressPersonal}" readOnly="${readOnly}" /></div></td>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressEmail}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressEmail" attributeEntry="${docPrivacyAttributes.suppressEmail}" readOnly="${readOnly}" /></div></td>
				 		</tr>
				 		<tr>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPhone}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPhone" attributeEntry="${docPrivacyAttributes.suppressPhone}" readOnly="${readOnly}" /></div></td>
				 		</tr>
		</table> 
		
		</div>
	</kul:tab>

