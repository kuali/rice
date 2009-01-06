<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docGroupAttributes" value="${DataDictionary.PersonDocumentGroup.attributes}" />
<kul:subtab lookedUpCollectionName="group" width="${tableWidth}" subTabTitle="Groups" noShowHideButton="true">      
        <table cellpadding=0 cellspacing=0 summary="">
          	<tr>
          		<th><div align="left">&nbsp</div></th> 
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupAttributes.groupId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupAttributes.kimTypeId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupAttributes.groupName}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupAttributes.activeFromDate}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupAttributes.activeToDate}" noColon="true" /></div></th>
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	
          	</tr>     
           <c:if test="${not inquiry}">	
          	
             <tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>

                <td align="left" valign="middle" class="infoline" colspan=3>
                <div align="center">
                	<kul:htmlControlAttribute property="newGroup.groupId" attributeEntry="${docGroupAttributes.groupId}" disabled="true"/>
                	<kul:lookup boClassName="org.kuali.rice.kim.bo.group.impl.KimGroupImpl" fieldConversions="groupId:newGroup.groupId,kimTypeId:newGroup.groupType,groupName:newGroup.groupName,namespaceCode:newGroup.namespaceCode" anchor="${tabKey}" />
					${KualiForm.newGroup.groupName}
					</div></td>
					<html:hidden property="newGroup.groupName" />
					<html:hidden property="newGroup.groupType" />
					<html:hidden property="newGroup.namespaceCode" />
				
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="newGroup.activeFromDate"  attributeEntry="${docGroupAttributes.activeFromDate}"  datePicker="true"/>
					</div>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="newGroup.activeToDate"  attributeEntry="${docGroupAttributes.activeToDate}"  datePicker="true"/>
					</div>
					</td>
				                                
                <td class="infoline">
					<div align=center>
						<html:image property="methodToCall.addGroup.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
                </td>
       </tr>         
    </c:if>        
        	<c:forEach var="group" items="${KualiForm.document.groups}" varStatus="status">
	             <tr>
					<th class="infoline">
						<c:out value="${status.index+1}" />
					</th>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].groupId"  attributeEntry="${docGroupAttributes.groupId}"  readOnly="true" />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].kimGroupType.name"  attributeEntry="${docGroupAttributes.kimGroupType.name}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].groupName"  attributeEntry="${docGroupAttributes.groupName}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].activeFromDate"  attributeEntry="${docGroupAttributes.activeFromDate}" datePicker="true"/>
					</div>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].activeToDate"  attributeEntry="${docGroupAttributes.activeToDate}" datePicker="true"/>
					</div>
					</td>
					
					<td>
					<div align=center>&nbsp;
	        	     <c:choose>
	        	       <c:when test="${group.edit}">
	        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
	        	       </c:when>
	        	       <c:otherwise>
	        	          <html:image property='methodToCall.deleteGroup.line${status.index}.anchor${currentTabIndex}'
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
	        	       </c:otherwise>
	        	     </c:choose>  
					</div>
	                </td>
	            </tr>
        	</c:forEach>        

            
        </table>
</kul:subtab>
