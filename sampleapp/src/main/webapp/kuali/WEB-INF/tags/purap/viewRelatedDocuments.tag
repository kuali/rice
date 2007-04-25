<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="notesAttributes" value="${DataDictionary.DocumentNote.attributes}" />


<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Related Documents</h2>
        </div>

		<br />
    
	    <div class="h2-container">
	        <h2>Purchase Order - <a href="">1234</a></h2>
	    </div>
	    <br />
	    <table border="1" cellspacing="0" cellpadding="0" class="datatable">
	        <tr>
	            <th align=center valign=middle class="bord-l-b" width="20%">Date</th>
	            <th align=center valign=middle class="bord-l-b" width="30%">Creator</th>
	            <th align=center valign=middle class="bord-l-b" width="50%">Note Text</th>
	        </tr>
	        <tr>
	            <td align=left valign=middle class="datacell">08/01/2006</td>
	            <td align=left valign=middle class="datacell">Susan Smith</td>
	            <td align=left valign=middle class="datacell">Please rush order.</td>
	        </tr>
	        <tr>
	            <td align=left valign=middle class="datacell">08/03/2006</td>
	            <td align=left valign=middle class="datacell">John Jones</td>
	            <td align=left valign=middle class="datacell">Vendor has been added and order is sent.</td>
	        </tr>
		</table>
		<br />
		<br />
		
	    <div class="h2-container">
	        <h2>Payment Request - <a href="">2222</a></h2>
	    </div>
	    <br />
		
		
	    <table  border="1" cellspacing="0" cellpadding="0" class="datatable">
	        <tr>
	            <th align=center valign=middle class="bord-l-b" width="20%">Date</th>
	            <th align=center valign=middle class="bord-l-b" width="30%">Creator</th>
	            <th align=center valign=middle class="bord-l-b" width="50%">Note Text</th>
	        </tr>
	        <tr>
	            <td align=left valign=middle class="datacell">08/10/2006</td>
	            <td align=left valign=middle class="datacell">Brian Jackson</td>
	            <td align=left valign=middle class="datacell">Processing payment.</td>
	        </tr>
	    </table>
	    <br>
	    <br>
	    
	    <div class="h2-container">
	        <h2>Payment Request - <a href="">4444</a></h2>
	    </div>
	    <br />
	    
		
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Related Documents Section">
			<tr>
				<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.sourceFinancialDocumentTypeCode}"
                	scope="col" />
               	<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.sourceDocumentIdentifier}"
                	scope="col" />
            </tr>
            
	
			<c:forEach var="doc" items="${KualiForm.document.sourceDocumentReferences}" varStatus="status">
	    		<tr>
	    			<td align=left valign=middle class="datacell"> hello </td>
	    			<td align=left valign=middle class="datacell">
	        			
	        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.sourceFinancialDocumentTypeCode}" 
	                		property="document.sourceDocumentReferences[${status.index}].sourceFinancialDocumentTypeCode" readOnly="true"/>
	        		</td>
	        		<td align=left valign=middle class="datacell">
	        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.sourceDocumentIdentifier}" 
	                		property="document.sourceDocumentReferences[${status.index}].sourceDocumentIdentifier" readOnly="true"/>
	        		</td>
	        		
	        	</tr>
	        	<c:forEach var="doc" items="${KualiForm.document.sourceDocumentReferences}" varStatus="status">
	        	
	        	</c:forEach>
        	</c:forEach>
	        	
        	
		</table>
		
		<br/>
		
	    <table cellpadding="0" cellspacing="0" class="datatable" summary="Status History Section">
			<tr>
				<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.sourceFinancialDocumentTypeCode}"
                	scope="col" />
               	<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.sourceDocumentIdentifier}"
                	scope="col" />
            </tr>
            <logic:notEmpty name="KualiForm" property="document.sourceDocumentReferences">
	
				<logic:iterate id="changes" name="KualiForm" property="document.sourceDocumentReferences" indexId="ctr">
				
	    		<tr>
	    			<td align=left valign=middle class="datacell"> hello 2</td>
	    			<td align=left valign=middle class="datacell">
	        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.sourceDocumentReferences.sourceFinancialDocumentTypeCode}" 
	                		property="document.sourceDocumentReferences[${ctr}].sourceFinancialDocumentTypeCode" readOnly="true"/>
	        		</td>
	        		<td align=left valign=middle class="datacell">
	        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.sourceDocumentIdentifier}" 
	                		property="document.sourceDocumentReferences[${ctr}].sourceDocumentIdentifier" readOnly="true"/>
	        		</td>
	        		
	        	</tr>
	        	</logic:iterate>
	        	
        	</logic:notEmpty>
		</table>
	
		
		
		
	    <table border="1" cellspacing="0" cellpadding="0" class="datatable">
	        <tr>
	            <th align=center valign=middle class="bord-l-b">No Notes</th>
	        </tr>
	    </table>
    </div>
</kul:tab>
