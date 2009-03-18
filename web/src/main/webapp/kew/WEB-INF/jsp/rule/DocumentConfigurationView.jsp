<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="documentType" value="${KualiForm.documentType}" />
<c:set var="documentTypeAttributes" value="${DataDictionary.DocumentType.attributes}" />

<kul:page headerTitle="Document Configuration" transactionalDocument="false"
	showDocumentInfo="false" htmlFormAction="DocumentConfigurationView" docTitle="Document Configuration">

	<kul:tabTop
	  tabTitle="Document Information"
	  defaultOpen="true">
	  	<div class="tab-container" style="width:auto;">
          <table class="datatable" cellspacing="0" cellpadding="0" align="center" style="text-align: left; margin-left: auto; margin-right: auto;">
            <tbody>
              <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left" 
                	attributeEntry="${documentTypeAttributes.name}" />
                <td>
	                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes.name}"
	                	property="documentType.name"
	                	readOnly="true" />                
                </td>
                <kul:htmlAttributeHeaderCell scope="col" align="left" 
                	attributeEntry="${documentTypeAttributes.unresolvedDocHandlerUrl}" />
                <td>
	                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes.unresolvedDocHandlerUrl}"
	                	property="documentType.docHandlerUrl"
	                	readOnly="true" />                
                </td>
			  </tr>
			  <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left" 
                	attributeEntry="${documentTypeAttributes.label}" />
                <td>
	                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes.label}"
	                	property="documentType.label"
	                	readOnly="true" />                
                </td>
                <kul:htmlAttributeHeaderCell scope="col" align="left" 
                	attributeEntry="${documentTypeAttributes.unresolvedHelpDefinitionUrl}" />
                <td>
	                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes.unresolvedHelpDefinitionUrl}"
	                	property="documentType.helpDefinitionUrl"
	                	readOnly="true" />                
                </td>
			  </tr>
			  <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left" 
                	attributeEntry="${documentTypeAttributes['parentDocType.name']}" />
                <td>
                	<c:if test="${!empty KualiForm.parentDocumentType.name}">
	                	<a href="?documentTypeName=${KualiForm.parentDocumentType.name}">
			                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes['parentDocType.name']}"
			                	property="parentDocumentType.name"
			                	readOnly="true" />                
	                		<c:if test="${fn:length( KualiForm.parentDocumentType.name ) <= 10}">
				                (<c:out value="${KualiForm.parentDocumentType.label}" />)                
	                		</c:if>
		                </a>
	                </c:if>
                </td>
                <th align="left" scope="col">
                	Child Document Types
                </th>
                <td>
                	<c:forEach var="childDocType" items="${KualiForm.childDocumentTypes}" varStatus="status">
                		<a href="?documentTypeName=${childDocType.name}"><c:out value="${childDocType.name}" />
                		<c:if test="${fn:length( childDocType.name ) <= 10}">
                			(<c:out value="${childDocType.label}" />)
                		</c:if>
                		</a><br />
                	</c:forEach>
                </td>
			  </tr>
			</tbody>
	 	  </table>
	 	</div>
   	    <kul:tab tabTitle="Permissions" defaultOpen="true">
			<div class="tab-container" style="width:auto;">
				<c:forEach var="perm" items="${KualiForm.permissions}">
					${perm}
				</c:forEach>
			</div> 	  
 	    </kul:tab>
 	    <kul:tab tabTitle="Workflow / Responsibilities" defaultOpen="true" >
 	  		<div class="tab-container" style="width:auto;">
 	  		</div>
 	    </kul:tab>
 	  <kul:panelFooter />
	  </kul:tabTop>
<%-- 
Document Summary Information

document type
document label
help URL (link)
service namespace
parent document
child documents? - drop-down
--%>

<%-- 
Permissions
--%>
	
	
<%-- 
Responsibilities
--%>
	
</kul:page>