<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<script language="JavaScript" src="scripts/en-common.js"></script>

<c:set var="groupMemberAttributes" value="${DataDictionary.KimDocumentGroupMember.attributes}" />
<c:set var="groupQualifierAttributes" value="${DataDictionary.GroupDocumentQualifier.attributes}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="canAssignGroup" value="${KualiForm.canAssignGroup}" />
<c:if test="${!canAssignGroup}">
	<c:set var="readOnly" value="${!canAssignGroup}"/>
</c:if>

<kul:tab tabTitle="Attributes" defaultOpen="true" tabErrorKey="document.qualifier*">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Attribute Data</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
        	<tr>
				<c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
        			<c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
         		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
		        </c:forEach>
        	</tr>     
			<tr>
				<c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
					<c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
        			<c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="document.qualifier(${qualifier.kimAttributeId}).attributeValue"  attributeEntry="${attrEntry}" readOnly="${readOnly}" />
						</div>
					</td>
		        </c:forEach>
		    </tr>
	</table>
	</div>
</kul:tab>