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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.BaseAttributeContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_CNTXT_ATTR_T")
public class ContextAttributeBo extends BaseAttributeBo implements BaseAttributeContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_CNTXT_ATTR_S")
    @GeneratedValue(generator = "KRMS_CNTXT_ATTR_S")
    @Id
    @Column(name = "CNTXT_ATTR_ID")
    private String id;

    @ManyToOne()
    @JoinColumn(name = "CNTXT_ID", referencedColumnName = "CNTXT_ID")
    private ContextBo context;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "ATTR_DEFN_ID", referencedColumnName = "ATTR_DEFN_ID")
    private KrmsAttributeDefinitionBo attributeDefinition;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContextId() {
        if (context != null) {
            return context.getId();
        }

        return null;
    }

    public ContextBo getContext() {
        return context;
    }

    public void setContext(ContextBo context) {
        this.context = context;
    }

    @Override
    public KrmsAttributeDefinitionContract getAttributeDefinition() {
        return attributeDefinition;
    }

    public void setAttributeDefinition(KrmsAttributeDefinitionBo attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

}
