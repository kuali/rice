<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="roleIdx" required="true" %>
<c:set var="role" value="${KualiForm.document.roles[roleIdx]}"/>
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
		        		
              			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
                		</tr>
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
		        		<td class="infoline">
							<div align=center>
								<html:image property="methodToCall.addRoleQualifier.line${roleIdx}.anchor${tabKey}"
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
							</div>
                		</td>
		        		</tr> 
		        				        		
		        		<!-- use definitions map -->
		           <c:if test="${fn:length(role.rolePrncpls) > 0}">	
		        		<c:forEach var="rolePrncpl" items="${role.rolePrncpls}" varStatus="status1">
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
				        			    
				        			</c:if>    
				        		   </c:forEach>
				                	<td align="left" valign="middle">
				                	    <!-- this is just a test to put in attributeLabel which we should not do -->
				                		<div align="center"> 
				                		   <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[${status1.index}].qualifiers[${status2.index}].attrVal"  attributeEntry="${attrEntry}" />
									   </div>
									</td>
								</c:forEach>
								<td class="infoline">
								<div align=center>
									<html:image property="methodToCall.deleteRoleQualifier.line${roleIdx}:${status1.index}.anchor${tabKey}"
									src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
									</div>
                		   		</td>
					      </tr>
						</c:forEach>
							
		        	</c:if>	
		        		</table>       
					</kul:subtab>
