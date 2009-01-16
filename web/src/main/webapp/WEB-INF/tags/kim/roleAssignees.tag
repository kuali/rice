<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="roleMemberAttributes" value="${DataDictionary.KimDocumentRoleMember.attributes}" />
<c:set var="roleQualifierAttributes" value="${DataDictionary.KimDocumentRoleQualifier.attributes}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}" />

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Assignees" defaultOpen="true">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Assignees</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${roleMemberAttributes.memberTypeCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${roleMemberAttributes.memberName}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${roleMemberAttributes.activeFromDate}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${roleMemberAttributes.activeToDate}" noColon="true" /></div></th>
				<c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
        			<c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
<!-- >	        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${KualiForm.document.attributeEntry[fieldName]}" noColon="true" /></div></th> -->
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
		        </c:forEach>
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
      	<c:forEach var="member" items="${KualiForm.document.members}" varStatus="statusMember">
            <tr>
				<th rowspan="1" class="infoline">
					<c:out value="${statusMember.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberType"  attributeEntry="${roleMemberAttributes.memberTypeCode}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberName"  attributeEntry="${roleMemberAttributes.memberName}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeFromDate"  attributeEntry="${roleMemberAttributes.activeFromDate}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeToDate"  attributeEntry="${roleMemberAttributes.activeToDate}" readOnly="true"  />
					</div>
				</td>
				<c:set var="numberOfQualifiers" value="${KualiForm.document.members[statusMember.index].numberOfQualifiers}" />
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
        			<c:choose>
        				<c:when test="${numberOfQualifiers>statusQualifier.index}">
				            <td align="left" valign="middle">
				               	<div align="left"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].qualifiers[${statusQualifier.index}].attrVal"  attributeEntry="${attrEntry}" readOnly="true"  />
								</div>
							</td>
						</c:when>
        				<c:otherwise>
				            <td align="left" valign="middle">
				               	<div align="left">
								</div>
							</td>
						</c:otherwise>
					</c:choose>
		        </c:forEach>
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
