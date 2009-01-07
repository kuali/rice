<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="roleIdx" required="true" %>
<c:set var="role" value="${KualiForm.document.roles[roleIdx]}"/>
<c:set var="docRolePrncplAttributes" value="${DataDictionary.PersonDocumentRolePrncpl.attributes}" />

               <kul:subtab lookedUpCollectionName="roleActivedates" noShowHideButton="true" width="${tableWidth}" subTabTitle="Active Dates">      
        				<table cellpadding=0 cellspacing=0 summary="">
                        <tr>
                             <th width="5%" rowspan=20 style=border-style:none>&nbsp;</th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeFromDate}" noColon="true" /></div></th>
          		<th><div align="center"><kul:htmlAttributeLabel attributeEntry="${docRolePrncplAttributes.activeToDate}" noColon="true" /></div></th>
                		</tr>
                	<tr>	
						<td>
						<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[0].activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}" datePicker="true"/>
			            </div>
		        		</td>
		        		<td>
		        		<div align="center">
			            <kul:htmlControlAttribute property="document.roles[${roleIdx}].rolePrncpls[0].activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}" datePicker="true"/>
		        		</div>
		        		</td>
		        	</tr>
		        		<tr>
                             <td colspan=4 style="padding:0px; border-style:none; height:22px; background-color:#F6F6F6">&nbsp;</td>
                           </tr>
		        		
		        		</table>       
					</kul:subtab>
