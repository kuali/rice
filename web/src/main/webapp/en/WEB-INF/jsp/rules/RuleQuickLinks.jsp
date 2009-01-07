<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/cal2.js">
    /*
    Xin's Popup calendar script-  Xin Yang (http://www.yxscripts.com/) Script
    featured on/available at http://www.dynamicdrive.com/
    This notice must stay intact for use */
</script>
<script language="JavaScript" src="scripts/cal_conf2.js"></script>
<script language="JavaScript" src="scripts/rule-common.js"></script>
<kul:page headerTitle="Rule QuickLinks" transactionalDocument="false"
	showDocumentInfo="false" htmlFormAction="RuleQuickLinks" docTitle="Rule QuickLinks">
	<c:forEach var="documentTypeStruct" items="${KualiForm.documentTypeQuickLinksStructures}" varStatus="docStatus">
	<c:if test="${documentTypeStruct.shouldDisplay}">
		<c:set var="documentType" value="${documentTypeStruct.documentType}" />
		  <kul:tabTop tabTitle="Document Type ID : ${documentType.documentTypeId}" defaultOpen="true" >              		
           <div class="tab-container" align=center>
           <table width="100%" border=0 cellpadding=0 cellspacing=0
			class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left"  literalLabel="Document Type">
					</kul:htmlAttributeHeaderCell>
				<td width="25%" class="datacell"><a
					href="<c:url value="DocumentType.do">
					<c:param name="docTypeId" value="${documentType.documentTypeId}" />
					<c:param name="methodToCall" value="report"/>
					</c:url>"><c:out value="${documentType.label}" /></a>&nbsp; &nbsp;</td>				
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left" literalLabel="Document Name">
				</kul:htmlAttributeHeaderCell>
				<td class="datacell" colspan="3"><c:out	value="${documentType.name}" />&nbsp;</td>
			</tr>			
			<tr>
              	<td colspan=4>
               		<c:choose>
               			<c:when test="${renderOpened}">
			                 	<a href="<c:url value="DocumentType.do">
								<c:param name="docTypeId" value="${documentType.documentTypeId}" />
								<c:param name="methodToCall" value="report"/>
								</c:url>">
								<kul:htmlAttributeHeaderCell scope="col" align="left">
									<bean-el:message key="${documentType.label}" />
								</kul:htmlAttributeHeaderCell>
						</c:when>
						<c:otherwise>
								<a href="<c:url value="DocumentType.do">
								<c:param name="docTypeId" value="${documentType.documentTypeId}" />
								<c:param name="methodToCall" value="report"/>
								</c:url>"><c:out value="${documentType.label}" />
						</c:otherwise>
					</c:choose>
					</a>&nbsp;
				</td>
              </tr>             
        <c:choose>
			<c:when test="${renderOpened}">
				<tr id="G<c:out value="${docStatus.count}" />"> 
					<kul:htmlAttributeHeaderCell scope="col" align="left"  literalLabel="Document Type">
					</kul:htmlAttributeHeaderCell>
					<td width="25%" class="datacell"><a	href="<c:url value="DocumentType.do">
					<c:param name="docTypeId" value="${documentType.documentTypeId}" />
					<c:param name="methodToCall" value="report"/>
					</c:url>"><c:out value="${documentType.label}" /></a>&nbsp; &nbsp;</td>				
					<td colspan="4" style="border-style: solid; border-width: thin; ">
								<c:set var="documentTypeStruct" value="${documentTypeStruct}" scope="request"/>
								<c:set var="excludeDocId" value="${documentType.documentTypeId}" scope="request" />
								<c:import url="RuleQuickLinksDocumentTypeLinks.jsp" />
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr id="G<c:out value="${docStatus.count}" />" style="display:none">
					<td  colspan=4>
						<kul:tabTop	tabTitle="Document Type ID : ${documentType.documentTypeId}" defaultOpen="true">
							<td colspan="4" style="border-style: solid; border-width: thin; ">
								<c:set var="documentTypeStruct" value="${documentTypeStruct}" scope="request"/>
								<c:set var="excludeDocId" value="${documentType.documentTypeId}" scope="request" />
								<c:import url="RuleQuickLinksDocumentTypeLinks.jsp" />
							</td>		                    
						</kul:tabTop>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
  </div>
  </table>
     </kul:tabTop>		
	</c:if>
	</c:forEach>
</kul:page>