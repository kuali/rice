<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="documentTypeAttributes" value="${DataDictionary.DocumentType.attributes}" />
<c:set var="permissionAttributes" value="${DataDictionary.PermissionImpl.attributes}" />
<c:set var="responsibilityAttributes" value="${DataDictionary.ResponsibilityImpl.attributes}" />
<c:set var="documentType" value="${KualiForm.documentType}" />
<c:set var="attributeLabels" value="${KualiForm.attributeLabels}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributes.attributes}" />

<style type="text/css">
tr.overridden td {
    text-decoration: line-through;
    color: #909090;
}
tr.overridden td a {
    color: #909090;
}
</style>

<kul:page headerTitle="Document Configuration - ${documentType.name}" transactionalDocument="false"
	showDocumentInfo="false" htmlFormAction="DocumentConfigurationView" docTitle="Document Configuration - ${documentType.name}">
    <c:if test="${empty documentType}">
        Unknown Document Type - <c:out value="${KualiForm.documentTypeName}" />
    </c:if>
<%--
    TODO: remove hard coded KIM class Impl names - if anything, redirect to the action to allow the code the make the
    determination of how to implement
    TODO: some attributes need to 
--%>
    <c:if test="${!empty documentType}">
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
                	<kul:inquiry boClassName="org.kuali.rice.kew.doctype.bo.DocumentType" 
                	             keyValues="documentTypeId=${documentType.documentTypeId}" render="true">
    	                <kul:htmlControlAttribute attributeEntry="${documentTypeAttributes.name}"
    	                	property="documentType.name"
    	                	readOnly="true" />         
	                </kul:inquiry>
	                <c:if test="${KualiForm.canInitiateDocumentTypeDocument}">
	                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                	<a href="<c:url value="${ConfigProperties.kr.url}/${Constants.MAINTENANCE_ACTION}">
                      <c:param name="methodToCall" value="edit" />
                      <c:param name="businessObjectClassName" value="org.kuali.rice.kew.doctype.bo.DocumentType"/>
                      <c:param name="documentTypeId" value="${documentType.documentTypeId}"/>
                      <c:param name="name" value="${documentType.name}"/>
                    </c:url>" target="_blank">Edit Document Type</a>       
                    </c:if>
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
                	&nbsp;
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
                	&nbsp;
                </td>
			  </tr>
			</tbody>
	 	  </table>
	 	</div>
   	    <kul:tab tabTitle="Permissions" defaultOpen="true">
			<div class="tab-container" style="width:auto;">
			 <%-- loop over the document types, going up the hierarchy --%>
                <c:forEach var="permDocTypeName" items="${KualiForm.docTypeHierarchyList}">
				  <c:set var="permissions" value="${KualiForm.permissionsByDocumentType[permDocTypeName]}" />
				  <c:choose>
  				    <c:when test="${permDocTypeName == documentType.name}">
				      <c:set var="tabLabel" value="Defined For This Document" />
				    </c:when>
				    <c:otherwise>
				      <c:set var="tabLabel" value="Inherited From: ${permDocTypeName}" />
				    </c:otherwise>
				  </c:choose>				  
	              <kul:subtab width="100%" subTabTitle="${tabLabel}" noShowHideButton="false">
	                <table class="datatable" cellspacing="0" cellpadding="0" align="center" style="text-align: left; margin-left: auto; margin-right: auto; padding-left: 5em;">
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
                           	<c:if test="${KualiForm.canInitiatePermissionDocument}">
	        	                <th>
	                                <a href="<c:url value="${ConfigProperties.kr.url}/${Constants.MAINTENANCE_ACTION}">
		                                <c:param name="methodToCall" value="Constants.MAINTENANCE_NEWWITHEXISTING_ACTION" />
		                                <%-- TODO: replace this class name with the interface or maintenance class and let module service handle --%>
		                                <c:param name="businessObjectClassName" value="org.kuali.rice.kim.bo.role.impl.KimPermissionImpl"/>
		                                <%-- TODO: replace hard-coding of attribute ID with lookup stored on form --%>
		                                <c:param name="detailObjects[0].kimAttributeId" value="13"/>
		                                <c:param name="detailObjects[0].attributeValue" value="${permDocTypeName}"/>
	                                </c:url>" target="_blank">Add Permission</a>
	        	                </th>
                            </c:if>
        	              </tr>
        	            
        				<c:forEach var="perm" items="${permissions}">
                          <tr <c:if test="${perm.overridden}">class="overridden"</c:if>>
                            <td>
                            	<%-- TODO: update this to use the proper url for an inquiry and not use the impl class --%>
                                <kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl" 
                                			 keyValues="permissionTemplateId=${perm.template.permissionTemplateId}" render="true">
                                <c:out value="${perm.template.name}" />
                                (<c:out value="${perm.template.namespaceCode}" />)
                                </kul:inquiry>
                            </td>
                            <td>
                            	<%-- TODO: update this to use the proper url for a detailed inquiry and not use the impl class --%>
                                <kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimPermissionImpl" 
                                			 keyValues="permissionId=${perm.permissionId}" render="true">
	                                <c:if test="${empty perm.name}">
	                                    <c:out value="${perm.template.name}" />
	                                </c:if>
	                                <c:out value="${perm.name}" />
	                                (<c:out value="${perm.namespaceCode}" />)
                                </kul:inquiry>
                            </td>
                            <td>
                            	<%-- skip output of the documentTypeName since in subhead --%>
                                <c:forEach var="dtl" items="${perm.details}" varStatus="status">
                                	<c:if test="${dtl.key != 'documentTypeName'}">
                                    	<c:if test="${status.index != 0}"><br /></c:if>
                                    	<c:out value="${attributeLabels[dtl.key]} = ${dtl.value}" />
                                    </c:if>
                                </c:forEach>
                                &nbsp;
                            </td>
                            <td>
                            	<c:forEach var="role" items="${KualiForm.permissionRoles[perm.permissionId]}">
                            		<kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimRoleImpl" 
                            					 keyValues="roleId=${role.roleId}" render="true">
                            			<c:out value="${role.namespaceCode} ${role.roleName}" />
                            		</kul:inquiry>
                            		<br />
                            	</c:forEach>
			                	&nbsp;
                            </td>
                        	<c:if test="${KualiForm.canInitiatePermissionDocument}">
	                            <td>
                                    <a href="<c:url value="${ConfigProperties.kr.url}/${Constants.MAINTENANCE_ACTION}">
		                                <c:param name="methodToCall" value="edit" />
		                                <c:param name="businessObjectClassName" value="org.kuali.rice.kim.bo.role.impl.KimPermissionImpl"/>
		                                <c:param name="permissionId" value="${perm.permissionId}"/>
	                                </c:url>" target="_blank">Edit Permission</a>
		                        </td>
                            </c:if>
                          </tr>
        				</c:forEach>
        	            </tbody>
        	          </table>
    	          </kul:subtab>
	          </c:forEach>
			</div> 	  
 	    </kul:tab>

 	    <kul:tab tabTitle="Workflow / Responsibilities" defaultOpen="true" >
 	      <%-- TODO: need separate section for the exception routing --%>
 	  		<div class="tab-container" style="width:auto;">
				<c:forEach var="node" items="${KualiForm.routeNodes}">
				  <c:if test="${node.roleNode}">
				  <c:set var="responsibilities" value="${KualiForm.responsibilityMap[node.routeNodeName]}" />
	              <kul:subtab width="100%" subTabTitle="Route Node: ${node.routeNodeName}" noShowHideButton="true">
        	          <table class="datatable" cellspacing="0" cellpadding="0" align="center" style="text-align: left; margin-left: auto; margin-right: auto; padding-left: 5em;">
        	            <tbody>
        	              <tr>
        	                <th style="width: 8em;">
        	                	<kul:htmlAttributeLabel attributeEntry="${kimAttributes.required}" noColon="true" />
       	                	</th>
        	                <th style="width: 11em;">
        	                	<kul:htmlAttributeLabel attributeEntry="${kimAttributes.actionDetailsAtRoleMemberLevel}" noColon="true" />
       	                	</th>
        	                <th>
                               <kul:htmlAttributeLabel attributeEntry="${responsibilityAttributes.assignedToRolesToDisplay}" noColon="true" />
                            </th>
        	                <th style="width: 20em;">Inherited</th>
       	                	<c:if test="${KualiForm.canInitiateResponsibilityDocument}">
	        	                <th style="width: 12em;">
	                                <a href="<c:url value="${ConfigProperties.kr.url}/${Constants.MAINTENANCE_ACTION}">
		                                <c:param name="methodToCall" value="Constants.MAINTENANCE_NEWWITHEXISTING_ACTION" />
		                                <%-- TODO: replace this class name with the interface or maintenance class and let module service handle --%>
		                                <c:param name="businessObjectClassName" value="org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl"/>
		                                <%-- TODO: replace hard-coding of attribute IDs with lookup stored on form --%>
		                                <c:param name="detailObjects[0].kimAttributeId" value="13"/>
		                                <c:param name="detailObjects[0].attributeValue" value="${documentType.name}"/>
		                                <c:param name="detailObjects[1].kimAttributeId" value="16"/>
		                                <c:param name="detailObjects[1].attributeValue" value="${node.routeNodeName}"/>
	                                </c:url>" target="_blank">Add Responsibility</a>
	        	                </th>
                            </c:if>	                
        	              </tr>
        	              <c:forEach var="resp" items="${responsibilities}">
        	                 <tr <c:if test="${resp.overridden}">class="overridden"</c:if>>
        	                    <td>                                	
                                	<c:choose>
                                	   <c:when test="${resp.details['required']}">Yes</c:when>
                                	   <c:otherwise>No</c:otherwise>
                                	</c:choose>
                                	<c:if test="${resp.overridden}"></del></c:if>
                                </td>
                                <td>
                                	<c:choose>
                                	   <c:when test="${resp.details['actionDetailsAtRoleMemberLevel']}">Yes</c:when>
                                	   <c:otherwise>No</c:otherwise>
                                	</c:choose>
                                </td>
                                <td>
                                	<c:forEach var="role" items="${KualiForm.responsibilityRoles[resp.responsibilityId]}">
                                		<kul:inquiry boClassName="org.kuali.rice.kim.bo.role.impl.KimRoleImpl" keyValues="roleId=${role.roleId}" render="true">
                                    		<c:out value="${role.namespaceCode} ${role.roleName}" />
                                		</kul:inquiry>
                                		<br />
                                	</c:forEach>
				                	&nbsp;
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${resp.details.documentTypeName == documentType.name}">
                                            No
                                        </c:when>
                                        <c:otherwise>
                                        	<a href="?documentTypeName=${resp.details.documentTypeName}"><c:out value="${resp.details.documentTypeName}" /></a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                               	<c:if test="${KualiForm.canInitiateResponsibilityDocument}">
	                                <td>
	                                    <a href="<c:url value="${ConfigProperties.kr.url}/${Constants.MAINTENANCE_ACTION}">
		                                    <c:param name="methodToCall" value="edit" />
		                                    <c:param name="businessObjectClassName" value="org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl"/>
		                                    <c:param name="responsibilityId" value="${resp.responsibilityId}"/>
	                                    </c:url>" target="_blank">Edit Responsibility</a>
	                                </td>
                                </c:if>
        	                 </tr>
        	              </c:forEach>
        	            </tbody>
        	          </table>
	              </kul:subtab>	              
                 </c:if>
	            </c:forEach>
 	  		</div>
 	    </kul:tab>
 	  <kul:panelFooter />
	  </kul:tabTop>
  </c:if>
	
</kul:page>