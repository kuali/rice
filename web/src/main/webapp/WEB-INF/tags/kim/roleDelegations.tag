<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="delegationAttributes" value="${DataDictionary.KimDelegationImpl.attributes}" />
<c:set var="roleQualifierAttributes" value="${DataDictionary.KimDocumentRoleQualifier.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${readOnly}">
	<c:set var="inquiry" value="${readOnly}"/>
</c:if>

<kul:tab tabTitle="Delegations" defaultOpen="true" tabErrorKey="document.deleg*">
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
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
		        </c:forEach>
				<c:if test="${not inquiry}">	
            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
				</c:if>	
        	</tr>     
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

			</tr>
		</c:forEach>        
	</table>
	</div>
</kul:tab>
