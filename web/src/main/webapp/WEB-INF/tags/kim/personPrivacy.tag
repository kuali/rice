<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docPrivacyAttributes" value="${DataDictionary.PersonDocumentPrivacy.attributes}" />

<c:set var="canModifyPrivacyPreferences" scope="request" value="${KualiForm.canOverrideEntityPrivacyPreferences && !readOnly}" />

<kul:tab tabTitle="Privacy Preferences" defaultOpen="false" tabErrorKey="document.privacy*">
	<div class="tab-container" align="center">
		<table cellpadding="0" cellspacing="0" summary=""> 
	 		<tr>
  				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressName}"  /></div></th>
		 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressName" attributeEntry="${docPrivacyAttributes.suppressName}" readOnly="${!canModifyPrivacyPreferences}" /></div></td>
  				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressAddress}"  /></div></th>
		 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressAddress" attributeEntry="${docPrivacyAttributes.suppressAddress}" readOnly="${!canModifyPrivacyPreferences}" /></div></td>
	 		</tr>
	 		<tr>
  				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPersonal}"  /></div></th>
		 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPersonal" attributeEntry="${docPrivacyAttributes.suppressPersonal}" readOnly="${!canModifyPrivacyPreferences}" /></div></td>
  				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressEmail}"  /></div></th>
		 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressEmail" attributeEntry="${docPrivacyAttributes.suppressEmail}" readOnly="${!canModifyPrivacyPreferences}" /></div></td>
	 		</tr>
	 		<tr>
  				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPhone}"  /></div></th>
		 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPhone" attributeEntry="${docPrivacyAttributes.suppressPhone}" readOnly="${!canModifyPrivacyPreferences}" /></div></td>
		 		<th></th>
		 		<td></td>
	 		</tr>
		</table> 		
	</div>
</kul:tab>