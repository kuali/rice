<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />


	<kul:tab tabTitle="Contact" defaultOpen="true" tabErrorKey="document.*">
	<div class="tab-container" align="center">
    	<h3>
    		<span class="subhead-left">Contact</span>
        </h3>
		<kim:personName />
		<kim:personAddress />
		<kim:personPhone />
		<kim:personEmail />
		
		</div>
	</kul:tab>
