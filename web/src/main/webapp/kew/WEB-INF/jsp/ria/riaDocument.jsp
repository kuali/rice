<%--
 Copyright 2005-2006 The Kuali Foundation.

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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="riaAttributes" value="${DataDictionary.RIADocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />
<kul:documentPage
	showDocumentInfo="true"
	htmlFormAction="riaDocument"
	documentTypeName="RIADocument"
	docTitle="${KualiForm.documentType.label}"
	alternativeHelp="${KualiForm.riaDocTypeMap.helpUrl}"
	renderMultipart="true"
	showTabButtons="true"
	auditCount="0">
 	<kul:hiddenDocumentFields />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />	
	
	<kul:htmlControlAttribute
				property="document.riaDocTypeName"
				attributeEntry="${riaAttributes.riaDocTypeName}"
				readOnly="false" />
				
	<!-- TODO figure out how to register hidden type -->
	<input type="hidden" name="document.xmlContent" maxlength="5000" size="9" tabindex="0" value='<c:out value="${KualiForm.document.xmlContent}" escapeXml="false" />' id="xmlContent" />	
	<kul:htmlControlAttribute property="document.xmlContent" attributeEntry="${riaAttributes.xmlContent}" readOnly="false" styleClass="hidden" />
	<kul:tab tabTitle="Document Content" defaultOpen="true" tabErrorKey="" >
		<div class="tab-container" align="center">	
			<table id="riaContainerTable" width="100%" height="1000px" style="overflow:hidden">
			<tr>
				<td width="*" style="overflow:none; height:100%; overflow:hidden;">
					<div id="ria_container" style="width:100%; height:100%; overflow:hidden; margin:0px auto; padding:0px;">
					    <!-- RIA gets loaded here --> 
					</div>
				</td>
			</tr>
			</table>
			<table id="errorContainer" width="100%" style="display:none">
				<tr>
					<td>
						<p id="errorMessage"></p>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="false" />
	<script type="text/javascript" src="scripts/ria/jquery-1.4.1.min.js"></script>
	<script type="text/javascript" src="scripts/ria/ria.js"></script>
	<script type="text/javascript">	
		jQuery(function() {
			jQuery.noConflict();
			ria.init('<c:out value="${KualiForm.riaDocTypeMap.riaUrl}" />', 'xmlContent', 'riaContainerTable', 'errorContainer', 'errorMessage', '<c:out value="${readOnly}" />');
		});
	</script>
</kul:documentPage>