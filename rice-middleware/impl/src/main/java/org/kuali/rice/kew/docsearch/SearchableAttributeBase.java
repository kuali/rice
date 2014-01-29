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
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
        @NamedQuery(name="SearchableAttributeValue.HasSearchableAttributeValue",
                query = "SELECT sa from SearchableAttributeBase sa WHERE "
                        + "(sa.searchableAttributeKey = :searchableAttributeKey AND sa.documentId = :documentId)"),
        @NamedQuery(name="SearchableAttributeValue.FindSearchableAttributesByDocumentId",
        query = "SELECT sa FROM SearchableAttributeBase sa WHERE sa.documentId = :documentId")
})
public abstract class SearchableAttributeBase implements SearchableAttributeValue {

    @Id
    @GeneratedValue(generator = "KREW_SRCH_ATTR_S")
    @PortableSequenceGenerator(name = "KREW_SRCH_ATTR_S")
    @Column(name="DOC_HDR_EXT_ID")
    private String searchableAttributeValueId;

    @Column(name="KEY_CD")
    private String searchableAttributeKey;

    @Column(name="DOC_HDR_ID")
    private String documentId;
    @ManyToOne(fetch= FetchType.EAGER, cascade={CascadeType.PERSIST})
    @JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
    private DocumentRouteHeaderValue routeHeader;

    @Transient
    protected String ojbConcreteClass; // attribute needed for OJB polymorphism - do not alter!

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String getSearchableAttributeValueId() {
        return searchableAttributeValueId;
    }

    @Override
    public void setSearchableAttributeValueId(String searchableAttributeValueId) {
        this.searchableAttributeValueId = searchableAttributeValueId;
    }

    @Override
    public String getSearchableAttributeKey() {
        return searchableAttributeKey;
    }

    @Override
    public void setSearchableAttributeKey(String searchableAttributeKey) {
        this.searchableAttributeKey = searchableAttributeKey;
    }

    @Override
    public String getOjbConcreteClass() {
        return ojbConcreteClass;
    }

    @Override
    public void setOjbConcreteClass(String ojbConcreteClass) {
        this.ojbConcreteClass = ojbConcreteClass;
    }

    @Override
    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    @Override
    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

}
