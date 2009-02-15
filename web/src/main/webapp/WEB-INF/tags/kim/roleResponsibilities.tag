<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="responsibilityAttributes" value="${DataDictionary.ResponsibilityImpl.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${readOnly}">
	<c:set var="inquiry" value="${readOnly}"/>
</c:if>

<kul:tab tabTitle="Responsibilities" defaultOpen="true" tabErrorKey="document.resp*">
	<div class="tab-container" align="center">
    <h3>
    	<span class="subhead-left">Responsibilities</span>
    </h3>
    
    <table cellpadding=0 cellspacing=0 summary="">
          <c:if test="${not inquiry}">	
          	
             <tr>
				<td align="center">
	                <div align="center">
	                	<br/>
						<strong><c:out value="Add Responsibility: (select)" /></strong>
	                	<kul:lookup boClassName="org.kuali.rice.kim.bo.impl.ResponsibilityImpl" fieldConversions=
	                	"template.name:responsibility.kimResponsibility.template.name,responsibilityId:responsibility.responsibilityId,name:responsibility.kimResponsibility.name,namespaceCode:responsibility.kimResponsibility.namespaceCode" anchor="${tabKey}" />
						<html:hidden property="responsibility.responsibilityId" />
						<html:hidden property="responsibility.kimResponsibility.name" />
						<html:hidden property="responsibility.kimResponsibility.namespaceCode" />
						${KualiForm.responsibility.kimResponsibility.namespaceCode}  ${KualiForm.responsibility.kimResponsibility.nameToDisplay}&nbsp;
	                	<br/>
	                	<br/>
		            </div>
				</td>
			</tr>
			<tr>                                
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addResponsibility.anchor${tabKey}"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
                </td>
	       </tr>         
     </c:if>       
	</table>
	<table>
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
      	<c:forEach var="responsibility" items="${KualiForm.document.responsibilities}" varStatus="status">
       	    <c:set var="rows" value="1"/>
       		<c:if test="${responsibility.roleRspAction.roleResponsibilityId!=null}">	
        	       <c:set var="rows" value="2"/>
       		</c:if>        	
            <tr>
				<th rowspan="${rows}" class="infoline" valign="top">
					<c:out value="${status.index+1}" />
				</th>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.namespaceCode"  attributeEntry="${responsibilityAttributes.namespaceCode}" readOnly="true"  />
					</div>
				</td>
	            <td align="left" valign="middle">
	               	<div align="left"> <kul:htmlControlAttribute property="document.responsibilities[${status.index}].kimResponsibility.nameToDisplay"  attributeEntry="${responsibilityAttributes.name}" readOnly="true"  />
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
			<c:if test="${not inquiry}">	
				<td>
					<div align=center>&nbsp;
						<c:choose>
							<c:when test="${role.edit}">
	        	          		<img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
							</c:when>
	        	       		<c:otherwise>
	        	        		<html:image property='methodToCall.deleteResponsibility.line${status.index}.anchor${currentTabIndex}'
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
		        	       	</c:otherwise>
	        	     	</c:choose>  
					</div>
				</td>
			</c:if>    
			</tr>
	        <c:if test="${responsibility.roleRspAction!=null}">	
    			<tr>
	              <td colspan="7" style="padding:0px;">
	              	<kim:responsibilityAction responsibilityIdx="${status.index}" />
		          </td>
		        </tr>
			</c:if>	 
		</c:forEach>        
	</table>
	</div>
</kul:tab>