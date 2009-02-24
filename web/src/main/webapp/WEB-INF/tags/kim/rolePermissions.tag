<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="permissionAttributes" value="${DataDictionary.PermissionImpl.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${readOnly}">
	<c:set var="inquiry" value="${readOnly}"/>
</c:if>

<kul:tab tabTitle="Permissions" defaultOpen="true" tabErrorKey="document.perm*">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Permissions</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
          <c:if test="${not inquiry}">	
          	
             <tr>
				<td align="center">
	                <div align="center">
	                	<br/>
						<strong><c:out value="Add Permission ID:" /></strong>
						<kul:htmlControlAttribute property="permission.permissionId" attributeEntry="${permissionAttributes.permissionId}"/>
	                	<kul:lookup boClassName="org.kuali.rice.kim.bo.impl.PermissionImpl" fieldConversions=
	                	"template.name:permission.kimPermission.template.name,permissionId:permission.permissionId,name:permission.kimPermission.name,namespaceCode:permission.kimPermission.namespaceCode" anchor="${tabKey}" />
						<html:hidden property="permission.kimPermission.name" />
						<html:hidden property="permission.kimPermission.namespaceCode" />
						${KualiForm.permission.kimPermission.namespaceCode}  ${KualiForm.permission.kimPermission.nameToDisplay}&nbsp;
	                	<br/>
	                	<br/>
		            </div>
				</td>
			</tr>
			<tr>                                
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addPermission.anchor${tabKey}"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
                </td>
	       </tr>         
     </c:if>       
	</table>
	<table>
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${permissionAttributes.namespaceCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${permissionAttributes.permissionId}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${permissionAttributes.name}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${permissionAttributes.detailObjectsValues}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${permissionAttributes.requiredRoleQualifierAttributesToDisplay}" noColon="true" /></div></th>
				<c:if test="${not inquiry}">	
            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
				</c:if>	
        	</tr>     
      	<c:forEach var="permission" items="${KualiForm.document.permissions}" varStatus="status">
            <tr>
				<th rowspan="1" class="infoline">
					<c:out value="${status.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.permissions[${status.index}].kimPermission.namespaceCode"  attributeEntry="${permissionAttributes.namespaceCode}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.permissions[${status.index}].kimPermission.permissionId"  attributeEntry="${permissionAttributes.permissionId}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.permissions[${status.index}].kimPermission.nameToDisplay"  attributeEntry="${permissionAttributes.name}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.permissions[${status.index}].kimPermission.detailObjectsValues"  attributeEntry="${permissionAttributes.detailObjectsToDisplay}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.permissions[${status.index}].kimPermission.requiredRoleQualifierAttributesToDisplay"  attributeEntry="${permissionAttributes.requiredRoleQualifierAttributesToDisplay}" readOnly="true"  />
					</div>
				</td>
			<c:if test="${not inquiry}">	
				<td>
					<div align=center>&nbsp;
						<c:choose>
							<c:when test="${permission.edit}">
	        	          		<img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
							</c:when>
	        	       		<c:otherwise>
	        	        		<html:image property='methodToCall.deletePermission.line${status.index}.anchor${currentTabIndex}'
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
		        	       	</c:otherwise>
	        	     	</c:choose>  
					</div>
				</td>
			</c:if>    
			</tr>
		</c:forEach>        
	</table>
	</div>
</kul:tab>
