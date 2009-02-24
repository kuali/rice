<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<script language="JavaScript" src="scripts/en-common.js"></script>

<c:set var="delegationMemberAttributes" value="${DataDictionary.RoleDocumentDelegationMember.attributes}" />
<c:set var="roleDocumentDelegationMemberQualifier" value="${DataDictionary.RoleDocumentDelegationMemberQualifier.attributes}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="canAssignRole" value="${KualiForm.canAssignRole}" />
<c:if test="${!canAssignRole}">
	<c:set var="readOnly" value="${!canAssignRole}"/>
</c:if>

<script language="javaScript">
function changeDelegationMemberTypeCode(){
	document.getElementsByTagName("command").value="changeDelegationTypeCode";
	javaScript:document.forms[0].submit();
}
</script>
<kul:tab tabTitle="Assignees" defaultOpen="true" tabErrorKey="document.delegationMember*">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Delegations</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.memberTypeCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.memberId}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.memberName}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.activeFromDate}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.activeToDate}" noColon="true" /></div></th>
				<c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
        			<c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
		        </c:forEach>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${delegationMemberAttributes.delegationTypeCode}" noColon="true" /></div></th>
				<c:if test="${not inquiry}">	
            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
				</c:if>	
        	</tr>     
          <c:if test="${not inquiry}">	
             <tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<input type="hidden" name="command"/>
                	<kul:htmlControlAttribute property="delegationMember.memberTypeCode" 
                	attributeEntry="${delegationMemberAttributes.memberTypeCode}" 
                	onchange="changeDelegationMemberTypeCode()" disabled="${readOnly}" />
					<NOSCRIPT>
   						<html:submit value="select" alt="press this button to refresh the page after changing the delegation type." />
					</NOSCRIPT>                
	            </div>
            	<c:set var="bo" value="${KualiForm.delegationMemberBusinessObjectName}"/>
            	<c:set var="fc" value="${KualiForm.delegationMemberFieldConversions}"/>
				</td>
                <td class="infoline">   
                <div align="center">
					<kul:htmlControlAttribute property="delegationMember.memberId" attributeEntry="${delegationMemberAttributes.memberId}" readOnly="${readOnly}" />
	               	<kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
				</div>
				</td>
				<td class="infoline">   
                <div align="center">
					<kul:htmlControlAttribute property="delegationMember.memberName" attributeEntry="${delegationMemberAttributes.memberName}" readOnly="true" />
				</div>
				</td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="delegationMember.activeFromDate" attributeEntry="${delegationMemberAttributes.activeFromDate}" datePicker="true" readOnly="${readOnly}" />
                </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="delegationMember.activeToDate" attributeEntry="${delegationMemberAttributes.activeToDate}" datePicker="true" readOnly="${readOnly}" />
                </div>
                </td>
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="delegationMember.qualifier(${qualifier.kimAttributeId}).attrVal"  attributeEntry="${attrEntry}" readOnly="${readOnly}" />
						</div>
					</td>
		        </c:forEach>
                <td align="left" valign="middle" class="infoline">
	                <div align="center">
	                	<input type="hidden" name="command"/>
	                	<kul:htmlControlAttribute property="delegationMember.delegationTypeCode" 
	                	attributeEntry="${delegationMemberAttributes.delegationTypeCode}" disabled="${readOnly}" />
		            </div>
				</td>
                <td class="infoline">
					<div align=center>
						<c:choose>
				        <c:when test="${!readOnly}">
							<html:image property="methodToCall.addDelegationMember.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
						</c:when>
						<c:otherwise>
							<html:image property="methodToCall.addDelegationMember.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton" disabled="true" />
						</c:otherwise>
						</c:choose>
					</div>
                </td>
    	   </tr>         
		</c:if>

      	<c:forEach var="member" items="${KualiForm.document.delegationMembers}" varStatus="statusMember">
            <tr>
				<th rowspan="1" class="infoline" valign="top">
					<c:out value="${statusMember.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].memberTypeCode"  attributeEntry="${delegationMemberAttributes.memberTypeCode}" disabled="true" readOnly="${readOnly}" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].memberId"  attributeEntry="${delegationMemberAttributes.memberId}" readOnly="${readOnly}" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].memberName"  attributeEntry="${delegationMemberAttributes.memberName}" readOnly="${readOnly}" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].activeFromDate"  attributeEntry="${delegationMemberAttributes.activeFromDate}" readOnly="${readOnly}" datePicker="true" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].activeToDate"  attributeEntry="${delegationMemberAttributes.activeToDate}" readOnly="${readOnly}"  datePicker="true" />
					</div>
				</td>
				<c:set var="numberOfColumns" value="${KualiForm.member.numberOfQualifiers+4}"/>
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].qualifier(${qualifier.kimAttributeId}).attrVal"  attributeEntry="${attrEntry}" readOnly="${readOnly}"  />
						</div>
					</td>
		        </c:forEach>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.delegationMembers[${statusMember.index}].delegationTypeCode"  attributeEntry="${delegationMemberAttributes.delegationTypeCode}" readOnly="${readOnly}"/>
					</div>
				</td>
			<c:if test="${not inquiry}">	
				<td>
					<div align=center>&nbsp;
						<c:choose>
							<c:when test="${member.edit or readOnly}">
	        	          		<img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
							</c:when>
	        	       		<c:otherwise>
	        	        		<html:image property='methodToCall.deleteDelegationMember.line${statusMember.index}.anchor${currentTabIndex}'
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