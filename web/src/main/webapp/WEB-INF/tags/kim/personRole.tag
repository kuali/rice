<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docRoleAttributes" value="${DataDictionary.PersonDocumentRole.attributes}" />
<c:set var="docRolePrncplAttributes" value="${DataDictionary.KimDocumentRoleMember.attributes}" />
<kul:subtab lookedUpCollectionName="role" width="${tableWidth}" subTabTitle="Roles" noShowHideButton="true">      
        <table cellpadding=0 cellspacing=0 summary="">
          	<tr>
          		<th>&nbsp;</th> 
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRoleAttributes.roleId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRoleAttributes.kimTypeId}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRoleAttributes.roleName}" noColon="true" /></div></th>
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
                	<kul:htmlControlAttribute property="newRole.roleId" attributeEntry="${docRoleAttributes.roleId}" disabled="true"/>
                	<kul:lookup boClassName="org.kuali.rice.kim.bo.role.impl.KimRoleImpl" fieldConversions="roleId:newRole.roleId,kimTypeId:newRole.kimTypeId,roleName:newRole.roleName,namespaceCode:newRole.namespaceCode,kimRoleType.name:newRole.kimRoleType.name,kimRoleType.kimTypeServiceName:newRole.kimRoleType.kimTypeServiceName" anchor="${tabKey}" />
					
					<html:hidden property="newRole.roleName" />
					<html:hidden property="newRole.namespaceCode" />
					<html:hidden property="newRole.kimTypeId" />
					<html:hidden property="newRole.kimRoleType.name" />
					<html:hidden property="newRole.kimRoleType.kimTypeServiceName" />
	            </div>
				</td>
				<td>&nbsp;</td>
				<td>${KualiForm.newRole.roleName}&nbsp;</td>
                                
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addRole.anchor${tabKey}"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
                </td>
       </tr>         
     </c:if>       
        	<c:forEach var="role" items="${KualiForm.document.roles}" varStatus="status">
        	    <c:set var="rows" value="2"/>
        		<c:if test="${empty role.definitions and fn:length(role.rolePrncpls[0].roleRspActions) > 0}">	
         	       <c:set var="rows" value="3"/>       		
        		</c:if>
        	
	             <tr>
					<th rowspan="${rows}" class="infoline">
						<c:out value="${status.index+1}" />
					</th>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.roles[${status.index}].roleId"  attributeEntry="${docRoleAttributes.roleId}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.roles[${status.index}].kimRoleType.name"  attributeEntry="${docRoleAttributes.kimGroupType.name}" readOnly="true"  />
					</div>
					</td>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.roles[${status.index}].roleName"  attributeEntry="${docRoleAttributes.roleName}" readOnly="true"  />
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
	        	          <html:image property='methodToCall.deleteRole.line${status.index}.anchor${currentTabIndex}'
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
	        	       </c:otherwise>
	        	     </c:choose>  
					</div>
	                </td>
	           </c:if>     
	            </tr>
		      <c:choose>
	            <c:when test="${!empty role.definitions  and fn:length(role.definitions) > 0}" >
	            	<tr>
		              <td colspan="5" style="padding:0px;">
		              	<kim:personRoleQualifier roleIdx="${status.index}" role="${role}" />
			          </td>
			        </tr>
 		        </c:when>
		        <c:otherwise>
     			    <tr>
		              <td colspan="5" style="padding:0px;">
		              	<kim:personRoleActiveDates roleIdx="${status.index}" />
			          </td>
			        </tr>
		          <c:if test="${fn:length(role.rolePrncpls[0].roleRspActions) > 0}">	
     			    <tr>
		              <td colspan="5" style="padding:0px;">
		              	<kim:roleResponsibilityAction roleIdx="${status.index}" mbrIdx="0" />
			          </td>
			        </tr>
				</c:if>	      
			        
			        
 		        </c:otherwise>
		      </c:choose>
       	</c:forEach>        

            
        </table>
</kul:subtab>
