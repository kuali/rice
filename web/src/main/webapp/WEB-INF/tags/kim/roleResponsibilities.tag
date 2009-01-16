<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="responsibilityAttributes" value="${DataDictionary.KimResponsibilityImpl.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Responsibilities" defaultOpen="true">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Responsibilities</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${responsibilityAttributes.namespaceCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${responsibilityAttributes.name}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${responsibilityAttributes.detailObjectsValues}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${responsibilityAttributes.requiredRoleQualifierAttributes}" noColon="true" /></div></th>
				<c:if test="${not inquiry}">	
            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
				</c:if>	
        	</tr>     
<!--         <c:if test="${not inquiry}">	
			<tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>
	
	            <td align="left" valign="middle" class="infoline" colspan=3>
					<div align="center">
	              		<kul:htmlControlAttribute property="newRole.roleId" attributeEntry="${responsibilityAttributes.roleId}" disabled="true"/>
		              	<kul:lookup boClassName="org.kuali.rice.kim.bo.role.impl.KimRoleImpl" fieldConversions="roleId:newRole.roleId,kimTypeId:newRole.kimTypeId,roleName:newRole.roleName,namespaceCode:newRole.namespaceCode,kimRoleType.name:newRole.kimRoleType.name,kimRoleType.kimTypeServiceName:newRole.kimRoleType.kimTypeServiceName" anchor="${tabKey}" />
						${KualiForm.newRole.roleName}
						<html:hidden property="newRole.roleName" />
						<html:hidden property="newRole.namespaceCode" />
						<html:hidden property="newRole.kimTypeId" />
						<html:hidden property="newRole.kimRoleType.name" />
						<html:hidden property="newRole.kimRoleType.kimTypeServiceName" />
					</div>
				</td>
	            <td class="infoline">
					<div align=center>
						<html:image property="methodToCall.addRole.anchor${tabKey}"
						src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
	            </td>
			</tr>         
		</c:if>       
 -->
      	<c:forEach var="responsibility" items="${KualiForm.document.responsibilities}" varStatus="status">
            <tr>
				<th rowspan="1" class="infoline">
					<c:out value="${status.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.namespaceCode"  attributeEntry="${responsibilityAttributes.namespaceCode}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.name"  attributeEntry="${responsibilityAttributes.name}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.detailObjectsValues"  attributeEntry="${responsibilityAttributes.detailObjectsToDisplay}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.requiredRoleQualifierAttributesToDisplay"  attributeEntry="${responsibilityAttributes.requiredRoleQualifierAttributesToDisplay}" readOnly="true"  />
					</div>
				</td>
<!-- 	
				<c:if test="${not inquiry}">	
					<td>
						<div align=center>&nbsp;
							<c:choose>
								<c:when test="${role.edit}">
									<img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
								</c:when>
								<c:otherwise>
									<html:image property='methodToCall.deleteRole.line${status.index}.anchor${currentTabIndex}'
										src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
								</c:otherwise>
							</c:choose>  
						</div>
					</td>
				</c:if>     
 -->
			</tr>
		</c:forEach>        
	</table>
	</div>
</kul:tab>