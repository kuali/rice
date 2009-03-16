<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

	<kul:tab tabTitle="Contact" defaultOpen="false" tabErrorKey="document.names*,document.phones*,newName.*,newPhone.*,document.addrs*,newAddr.*,document.emails*,newEmail.*">
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
