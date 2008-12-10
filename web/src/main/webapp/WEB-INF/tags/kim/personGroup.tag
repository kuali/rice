<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docGroupPersonAttributes" value="${DataDictionary.PersonDocumentGroup.attributes}" />
<kul:subtab lookedUpCollectionName="group" width="${tableWidth}" subTabTitle="Groups" noShowHideButton="true">      
        <table cellpadding=0 cellspacing=0 summary="">
          	<tr>
          		<th><div align="left">&nbsp</div></th> 
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupPersonAttributes.groupId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupPersonAttributes.kimTypeId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupPersonAttributes.groupName}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docGroupPersonAttributes.active}" noColon="true" /></div></th>
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	
          	</tr>     
          	
             <tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>

                <td align="left" valign="middle" class="infoline" colspan=2>
                <div align="center">
                	<kul:htmlControlAttribute property="newGroup.groupId" attributeEntry="${docGroupPersonAttributes.groupId}"/>
                	<kul:lookup boClassName="org.kuali.rice.kim.bo.group.impl.KimGroupImpl" fieldConversions="groupId:newGroup.groupId,kimTypeId:newGroup.groupType,groupName:newGroup.groupName" anchor="${tabKey}" />
					</div></td><td><html:hidden property="newGroup.groupName" /></td>
					<html:hidden property="newGroup.groupType" />
				
				
                 <td> <div align=center>
                     <kul:htmlControlAttribute property="newGroup.active" attributeEntry="${docGroupPersonAttributes.active}"/>
                  </div> </td>
                                
                <td class="infoline">
					<div align=center>
	        	     <c:choose>
	        	       <c:when test="${KualiForm.editingMode['populateGroup']}">
	        	          <!-- <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add2.gif' styleClass='tinybutton'/> -->
	        	       </c:when>
	        	       <c:otherwise>
							<html:image property="methodToCall.addGroup.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
	        	       </c:otherwise>
	        	     </c:choose>  
					</div>
                </td>
       </tr>         
            
        	<c:forEach var="group" items="${KualiForm.document.groups}" varStatus="status">
	             <tr>
					<th class="infoline">
						<c:out value="${status.index+1}" />
					</th>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].groupId"  attributeEntry="${docGroupPersonAttributes.groupId}"  readOnly="true" />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].kimGroupType.name"  attributeEntry="${docGroupAttributes.kimGroupType.name}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].groupName"  attributeEntry="${docGroupAttributes.roleName}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.groups[${status.index}].active"  attributeEntry="${docGroupPersonAttributes.active}"  />
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
