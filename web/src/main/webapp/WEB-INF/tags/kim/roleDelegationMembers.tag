<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="delegationIdx" required="true" %>
<c:set var="delegation" value="${KualiForm.document.delegations[delegationIdx]}"/>

<c:set var="delegationAttributes" value="${DataDictionary.KimDelegationImpl.attributes}" />
<c:set var="roleQualifierAttributes" value="${DataDictionary.KimDocumentRoleQualifier.attributes}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<script type="text/javascript">
function changeDelegationTypeCode(){
	document.getElementById("command").value="changeDelegationTypeCode";
	document.forms[0].submit();
}
</script>
<kul:tab tabTitle="Delegations" defaultOpen="true">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Delegations</span>
    </h3>
    
    <table cellpadding="0" cellspacing="0" summary="">
        	<tr>
        		<th><div align="left">&nbsp;</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.memberTypeCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.delegationName}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.activeFromDate}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationAttributes.activeToDate}" noColon="true" /></div></th>
				<c:if test="${not inquiry}">	
            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
				</c:if>	
        	</tr>     
          <c:if test="${not inquiry}">	
             <tr>
				<th class="infoline">Add:</th>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<input type="hidden" name="command" id="command" />
                	<kul:htmlControlAttribute property="delegation.member.memberTypeCode" 
                	attributeEntry="${delegationAttributes.memberTypeCode}" 
                	onchange="changeDelegationTypeCode()" />
					<NOSCRIPT>
   						<html:submit value="select" alt="press this button to refresh the page after changing the delegation type." />
					</NOSCRIPT>                
	            </div>
            	<c:set var="bo" value="${KualiForm.memberBusinessObjectName}"/>
            	<c:set var="fc" value="${KualiForm.memberFieldConversions}"/>
				</td>
                <td class="infoline">   
                <div align="center">             	
					<kul:htmlControlAttribute property="delegation.member.memberName" attributeEntry="${delegationAttributes.memberName}" />
	               	<kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
					<html:hidden property="delegation.member.memberId" />
				</div>
				</td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="delegation.member.activeFromDate" attributeEntry="${delegationAttributes.activeFromDate}" datePicker="true" />
                </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="delegation.member.activeToDate" attributeEntry="${delegationAttributes.activeToDate}" datePicker="true" />
                </div>
                </td>
				<c:set var="numberOfQualifiers" value="${KualiForm.document.members[statusMember.index].numberOfQualifiers}" />
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
        			<c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
        			<c:choose>
        				<c:when test="${numberOfQualifiers>statusQualifier.index}">
				            <td align="left" valign="middle">
				               	<div align="left"> <kul:htmlControlAttribute property="delegation.member.qualifier[${qualifier.kimAttributeId}].attrVal"  attributeEntry="${attrEntry}" readOnly="${inquiry}"  />
								</div>
							</td>
						</c:when>
					</c:choose>
		        </c:forEach>
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addDelegationMember.anchor${tabKey}"
						src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
                </td>
    	   </tr>         
		</c:if>

      	<c:forEach var="member" items="${KualiForm.document.delegation.members}" varStatus="statusMember">
            <tr>
				<th rowspan="1" class="infoline" valign="top">
					<c:out value="${statusMember.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegation.members[${statusMember.index}].memberTypeCode"  attributeEntry="${delegationAttributes.memberTypeCode}" disabled="true" readOnly="false" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegation.members[${statusMember.index}].memberName"  attributeEntry="${delegationAttributes.memberName}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegation.members[${statusMember.index}].activeFromDate"  attributeEntry="${delegationAttributes.activeFromDate}" readOnly="${inquiry}"  datePicker="true" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.delegation.members[${statusMember.index}].activeToDate"  attributeEntry="${delegationAttributes.activeToDate}" readOnly="${inquiry}"  datePicker="true" />
					</div>
				</td>
				<c:set var="numberOfQualifiers" value="${KualiForm.document.delegation.members[statusMember.index].numberOfQualifiers}" />
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
        			<c:choose>
        				<c:when test="${numberOfQualifiers>statusQualifier.index}">
				            <td align="left" valign="middle">
				               	<div align="left"><kul:htmlControlAttribute property="document.delegation.members[${statusMember.index}].qualifier[${qualifier.kimAttributeId}].attrVal"  attributeEntry="${attrEntry}" readOnly="${inquiry}"  />
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
			<c:if test="${not inquiry}">	
				<td>
					<div align="center">&nbsp;
						<c:choose>
							<c:when test="${role.edit}">
	        	          		<img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
							</c:when>
	        	       		<c:otherwise>
	        	        		<html:image property='methodToCall.deleteDelegationMember.line${status.index}.anchor${currentTabIndex}'
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