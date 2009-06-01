<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />

<kul:tab tabTitle="Membership" defaultOpen="false" tabErrorKey="document.groups*,document.roles*,document.dele*,newGroup.*,newRole.*,newDel.*">
	<div class="tab-container" align="center">
		<kim:personGroup />
		<kim:personRole />
		<kim:personDelegations />
	</div>
</kul:tab>
