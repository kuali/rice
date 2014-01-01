/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sampleu.bookstore.document.attribs;

import org.kuali.rice.kew.api.KewApiConstants;

/**
 * Searchable attribute for book title
 * TODO: probably needs to be wired up to extension repository for remote invocation
 *
 * <documentContent>
   <applicationContent>
   <org.kuali.rice.krad.workflow.KualiDocumentXmlMaterializer>
   ...
   <newMaintainableObject class="edu.sampleu.bookstore.maintenance.BookMaintainable">
      <businessObject class="edu.sampleu.bookstore.bo.Book">
        <title>asdfasdf</title>
        <typeCode>ROM</typeCode>
        <isbn/>
        <publisher>asfdasdd</publisher>
        <price>
          <value>1212.00</value>
        </price>
        <rating>
          <value>1</value>
        </rating>
        <authors>
          <edu.sampleu.bookstore.bo.Author>
            <authorName>asdfasdf</authorName>
            <email>asdfasdf@gmailc.om</email>
            <phoneNbr>123-123-1234</phoneNbr>
            <active>true</active>
            <addresses/>
            <books/>
            <newCollectionRecord>false</newCollectionRecord>
            <extension class="edu.sampleu.bookstore.bo.Account">
              <bankName>asdfasdf</bankName>
              <accountNumber>12345</accountNumber>
              <newCollectionRecord>false</newCollectionRecord>
            </extension>
          </edu.sampleu.bookstore.bo.Author>
        </authors>
        <newCollectionRecord>false</newCollectionRecord>
      </businessObject>
      <newCollectionLines/>
      <inactiveRecordDisplay/>
      <newCollectionLineNames/>
      <documentNumber>3020</documentNumber>
      <dataObject class="edu.sampleu.bookstore.bo.Book" reference="../businessObject"/>
      <dataObjectClass>edu.sampleu.bookstore.bo.Book</dataObjectClass>
      <maintenanceAction>New</maintenanceAction>
    </newMaintainableObject>
    ..
 */
public class BookTitleSearchableAttribute extends XPathSearchableAttribute {
    public BookTitleSearchableAttribute() {
        super("book_title", KewApiConstants.SearchableAttributeConstants.DATA_TYPE_STRING, "//newMaintainableObject/businessObject/title/text()", "Book Title");
    }
}