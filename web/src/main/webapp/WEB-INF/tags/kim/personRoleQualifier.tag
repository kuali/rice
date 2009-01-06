<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="roleIdx" required="true" %>
<c:set var="role" value="${KualiForm.document.roles[roleIdx]}"/>
<c:set var="docRolePrncplAttributes" value="${DataDictionary.PersonDocumentRolePrncpl.attributes}" />

                	<kul:subtab lookedUpCollectionName="roleQualifier" width="${tableWidth}" subTabTitle="Role Qualifier">      
        				<table cellpadding=0 cellspacing=0 summary="">
                        <tr>
          				<th><div align="left">&nbsp</div></th> 
		        		<c:forEach var="attrDefn" items="${role.definitions}" varStatus="status">
		        		    <c:set var="attr" value="${attrDefn.value}" />
		        		    
		        			<c:set var="fieldName" value="${attr.name}" />
		        			<c:if test="${empty fieldName}" >
		        			    <c:set var="fieldName" value="${attr.dataDictionaryAttributeDefinition.name}" />
		        			</c:if>
		        			<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
		          		    <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry }" useShortLabel="false" />
		        		</c:forEach>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeFromDate}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeToDate}" noColon="true" /></div></th>
		        		
              			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
                		</tr>
                		
              <c:if test="${not inquiry}">	
                		
		        		<tr>
						<th class="infoline">
							<c:out value="Add:" />
						</th>
		        		<c:forEach var="attrDefn" items="${role.definitions}" varStatus="status1">
		        		    <c:set var="attr" value="${attrDefn.value}" />
		        			<c:set var="fieldName" value="${attr.name}" />
		        			<c:if test="${empty fieldName}" >
		        			    <c:set var="fieldName" value="${attr.dataDictionaryAttributeDefinition.name}" />
		        			</c:if>
		        			<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
			                	<td align="left" valign="middle">
			                	    <!-- this is just a test to put in attributeLabel which we should not do -->
			                		<div align="center"> 
			                		   <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.qualifiers[${status1.index}].attrVal"  attributeEntry="${attrEntry}" />
								   </div>
							</td>
		        		</c:forEach>
		        		
		        		<td>
		        		<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}" datePicker="true"/>
		        		</div>
		        		</td>
		        		<td>
		        		<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].newRolePrncpl.activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}" datePicker="true"/>
		        		</div>
		        		</td>
		        		
		        		<td class="infoline">
							<div align=center>
								<html:image property="methodToCall.addRoleQualifier.line${roleIdx}.anchor${tabKey}"
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
							</div>
                		</td>
		        		</tr> 
		</c:if>        				        		
		        		<!-- use definitions map -->
		           <c:if test="${fn:length(role.rolePrncpls) > 0}">	
		        	 <c:forEach var="rolePrncpl" items="${role.rolePrncpls}" varStatus="status1">
		                <c:if test="${fn:length(rolePrncpl.qualifiers) > 0}">	
		        		<tr>
							<th class="infoline">
								<c:out value="${status1.index+1}" />
							</th>
			        		 <c:forEach var="qualifier" items="${rolePrncpl.qualifiers}" varStatus="status2">
			        			    
				        		  <c:forEach var="attrDefn" items="${role.definitions}" varStatus="status">
				        		   <c:if test="${attrDefn.key == qualifier.qualifierKey}">
				        		    <c:set var="attr" value="${attrDefn.value}" />
				        			<c:set var="fieldName" value="${attr.name}" />
				        			<c:if test="${empty fieldName}" >
				        			    <c:set var="fieldName" value="${attr.dataDictionaryAttributeDefinition.name}" />
				        			</c:if>
				        			<c:set var="attrEntry" value="${role.attributeEntry[fieldName]}" />
				        			    <c:set var="lookupReturn" value="${attr.lookupReturnPropertyConversions}" />
				        			</c:if>    
				        		   </c:forEach>
				                	<td align="left" valign="middle">
				                	    <!-- this is just a test to put in attributeLabel which we should not do -->
				                		<div align="center"> 
				                		   <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].qualifiers[${status2.index}].attrVal"  attributeEntry="${attrEntry}" />
										   <c:if test="${!empty lookupReturn}">
										       <c:set var="fieldConversions" value="${fieldName}:document.roles[${roleIdx}].rolePrncpls[${status1.index}].qualifiers[${status2.index}].attrVal"  />
                								<kul:lookup boClassName="org.kuali.rice.kns.bo.Parameter" fieldConversions="${fieldConversions}" anchor="${tabKey}" />
										   </c:if>
									   </div>
									</td>
								</c:forEach>
								
						<td>
						<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}" datePicker="true"/>
		        		</div>
		        		</td>
		        		<td>
		        		<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}" datePicker="true"/>
		        		</div>
		        		</td>
								
								<td class="infoline">
								<div align=center>
				        	     <c:choose>
				        	       <c:when test="${rolePrncpl.edit}">
				        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
				        	       </c:when>
				        	       <c:otherwise>
				        	          <html:image property='methodToCall.deleteRoleQualifier.line${roleIdx}:${status1.index}.anchor${currentTabIndex}'
											src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
				        	       </c:otherwise>
				        	     </c:choose>  
									</div>
                		   		</td>
					      </tr>
					     </c:if> 
						</c:forEach>
							
		        	</c:if>	
		        		</table>       
					</kul:subtab>
