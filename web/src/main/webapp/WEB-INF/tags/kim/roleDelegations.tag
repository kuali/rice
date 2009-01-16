<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="delegationAttributes" value="${DataDictionary.KimDelegationImpl.attributes}" />
<c:set var="roleQualifierAttributes" value="${DataDictionary.KimDocumentRoleQualifier.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Delegations" defaultOpen="true">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Delegations</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.kimTypeId}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.active}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.delegationTypeCode}" noColon="true" /></div></th>
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
      	<c:forEach var="delegation" items="${KualiForm.document.delegations}" varStatus="statusDelegation">
            <tr>
				<th rowspan="1" class="infoline">
					<c:out value="${statusDelegation.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegations[${statusDelegation.index}].kimTypeId"  attributeEntry="${delegationAttributes.kimTypeId}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegations[${statusDelegation.index}].active"  attributeEntry="${delegationAttributes.activeToDate}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegations[${statusDelegation.index}].delegationTypeCode"  attributeEntry="${delegationAttributes.delegationTypeCode}" readOnly="true"  />
					</div>
				</td>
				
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
					<c:set var="sizeq" value="${KualiForm.document.delegations[statusDelegation.index].numberOfQualifiers}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
        			<c:choose>
        				<c:when test="${KualiForm.document.delegations[statusDelegation.index].numberOfQualifiers>statusQualifier.index}">
				            <td align="left" valign="middle">
				               	<div align="left"> <kul:htmlControlAttribute property="document.delegations[${statusDelegation.index}].qualifiers[${statusQualifier.index}].attrVal"  attributeEntry="${attrEntry}" readOnly="true"  />
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
