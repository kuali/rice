<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<script language="JavaScript" src="scripts/en-common.js"></script>

<c:set var="groupMemberAttributes" value="${DataDictionary.KimDocumentGroupMember.attributes}" />
<c:set var="groupQualifierAttributes" value="${DataDictionary.KimDocumentGroupQualifier.attributes}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="canAssignGroup" value="${KualiForm.canAssignGroup}" />
<c:if test="${!canAssignGroup}">
	<c:set var="readOnly" value="${!canAssignGroup}"/>
</c:if>

<script language="javaScript">
function changeMemberTypeCode(){
	document.getElementsByTagName("command").value="changeMemberTypeCode";
	javaScript:document.forms[0].submit();
}
</script>
<kul:tab tabTitle="Assignees" defaultOpen="true" tabErrorKey="document.member*">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Assignees</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
        		<th><div align="left">&nbsp</div></th> 
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${groupMemberAttributes.memberTypeCode}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${groupMemberAttributes.memberId}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${groupMemberAttributes.memberName}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${groupMemberAttributes.activeFromDate}" noColon="true" /></div></th>
        		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${groupMemberAttributes.activeToDate}" noColon="true" /></div></th>
				<c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
        			<c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
		        </c:forEach>
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
                	<kul:htmlControlAttribute property="member.memberTypeCode" 
                	attributeEntry="${groupMemberAttributes.memberTypeCode}" 
                	onchange="changeMemberTypeCode()" disabled="${readOnly}" />
					<NOSCRIPT>
   						<html:submit value="select" alt="press this button to refresh the page after changing the delegation type." />
					</NOSCRIPT>                
	            </div>
            	<c:set var="bo" value="${KualiForm.memberBusinessObjectName}"/>
            	<c:set var="fc" value="${KualiForm.memberFieldConversions}"/>
				</td>
                <td class="infoline">   
                <div align="center">             	
					<kul:htmlControlAttribute property="member.memberId" attributeEntry="${groupMemberAttributes.memberId}" readOnly="${readOnly}"/>
					<c:if test="${!readOnly}">
		               	<kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
	               	</c:if>
				</div>
				</td>
                <td class="infoline">   
                <div align="center">             	
					<kul:htmlControlAttribute property="member.memberName" attributeEntry="${groupMemberAttributes.memberName}" readOnly="true" />
				</div>
				</td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="member.activeFromDate" attributeEntry="${groupMemberAttributes.activeFromDate}" datePicker="true" readOnly="${readOnly}" />
                </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="member.activeToDate" attributeEntry="${groupMemberAttributes.activeToDate}" datePicker="true" readOnly="${readOnly}" />
                </div>
                </td>
                <td class="infoline">
					<div align=center>
						<c:choose>
				        <c:when test="${!readOnly}">
							<html:image property="methodToCall.addMember.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
						</c:when>
				        <c:otherwise>
							<html:image property="methodToCall.addMember.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton" disabled="true"/>
						</c:otherwise>
						</c:choose>
					</div>
                </td>
    	   </tr>         
		</c:if>

      	<c:forEach var="member" items="${KualiForm.document.members}" varStatus="statusMember">
            <tr>
				<th rowspan="1" class="infoline" valign="top">
					<c:out value="${statusMember.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberTypeCode"  attributeEntry="${groupMemberAttributes.memberTypeCode}" disabled="true" readOnly="false" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberId"  attributeEntry="${groupMemberAttributes.memberId}" readOnly="${readOnly}" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberName"  attributeEntry="${groupMemberAttributes.memberName}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeFromDate"  attributeEntry="${groupMemberAttributes.activeFromDate}" readOnly="${readOnly}" datePicker="true" />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeToDate"  attributeEntry="${groupMemberAttributes.activeToDate}" readOnly="${readOnly}" datePicker="true" />
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
	        	        		<html:image property='methodToCall.deleteMember.line${statusMember.index}.anchor${currentTabIndex}'
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