<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="documentTypeAttributes" value="${DataDictionary.DocumentType.attributes}" />
<c:set var="permissionAttributes" value="${DataDictionary.PermissionImpl.attributes}" />

<kul:page headerTitle="Document Configuration" transactionalDocument="false"
	showDocumentInfo="false" htmlFormAction="DocumentConfigurationView" docTitle="Document Configuration">

<%--
 TODO: link to edit document type, link to view document type?
--%>
	<kul:tabTop
	  tabTitle="Document Information"
	  defaultOpen="true">
        <c:set var="documentType" value="${KualiForm.documentType}" />
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
			 <%-- TODO: need bar here for add links --%>
	          <table class="datatable" cellspacing="0" cellpadding="0" align="center" style="text-align: left; margin-left: auto; margin-right: auto;">
	            <tbody>
	              <tr>
	                <kul:htmlAttributeHeaderCell scope="col" align="left" 
	                    attributeEntry="${permissionAttributes['template.name']}" />
                    <kul:htmlAttributeHeaderCell scope="col" align="left" 
                        attributeEntry="${permissionAttributes.name}" />
                    <kul:htmlAttributeHeaderCell scope="col" align="left" 
                        attributeEntry="${permissionAttributes.detailObjectsValues}" />
                    <kul:htmlAttributeHeaderCell scope="col" align="left" 
                        attributeEntry="${permissionAttributes.assignedToRolesToDisplay}" />
	                <th>Inherited</th>
	                <th>&nbsp;</th>
	              </tr>
	            
				<c:forEach var="perm" items="${KualiForm.permissions}">
                  <tr>
                    <td>
                    	<%-- TODO: update this to use the proper url for an inquiry and not use the impl class --%>
                        <kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl" keyValues="permissionTemplateId=${perm.template.permissionTemplateId}" render="true">
                        <c:out value="${perm.template.name}" />
                        (<c:out value="${perm.template.namespaceCode}" />)
                        </kul:inquiry>
                    </td>
                    <td>
                    	<%-- TODO: update this to use the proper url for a detailed inquiry and not use the impl class --%>
                        <kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimPermissionImpl" keyValues="permissionId=${perm.permissionId}" render="true">
                        <c:if test="${empty perm.name}">
                            <c:out value="${perm.template.name}" />
                        </c:if>
                        <c:out value="${perm.name}" />
                        (<c:out value="${perm.namespaceCode}" />)
                        </kul:inquiry>
                    </td>
                    <td>
                    	<%-- TODO: skip output of the documentTypeName since known external to this? --%>
                        <c:forEach var="dtl" items="${perm.details}" varStatus="status">
                        	<c:if test="${status.index != 0}">,</c:if>
                        	${dtl.value}
                        </c:forEach>
                    </td>
                    <td>
                    	<c:forEach var="role" items="${KualiForm.permissionRoles[perm.permissionId]}">
                    		<kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimRoleImpl" keyValues="roleId=${role.roleId}" render="true">
                    		<c:out value="${role.namespaceCode} ${role.roleName}" />
                    		</kul:inquiry>
                    		<br />
                    	</c:forEach>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${perm.details.documentTypeName == documentType.name}">
                                No
                            </c:when>
                            <c:otherwise>
                            	<a href="?documentTypeName=${perm.details.documentTypeName}"><c:out value="${perm.details.documentTypeName}" /></a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        Edit Permission
                    </td>
                  </tr>
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