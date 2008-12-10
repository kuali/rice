<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docEmailAttributes" value="${DataDictionary.PersonDocumentEmail.attributes}" />
<kul:subtab lookedUpCollectionName="email" width="${tableWidth}" subTabTitle="Email Addresses">      
        <table cellpadding=0 cellspacing=0 summary="">
          	<tr>
          		<th><div align="left">&nbsp</div></th> 
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docEmailAttributes.emailTypeCode}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docEmailAttributes.emailAddress}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docEmailAttributes.dflt}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docEmailAttributes.active}" noColon="true" /></div></th>
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	
          	</tr>     
          	
             <tr>
				<th class="infoline">
					<c:out value="Add:" />
				</th>

                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="newEmail.emailTypeCode" attributeEntry="${docEmailAttributes.emailTypeCode}"/>
	            </div>
				</td>
                <td class="infoline">   
                <div align="center">             	
                  <kul:htmlControlAttribute property="newEmail.emailAddress" attributeEntry="${docEmailAttributes.emailAddress}" />
				</div>
				</td>
                <td class="infoline">   
                <div align="center">             	
                  <kul:htmlControlAttribute property="newEmail.dflt" attributeEntry="${docEmailAttributes.dflt}" />
				</div>
				</td>
                <td class="infoline">   
                <div align="center">             	
                  <kul:htmlControlAttribute property="newEmail.active" attributeEntry="${docEmailAttributes.active}" />
				</div>
				</td>
                                
                <td class="infoline">
					<div align=center>
						<html:image property="methodToCall.addEmail.anchor${tabKey}"
						src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
                </td>
       </tr>         
            
        	<c:forEach var="email" items="${KualiForm.document.emails}" varStatus="status">
	             <tr>
					<th class="infoline">
						<c:out value="${status.index+1}" />
					</th>
	                <td align="left" valign="middle">
	                	<div align="center"> <kul:htmlControlAttribute property="document.emails[${status.index}].emailTypeCode"  attributeEntry="${docEmailAttributes.emailTypeCode}"  />
					</div>
					</td>
	                <td>     
	                <div align="center">           	
	                  <kul:htmlControlAttribute property="document.emails[${status.index}].emailAddress" attributeEntry="${docEmailAttributes.emailAddress}" />
					</div>
					</td>
					<td>
	                <div align="center">           	
	                  <kul:htmlControlAttribute property="document.emails[${status.index}].dflt" attributeEntry="${docEmailAttributes.dflt}" />
					</div>
					</td>
					<td>
	                <div align="center">           	
	                  <kul:htmlControlAttribute property="document.emails[${status.index}].active" attributeEntry="${docEmailAttributes.active}" />
					</div>
					</td>
					
					<td>
					<div align=center>&nbsp;
	        	     <c:choose>
	        	       <c:when test="${email.edit}">
	        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
	        	       </c:when>
	        	       <c:otherwise>
	        	          <html:image property='methodToCall.deleteEmail.line${status.index}.anchor${currentTabIndex}'
								src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
	        	       </c:otherwise>
	        	     </c:choose>  
					</div>
	                </td>
	            </tr>
        	</c:forEach>        

            
        </table>
</kul:subtab>
