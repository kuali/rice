<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docPrivacyAttributes" value="${DataDictionary.PersonDocumentPrivacy.attributes}" />


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
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressName" attributeEntry="${docPrivacyAttributes.suppressName}"  /></div></td>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressAddress}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressAddress" attributeEntry="${docPrivacyAttributes.suppressAddress}"  /></div></td>
				 		</tr>
				 		<tr>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPersonal}"  /></div></th>
				 		<td aligh="center" width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPersonal" attributeEntry="${docPrivacyAttributes.suppressPersonal}"  /></div></td>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressEmail}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressEmail" attributeEntry="${docPrivacyAttributes.suppressEmail}" /></div></td>
				 		</tr>
				 		<tr>
          				<th width="30%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${docPrivacyAttributes.suppressPhone}"  /></div></th>
				 		<td width="20%"><div align="center"><kul:htmlControlAttribute property="document.privacy.suppressPhone" attributeEntry="${docPrivacyAttributes.suppressPhone}" /></div></td>
				 		</tr>
		</table> 
		
		</div>
	</kul:tab>

